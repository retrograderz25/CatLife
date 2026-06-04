package hust.hedspi.oop.game.minigames.trom_meo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.I18NBundle;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.managers.StoryManager;
import hust.hedspi.oop.game.minigames.IMinigameStrategy;
import hust.hedspi.oop.game.utils.Constants;
import hust.hedspi.oop.game.utils.MinigameID;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Minigame "Trom Meo" (THIEF_HIDE).
 *
 * Người chơi điều khiển mèo né tránh các bẫy (trap) bắn ra từ Boss ở trung tâm.
 * Sống sót trong 45 giây để thắng.
 */
public class TromMeoMinigame implements IMinigameStrategy {

    private static final String ASSET_BASE = "minigames/trom_meo/";
    private static final float GAME_DURATION = 45f;

    private static final float CAT_SPEED = 160f;
    private static final float CAT_RUN_SPEED = 280f;
    private static final float CAT_DISPLAY_SIZE = 96f;
    private static final float CAT_HITBOX_RADIUS = 20f;

    private static final int FRAME_CELL = 80;
    private static final int IDLE_FRAMES = 8;
    private static final int WALK_FRAMES = 12;
    private static final int RUN_FRAMES = 8;
    private static final float IDLE_FRAME_DUR = 0.12f;
    private static final float WALK_FRAME_DUR = 0.075f;
    private static final float RUN_FRAME_DUR = 0.06f;

    private enum AnimState { IDLE, WALK, RUN }

    // ── Trap struct (formerly PoisonBall) ──────────────────────────────────────
    private static class Trap {
        float x, y;
        float vx, vy;
        float[] prevX = new float[6];
        float[] prevY = new float[6];
        int prevCount = 0;

        Trap(float x, float y, float vx, float vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }

        void update(float dt) {
            // Trail history
            for (int i = prevX.length - 1; i > 0; i--) {
                prevX[i] = prevX[i - 1];
                prevY[i] = prevY[i - 1];
            }
            prevX[0] = x;
            prevY[0] = y;
            if (prevCount < prevX.length) prevCount++;

            // Move
            x += vx * dt;
            y += vy * dt;
        }
    }

    // ── Textures ───────────────────────────────────────────────────────────────
    private Texture bgTexture, bossTexture, trapTexture, timeFrameTexture, dimTexture;
    private Texture idleTexture, walkTexture, runTexture;

    // ── Animations ───────────────────────────────────────────────────────────────
    private Animation<TextureRegion> idleAnim, walkAnim, runAnim;
    private AnimState animState;
    private float stateTime;

    private int screenW, screenH;

    private float catX, catY;
    private boolean facingLeft;

    private float bossX, bossY;
    private float bossRadius;
    private float bossRotation;
    private float bossShootFlash;

    private final ArrayList<Trap> traps = new ArrayList<>();
    private float spawnTimer;

    private float timer;
    private boolean gameOver, won, exitRequested;

    private BitmapFont hudFont, dialogFont;
    private I18NBundle bundle;

    @Override
    public void start() {
        screenW = hust.hedspi.oop.game.utils.Constants.VIRTUAL_WIDTH;
        screenH = hust.hedspi.oop.game.utils.Constants.VIRTUAL_HEIGHT;

        loadAssets();

        hudFont = ResourceManager.getInstance().hudFont;
        dialogFont = ResourceManager.getInstance().dialogFont;
        bundle = ResourceManager.getInstance().getBundle();

        catX = 150f;
        catY = 150f;
        facingLeft = false;
        animState = AnimState.IDLE;
        stateTime = 0f;

        bossX = screenW / 2f;
        bossY = screenH / 2f;
        bossRadius = 60f;
        bossRotation = 0f;
        bossShootFlash = 0f;

        traps.clear();
        spawnTimer = 0f;
        timer = 0f;
        gameOver = false;
        won = false;
        exitRequested = false;
    }

    private void loadAssets() {
        bgTexture = new Texture(Gdx.files.internal(ASSET_BASE + "background.png"));
        bossTexture = new Texture(Gdx.files.internal(ASSET_BASE + "boss.png"));
        trapTexture = new Texture(Gdx.files.internal(ASSET_BASE + "trap.png"));
        timeFrameTexture = new Texture(Gdx.files.internal(ASSET_BASE + "timeframe.png"));

        idleTexture = new Texture(Gdx.files.internal(ASSET_BASE + "orange/IDLE.png"));
        walkTexture = new Texture(Gdx.files.internal(ASSET_BASE + "orange/WALK.png"));
        runTexture = new Texture(Gdx.files.internal(ASSET_BASE + "orange/RUN.png"));

        idleAnim = buildAnim(idleTexture, IDLE_FRAMES, IDLE_FRAME_DUR);
        walkAnim = buildAnim(walkTexture, WALK_FRAMES, WALK_FRAME_DUR);
        runAnim = buildAnim(runTexture, RUN_FRAMES, RUN_FRAME_DUR);

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        dimTexture = new Texture(pix);
        pix.dispose();
    }

