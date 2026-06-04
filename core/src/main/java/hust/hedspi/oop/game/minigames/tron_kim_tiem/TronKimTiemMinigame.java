package hust.hedspi.oop.game.minigames.tron_kim_tiem;

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
 * Minigame "Trốn Kim Tiêm" (PET_ESCAPE_VET).
 *
 * Nhiệm vụ người chơi: Điều khiển mèo né tránh các đạn độc (poisonball) bắn ra từ Boss ở trung tâm.
 * Sống sót trong vòng 45 giây để giành chiến thắng.
 * Trực tiếp va chạm với Boss hoặc Poisonball sẽ thất bại ngay lập tức.
 */
public class TronKimTiemMinigame implements IMinigameStrategy {

    private static final String ASSET_BASE = "minigames/tron_kim_tiem/";
    private static final float GAME_DURATION = 45f; // Sống sót trong 45 giây

    private static final float CAT_SPEED = 160f; // Tốc độ di chuyển đi bộ
    private static final float CAT_RUN_SPEED = 280f; // Tốc độ di chuyển chạy nhanh
    private static final float CAT_DISPLAY_SIZE = 96f; // Kích thước hiển thị của mèo
    private static final float CAT_HITBOX_RADIUS = 20f; // Bán kính va chạm của mèo

    private static final int FRAME_CELL = 80;
    private static final int IDLE_FRAMES = 8;
    private static final int WALK_FRAMES = 12;
    private static final int RUN_FRAMES = 8;
    private static final float IDLE_FRAME_DUR = 0.12f;
    private static final float WALK_FRAME_DUR = 0.075f;
    private static final float RUN_FRAME_DUR = 0.06f;

    private enum AnimState { IDLE, WALK, RUN }

    // ── Poison Ball Struct ──────────────────────────────────────────────────
    private static class PoisonBall {
        float x, y;
        float vx, vy;
        float[] prevX = new float[6];
        float[] prevY = new float[6];
        int prevCount = 0;

        PoisonBall(float x, float y, float vx, float vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }

        void update(float dt) {
            // Shift trail history for premium ghost trail effect
            for (int i = prevX.length - 1; i > 0; i--) {
                prevX[i] = prevX[i - 1];
                prevY[i] = prevY[i - 1];
            }
            prevX[0] = x;
            prevY[0] = y;
            if (prevCount < prevX.length) {
                prevCount++;
            }

            // Move
            x += vx * dt;
            y += vy * dt;
        }
    }

    // ── Textures ─────────────────────────────────────────────────────────────
    private Texture bgTexture, bossTexture, poisonBallTexture, timeFrameTexture, dimTexture;
    private Texture idleTexture, walkTexture, runTexture;

    // ── Animations ────────────────────────────────────────────────────────────
    private Animation<TextureRegion> idleAnim, walkAnim, runAnim;
    private AnimState animState;
    private float stateTime;

    // ── Screen size ──────────────────────────────────────────────────────────
    private int screenW, screenH;

    // ── Entities State ───────────────────────────────────────────────────────
    private float catX, catY;
    private boolean facingLeft;

    private float bossX, bossY;
    private float bossRadius;
    private float bossRotation;
    private float bossShootFlash; // Effect when shooting

    private final ArrayList<PoisonBall> poisonBalls = new ArrayList<>();
    private float spawnTimer;

    // ── Game State ───────────────────────────────────────────────────────────
    private float timer;
    private boolean gameOver, won, exitRequested;

    // ── UI ───────────────────────────────────────────────────────────────────
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

        // Mèo xuất hiện ở góc dưới bên trái, cách xa Boss ở trung tâm
        catX = 150f;
        catY = 150f;
        facingLeft = false;
        animState = AnimState.IDLE;
        stateTime = 0f;

        // Boss ở trung tâm tuyệt đối
        bossX = screenW / 2f;
        bossY = screenH / 2f;
        bossRadius = 60f; // Bán kính va chạm của Boss
        bossRotation = 0f;
        bossShootFlash = 0f;

        poisonBalls.clear();
        spawnTimer = 0f;
        timer = 0f;
        gameOver = false;
        won = false;
        exitRequested = false;
    }

    private void loadAssets() {
        bgTexture = new Texture(Gdx.files.internal(ASSET_BASE + "background.png"));
        bossTexture = new Texture(Gdx.files.internal(ASSET_BASE + "boss.png"));
        poisonBallTexture = new Texture(Gdx.files.internal(ASSET_BASE + "poisonball.png"));
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
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                exitRequested = true;
            }
            if (Gdx.input.justTouched()) {
                com.badlogic.gdx.math.Vector2 mousePos = hust.hedspi.oop.game.screens.MinigameScreen.unproject(Gdx.input.getX(), Gdx.input.getY());
                float btnW = 160f;
                float btnH = 50f;
                float btnX = (screenW - btnW) / 2f;
                float btnY = ((screenH - screenH * 0.45f) / 2f) + 40f;
                if (mousePos.x >= btnX && mousePos.x <= btnX + btnW && mousePos.y >= btnY && mousePos.y <= btnY + btnH) {
                    exitRequested = true;
                }
            }
            return;
        }

        timer += dt;
        if (timer >= GAME_DURATION) {
            endGame(true);
            return;
        }

        // ── 1. Người chơi di chuyển ──────────────────────────────────────────
        boolean running = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
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

        boolean moving = (dx != 0 || dy != 0);
        catX += dx;
        catY += dy;

        // Giới hạn trong màn hình (có trừ bán kính lề)
        float boundPadding = CAT_DISPLAY_SIZE / 3f;
        catX = MathUtils.clamp(catX, boundPadding, screenW - boundPadding);
        catY = MathUtils.clamp(catY, boundPadding, screenH - boundPadding);

        // Cập nhật trạng thái hoạt ảnh mèo
        AnimState targetAnim = !moving ? AnimState.IDLE : (running ? AnimState.RUN : AnimState.WALK);
        if (targetAnim != animState) {
            animState = targetAnim;
            stateTime = 0f;
        }
        stateTime += dt;

        // ── 2. Boss hoạt ảnh xoay & nhấp nhô ────────────────────────────────────
        if (bossShootFlash > 0f) {
            bossShootFlash -= dt * 4f; // Hiệu ứng lóe sáng phai nhanh
        }

        // ── 3. Spawn đạn độc (tần suất & số lượng tăng dần) ────────────────────
        spawnTimer += dt;
        float currentSpawnInterval = Math.max(0.18f, 0.72f - (timer / GAME_DURATION) * 0.58f);
        if (spawnTimer >= currentSpawnInterval) {
            spawnTimer = 0f;
            bossShootFlash = 1f; // Boss sáng bừng lên khi bắn

            // Xác định số đạn bắn cùng lúc (Tăng đáng kể mật độ)
            int count = 1;
            if (timer > 30f) {
                count = MathUtils.random(3, 4); // Cuối trận bắn 3-4 viên
            } else if (timer > 15f) {
                count = MathUtils.random(2, 3); // Giữa trận bắn 2-3 viên
            } else {
                count = MathUtils.random(1, 2); // Đầu trận bắn 1-2 viên
            }

            for (int i = 0; i < count; i++) {
                float angle = MathUtils.random(0f, 360f);
                float bulletSpeed = MathUtils.random(160f, 260f);
                float vx = MathUtils.cosDeg(angle) * bulletSpeed;
                float vy = MathUtils.sinDeg(angle) * bulletSpeed;

                // Xuất phát cách Boss một khoảng nhỏ cho tự nhiên
                float offsetDistance = bossRadius * 0.7f;
                float sx = bossX + MathUtils.cosDeg(angle) * offsetDistance;
                float sy = bossY + MathUtils.sinDeg(angle) * offsetDistance;

                poisonBalls.add(new PoisonBall(sx, sy, vx, vy));
            }
        }

        // ── 4. Cập nhật đạn độc & xóa đạn bay ngoài màn hình ───────────────────
        Iterator<PoisonBall> it = poisonBalls.iterator();
        while (it.hasNext()) {
            PoisonBall ball = it.next();
            ball.update(dt);

            // Kiểm tra ra khỏi màn hình
            float margin = 80f;
            if (ball.x < -margin || ball.x > screenW + margin || ball.y < -margin || ball.y > screenH + margin) {
                it.remove();
                continue;
            }

            // Kiểm tra va chạm với mèo
            float distToCat = Vector2.dst(catX, catY, ball.x, ball.y);
            float ballRadius = 12f; // Bán kính va chạm poisonball
            if (distToCat < CAT_HITBOX_RADIUS + ballRadius) {
                endGame(false);
                return;
            }
        }

        // ── 5. Kiểm tra va chạm giữa mèo và Boss ở tâm ─────────────────────────
        float distToBoss = Vector2.dst(catX, catY, bossX, bossY);
        if (distToBoss < CAT_HITBOX_RADIUS + bossRadius) {
            endGame(false);
            return;
        }
    }

    private void endGame(boolean playerWon) {
        won = playerWon;
        gameOver = true;
        StoryManager.getInstance().recordResult(MinigameID.PET_ESCAPE_VET, won);
    }

    @Override
    public void render(SpriteBatch batch) {
        // ── 1. Vẽ background trải rộng toàn màn hình ──────────────────────────
        batch.draw(bgTexture, 0, 0, screenW, screenH);

        // ── 2. Vẽ các đạn độc kèm hiệu ứng bóng mờ (trail) premium ─────────────
        float ballSize = 28f;
        for (PoisonBall ball : poisonBalls) {
            // Vẽ các bóng mờ phía sau
            for (int i = ball.prevCount - 1; i >= 0; i--) {
                float alpha = 0.35f * (1f - (float) i / ball.prevX.length);
                batch.setColor(0.6f, 1.0f, 0.6f, alpha); // Ánh lục mờ ảo
                batch.draw(poisonBallTexture, ball.prevX[i] - ballSize / 2f, ball.prevY[i] - ballSize / 2f, ballSize, ballSize);
            }
            batch.setColor(Color.WHITE);

            // Vẽ viên đạn độc chính
            batch.draw(poisonBallTexture, ball.x - ballSize / 2f, ball.y - ballSize / 2f, ballSize, ballSize);
        }

        // ── 3. Vẽ Boss ở trung tâm có thở phập phồng (pulse) & xoay tròn ────────
        float bossPulse = 1.0f + 0.06f * MathUtils.sin(stateTime * 5f);
        if (bossShootFlash > 0f) {
            bossPulse += bossShootFlash * 0.08f; // Giật nhẹ khi bắn
        }
        float bW = bossTexture.getWidth() * bossPulse * 1.2f;
        float bH = bossTexture.getHeight() * bossPulse * 1.2f;

        // Nếu bắn đạn, nhuộm Boss ánh sáng đỏ nhẹ cảnh báo
        if (bossShootFlash > 0f) {
            batch.setColor(1.0f, 1.0f - bossShootFlash * 0.4f, 1.0f - bossShootFlash * 0.4f, 1.0f);
        }
        batch.draw(bossTexture,
                bossX - bW / 2f, bossY - bH / 2f,
                bW / 2f, bH / 2f, // Tâm xoay
                bW, bH,
                1f, 1f,
                bossRotation,
                0, 0, bossTexture.getWidth(), bossTexture.getHeight(),
                false, false);
        batch.setColor(Color.WHITE);

        // ── 4. Vẽ mèo hoạt ảnh ────────────────────────────────────────────────
        float hs = CAT_DISPLAY_SIZE / 2f;
        Animation<TextureRegion> currentAnim = (animState == AnimState.RUN) ? runAnim
                : (animState == AnimState.WALK) ? walkAnim : idleAnim;
        TextureRegion frame = currentAnim.getKeyFrame(stateTime);

        // Xoay chiều mặt mèo dựa theo di chuyển
        if (!facingLeft && !frame.isFlipX()) frame.flip(true, false);
        if (facingLeft && frame.isFlipX()) frame.flip(true, false);

        batch.draw(frame, catX - hs, catY - hs, CAT_DISPLAY_SIZE, CAT_DISPLAY_SIZE);

        // ── 5. HUD: TimeFrame & thời gian còn lại (Góc trên-PHẢI) ──────────────
        float tfScale = 3f;
        float tfW = timeFrameTexture.getWidth() * tfScale;
        float tfH = timeFrameTexture.getHeight() * tfScale;
        float tfX = screenW - tfW - 15f;
        float tfY = screenH - tfH - 15f;

        // Rung lắc nhẹ khung thời gian khi sắp hết giờ (< 10s) tạo kịch tính
        float timeLeft = Math.max(0f, GAME_DURATION - timer);
        float shakeX = 0f, shakeY = 0f;
        if (timeLeft < 10f && !gameOver) {
            shakeX = MathUtils.random(-3f, 3f);
            shakeY = MathUtils.random(-3f, 3f);
        }

        batch.draw(timeFrameTexture, tfX + shakeX, tfY + shakeY, tfW, tfH);

        hudFont.setColor(timeLeft < 10f ? Color.RED : Color.WHITE);
        hudFont.draw(batch,
                String.format("%.0f", timeLeft) + bundle.get("tron_kim_tiem_time_unit"),
                tfX + 8f + shakeX, tfY + tfH * 0.85f + shakeY);
        hudFont.setColor(Color.WHITE);

        // ── 6. Trình bày GameOver overlay ──────────────────────────────────────
        if (gameOver) {
            renderGameOver(batch);
        }
    }

    private void renderGameOver(SpriteBatch batch) {
        batch.setColor(0f, 0f, 0f, 0.72f);
        batch.draw(dimTexture, 0, 0, screenW, screenH);
        batch.setColor(Color.WHITE);

        float panelW = screenW * 0.45f;
        float panelH = screenH * 0.45f;
        float panelX = (screenW - panelW) / 2f;
        float panelY = (screenH - panelH) / 2f;
        hust.hedspi.oop.game.screens.MinigameScreen.staticPanelPatch.draw(batch, panelX, panelY, panelW, panelH);

        hudFont.setColor(won ? Color.GREEN : Color.RED);
        hudFont.draw(batch,
                bundle.get(won ? "tron_kim_tiem_win" : "tron_kim_tiem_lose"),
                panelX, panelY + panelH * 0.76f, panelW, com.badlogic.gdx.utils.Align.center, false);
        hudFont.setColor(Color.WHITE);

        dialogFont.getData().setScale(0.8f);
        dialogFont.setColor(Color.LIGHT_GRAY);
        dialogFont.draw(batch,
                bundle.get("tron_kim_tiem_exit_hint"),
                panelX, panelY + panelH * 0.55f, panelW, com.badlogic.gdx.utils.Align.center, false);
        dialogFont.getData().setScale(1.0f);
        dialogFont.setColor(Color.WHITE);

        float btnW = 160f;
        float btnH = 50f;
        float btnX = (screenW - btnW) / 2f;
        float btnY = panelY + 40f;

        boolean isHoveredOrPressed = false;
        com.badlogic.gdx.math.Vector2 mousePos = hust.hedspi.oop.game.screens.MinigameScreen.unproject(Gdx.input.getX(), Gdx.input.getY());
        if (mousePos.x >= btnX && mousePos.x <= btnX + btnW && mousePos.y >= btnY && mousePos.y <= btnY + btnH) {
            isHoveredOrPressed = true;
        }

        if (isHoveredOrPressed && Gdx.input.isTouched()) {
            hust.hedspi.oop.game.screens.MinigameScreen.staticBtnPressedPatch.draw(batch, btnX, btnY, btnW, btnH);
        } else {
            hust.hedspi.oop.game.screens.MinigameScreen.staticBtnPatch.draw(batch, btnX, btnY, btnW, btnH);
        }

        dialogFont.setColor(isHoveredOrPressed ? Color.YELLOW : Color.WHITE);
        dialogFont.draw(batch, "Quay lại", btnX, btnY + btnH / 2f + 6f, btnW, com.badlogic.gdx.utils.Align.center, false);
        dialogFont.setColor(Color.WHITE);
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
        safeDispose(poisonBallTexture);
        safeDispose(timeFrameTexture);
        safeDispose(dimTexture);
        safeDispose(idleTexture);
        safeDispose(walkTexture);
        safeDispose(runTexture);
    }

    private void safeDispose(Texture t) {
        if (t != null) {
            t.dispose();
        }
    }

    @Override
    public void forceEnd(boolean win) {
        this.won = win;
        this.gameOver = true;
        hust.hedspi.oop.game.managers.StoryManager.getInstance().recordResult(MinigameID.PET_ESCAPE_VET, win);
    }
}