    private Animation<TextureRegion> buildAnim(Texture tex, int frameCount, float frameDur) {
        int frameH = tex.getHeight();
        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = new TextureRegion(tex, i * FRAME_CELL, 0, FRAME_CELL, frameH);
        }
        Animation<TextureRegion> anim = new Animation<>(frameDur, frames);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        return anim;
    }

    @Override
    public void update(float dt) {
        if (gameOver) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                exitRequested = true;
            }
            return;
        }

        timer += dt;
        if (timer >= GAME_DURATION) {
            endGame(true);
            return;
        }

        // Player movement
        boolean running = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        float speed = running ? CAT_RUN_SPEED : CAT_SPEED;
        float dx = 0f, dy = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            dx -= speed * dt;
            facingLeft = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            dx += speed * dt;
            facingLeft = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            dy += speed * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            dy -= speed * dt;
        }
        boolean moving = dx != 0 || dy != 0;
        catX = MathUtils.clamp(catX + dx, CAT_DISPLAY_SIZE / 3f, screenW - CAT_DISPLAY_SIZE / 3f);
        catY = MathUtils.clamp(catY + dy, CAT_DISPLAY_SIZE / 3f, screenH - CAT_DISPLAY_SIZE / 3f);

        AnimState targetAnim = !moving ? AnimState.IDLE : (running ? AnimState.RUN : AnimState.WALK);
        if (targetAnim != animState) {
            animState = targetAnim;
            stateTime = 0f;
        }
        stateTime += dt;

        // Spawn traps
        spawnTimer += dt;
        float currentSpawnInterval = Math.max(0.18f, 0.72f - (timer / GAME_DURATION) * 0.58f);
        if (spawnTimer >= currentSpawnInterval) {
            spawnTimer = 0f;
            bossShootFlash = 1f;
            int count;
            if (timer > 30f) count = MathUtils.random(3, 4);
            else if (timer > 15f) count = MathUtils.random(2, 3);
            else count = MathUtils.random(1, 2);
            for (int i = 0; i < count; i++) {
                float angle = MathUtils.random(0f, 360f);
                float speedBullet = MathUtils.random(160f, 260f);
                float vx = MathUtils.cosDeg(angle) * speedBullet;
                float vy = MathUtils.sinDeg(angle) * speedBullet;
                float offset = bossRadius * 0.7f;
                float sx = bossX + MathUtils.cosDeg(angle) * offset;
                float sy = bossY + MathUtils.sinDeg(angle) * offset;
                traps.add(new Trap(sx, sy, vx, vy));
            }
        }

        // Update traps and remove off-screen
        Iterator<Trap> it = traps.iterator();
        while (it.hasNext()) {
            Trap t = it.next();
            t.update(dt);
            float margin = 80f;
            if (t.x < -margin || t.x > screenW + margin || t.y < -margin || t.y > screenH + margin) {
                it.remove();
                continue;
            }
            // Collision with cat
            float distToCat = Vector2.dst(catX, catY, t.x, t.y);
            float trapRadius = 12f;
            if (distToCat < CAT_HITBOX_RADIUS + trapRadius) {
                endGame(false);
                return;
            }
        }

        // Collision with boss
        if (Vector2.dst(catX, catY, bossX, bossY) < CAT_HITBOX_RADIUS + bossRadius) {
            endGame(false);
            return;
        }
    }

    private void endGame(boolean playerWon) {
        won = playerWon;
        gameOver = true;
        StoryManager.getInstance().recordResult(MinigameID.THIEF_HIDE, won);
    }

    @Override
    public void render(SpriteBatch batch) {
        // Background
        batch.draw(bgTexture, 0, 0, screenW, screenH);

        // Traps with trail effect
        float trapSize = 28f;
        for (Trap t : traps) {
            for (int i = t.prevCount - 1; i >= 0; i--) {
                float alpha = 0.35f * (1f - (float) i / t.prevX.length);
                batch.setColor(0.9f, 0.6f, 0.2f, alpha);
                batch.draw(trapTexture, t.prevX[i] - trapSize / 2f, t.prevY[i] - trapSize / 2f, trapSize, trapSize);
            }
            batch.setColor(Color.WHITE);
            batch.draw(trapTexture, t.x - trapSize / 2f, t.y - trapSize / 2f, trapSize, trapSize);
        }

        // Boss (no rotation per user request)
        float bossPulse = 1.0f + 0.06f * MathUtils.sin(stateTime * 5f);
        if (bossShootFlash > 0f) bossPulse += bossShootFlash * 0.08f;
        float bW = bossTexture.getWidth() * bossPulse * 1.2f;
        float bH = bossTexture.getHeight() * bossPulse * 1.2f;
        if (bossShootFlash > 0f) batch.setColor(1f, 1f - bossShootFlash * 0.4f, 1f - bossShootFlash * 0.4f, 1f);
        batch.draw(bossTexture, bossX - bW / 2f, bossY - bH / 2f, bW / 2f, bH / 2f, bW, bH, 1f, 1f, bossRotation, 0, 0, bossTexture.getWidth(), bossTexture.getHeight(), false, false);
        batch.setColor(Color.WHITE);

        // Cat
        float hs = CAT_DISPLAY_SIZE / 2f;
        Animation<TextureRegion> curAnim = switch (animState) {
            case RUN -> runAnim;
            case WALK -> walkAnim;
            default -> idleAnim;
        };
        TextureRegion frame = curAnim.getKeyFrame(stateTime);
        if (!facingLeft && !frame.isFlipX()) frame.flip(true, false);
        if (facingLeft && frame.isFlipX()) frame.flip(true, false);
        batch.draw(frame, catX - hs, catY - hs, CAT_DISPLAY_SIZE, CAT_DISPLAY_SIZE);

        // Timeframe UI
        float tfScale = 3f;
        float tfW = timeFrameTexture.getWidth() * tfScale;
        float tfH = timeFrameTexture.getHeight() * tfScale;
        float tfX = screenW - tfW - 15f;
        float tfY = screenH - tfH - 15f;
        float timeLeft = Math.max(0f, GAME_DURATION - timer);
        float shakeX = 0f, shakeY = 0f;
        if (timeLeft < 10f && !gameOver) {
            shakeX = MathUtils.random(-3f, 3f);
            shakeY = MathUtils.random(-3f, 3f);
        }
        batch.draw(timeFrameTexture, tfX + shakeX, tfY + shakeY, tfW, tfH);
        hudFont.setColor(timeLeft < 10f ? Color.RED : Color.WHITE);
        hudFont.draw(batch, String.format("%.0f", timeLeft) + bundle.get("trom_meo_time_unit"), tfX + 8f + shakeX, tfY + tfH * 0.85f + shakeY);
        hudFont.setColor(Color.WHITE);
        dialogFont.draw(batch, bundle.get("trom_meo_ctrl_hint"), tfX - 100f + shakeX, tfY - 10f + shakeY);

        // Game Over overlay
        if (gameOver) {
            renderGameOver(batch);
        }
    }

    private void renderGameOver(SpriteBatch batch) {
        batch.setColor(0f, 0f, 0f, 0.72f);
        batch.draw(dimTexture, 0, 0, screenW, screenH);
        batch.setColor(Color.WHITE);
        float panelW = screenW * 0.42f;
        float panelH = screenH * 0.4f;
        float panelX = (screenW - panelW) / 2f;
        float panelY = (screenH - panelH) / 2f;
        batch.draw(timeFrameTexture, panelX, panelY, panelW, panelH);
        hudFont.setColor(won ? Color.GREEN : Color.RED);
        hudFont.draw(batch, bundle.get(won ? "trom_meo_win" : "trom_meo_lose"), panelX + 25f, panelY + panelH * 0.82f);
        hudFont.setColor(Color.WHITE);
        dialogFont.draw(batch, bundle.get("trom_meo_exit_hint"), panelX + 25f, panelY + panelH * 0.35f);
    }

    @Override
    public boolean isFinished() {
        return exitRequested;
    }

    @Override
    public boolean isWon() {
        return won;
    }

    @Override
    public void dispose() {
        safeDispose(bgTexture);
        safeDispose(bossTexture);
        safeDispose(trapTexture);
        safeDispose(timeFrameTexture);
        safeDispose(dimTexture);
        safeDispose(idleTexture);
        safeDispose(walkTexture);
        safeDispose(runTexture);
    }

    private void safeDispose(Texture t) {
        if (t != null) t.dispose();
    }

    @Override
    public void forceEnd(boolean win) {
        this.won = win;
        this.gameOver = true;
        hust.hedspi.oop.game.managers.StoryManager.getInstance().recordResult(MinigameID.THIEF_HIDE, win);
    }
}
