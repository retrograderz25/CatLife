package hust.hedspi.oop.game.minigames.thoat_khoi_cong;

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
import com.badlogic.gdx.utils.I18NBundle;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.managers.StoryManager;
import hust.hedspi.oop.game.minigames.IMinigameStrategy;
import hust.hedspi.oop.game.utils.Constants;
import hust.hedspi.oop.game.utils.MinigameID;

/**
 * Minigame "Thoát Khỏi Cống" (DAILY_ESCAPE_SEWER).
 *
 * Hệ toạ độ ảnh (imgY=0 ở góc trên-trái PNG):
 *   Spawn: góc trên-trái, imgX∈[0,190] imgY∈[0,190]
 *   Goal : góc dưới-phải, imgX∈[mazeW-150,mazeW] imgY∈[mazeH-150,mazeH]
 *
 * Va chạm: đọc alpha từng pixel của road.png (Pixmap, không load vào GPU).
 * Bóng tối: dark_frame.png scale 2.5× màn hình, tâm đặt trên mèo,
 *            fade-in khi mèo rời khỏi vùng spawn.
 */
public class ThoatKhoiCongMinigame implements IMinigameStrategy {

    private static final String ASSET_BASE = "minigames/thoat_khoi_cong/";

    private static final float GAME_DURATION   = Constants.THOAT_CONG_DURATION;
    private static final int   SPAWN_AREA      = Constants.THOAT_CONG_SPAWN_AREA;
    private static final int   GOAL_AREA       = Constants.THOAT_CONG_GOAL_AREA;
    private static final float CAT_SPEED       = Constants.THOAT_CONG_CAT_SPEED;
    private static final float CAT_RUN_SPEED   = Constants.THOAT_CONG_CAT_RUN_SPEED;
    private static final float MAZE_SCALE      = Constants.THOAT_CONG_MAZE_SCALE;
    private static final float CAT_SCALE_MULT  = Constants.THOAT_CONG_SPRITE_SCALE;
    private static final float DARK_FADE_SPEED = Constants.THOAT_CONG_DARK_FADE;

    // Sprite sheet: mỗi frame 80px wide, sprite mặc định quay mặt sang TRÁI
    private static final int   FRAME_CELL      = 80;
    private static final int   IDLE_FRAMES     = 8;
    private static final int   WALK_FRAMES     = 12;
    private static final int   RUN_FRAMES      = 8;
    private static final float IDLE_FRAME_DUR  = 0.12f;
    private static final float WALK_FRAME_DUR  = 0.075f;
    private static final float RUN_FRAME_DUR   = 0.06f;
    private static final int   HITBOX_HALF     = 4; // bán kính hitbox trong pixel toạ độ ảnh

    private enum AnimState { IDLE, WALK, RUN }

    // ── Textures / Pixmap ──────────────────────────────────────────────────────
    private Texture mazeTexture, darkFrameTexture, timeFrameTexture, dimTexture;
    private Texture idleTexture, walkTexture, runTexture;
    private Texture keyTexture;
    private Pixmap  roadPixmap;

    // ── Animations ────────────────────────────────────────────────────────────
    private Animation<TextureRegion> idleAnim, walkAnim, runAnim;
    private AnimState animState;
    private float     stateTime;

    // ── Maze dimensions & layout ──────────────────────────────────────────────
    private int   mazeW, mazeH;
    private float mazeDisplayW, mazeDisplayH;
    private float catDisplaySize;

    // ── Screen ────────────────────────────────────────────────────────────────
    private int screenW, screenH;

    // ── Cat position  (toạ độ ảnh: imgY=0 ở đỉnh PNG) ────────────────────────
    private float catImgX, catImgY;
    private boolean facingLeft; // true = mặt quay trái (hướng mặc định sprite)

    // ── Key position (trung tâm vùng 150×150 góc dưới-phải) ──────────────────
    private float keyImgX, keyImgY;

    // ── Camera (cuộn 2 chiều theo mèo, tọa độ = dòng/cột ảnh tại góc trên-trái viewport) ──
    private float camImgX, camImgY;

    // ── Bóng tối ──────────────────────────────────────────────────────────────
    private float   darkAlpha;
    private boolean leftSpawn;

    // ── Game state ────────────────────────────────────────────────────────────
    private float   timer;
    private boolean gameOver, won, exitRequested;

    // ── UI ────────────────────────────────────────────────────────────────────
    private BitmapFont hudFont, dialogFont;
    private I18NBundle bundle;

    // =========================================================================

    @Override
    public void start() {
        screenW = hust.hedspi.oop.game.utils.Constants.VIRTUAL_WIDTH;
        screenH = hust.hedspi.oop.game.utils.Constants.VIRTUAL_HEIGHT;

        loadAssets();

        hudFont    = ResourceManager.getInstance().hudFont;
        dialogFont = ResourceManager.getInstance().dialogFont;
        bundle     = ResourceManager.getInstance().getBundle();

        mazeDisplayW   = mazeW * MAZE_SCALE;
        mazeDisplayH   = mazeH * MAZE_SCALE;
        catDisplaySize = FRAME_CELL * MAZE_SCALE * CAT_SCALE_MULT;

        // Spawn tại tâm vùng 190×190 góc trên-trái
        catImgX    = SPAWN_AREA / 2f;
        catImgY    = SPAWN_AREA / 2f;
        facingLeft = true;
        animState  = AnimState.IDLE;
        stateTime  = 0f;
        camImgX    = 0f;
        camImgY    = 0f;

        // Chìa khóa tại tâm vùng 150×150 góc dưới-phải
        keyImgX = mazeW - GOAL_AREA / 2f;
        keyImgY = mazeH - GOAL_AREA / 2f;

        darkAlpha     = 0f;
        leftSpawn     = true; // bóng tối fade-in ngay từ đầu
        timer         = 0f;
        gameOver      = false;
        won           = false;
        exitRequested = false;
    }

    private void loadAssets() {
        mazeTexture      = new Texture(Gdx.files.internal(ASSET_BASE + "maze.png"));
        darkFrameTexture = new Texture(Gdx.files.internal(ASSET_BASE + "dark_frame.png"));
        timeFrameTexture = new Texture(Gdx.files.internal(ASSET_BASE + "timeframe.png"));
        keyTexture       = new Texture(Gdx.files.internal(ASSET_BASE + "key.png"));
        idleTexture      = new Texture(Gdx.files.internal(ASSET_BASE + "orange/IDLE.png"));
        walkTexture      = new Texture(Gdx.files.internal(ASSET_BASE + "orange/WALK.png"));
        runTexture       = new Texture(Gdx.files.internal(ASSET_BASE + "orange/RUN.png"));

        mazeW = mazeTexture.getWidth();
        mazeH = mazeTexture.getHeight();

        roadPixmap = new Pixmap(Gdx.files.internal(ASSET_BASE + "road.png"));

        idleAnim = buildAnim(idleTexture, IDLE_FRAMES, IDLE_FRAME_DUR);
        walkAnim = buildAnim(walkTexture, WALK_FRAMES, WALK_FRAME_DUR);
        runAnim  = buildAnim(runTexture,  RUN_FRAMES,  RUN_FRAME_DUR);

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

    // =========================================================================

    @Override
    public void update(float dt) {
        if (gameOver) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) exitRequested = true;
            return;
        }

        timer += dt;
        if (timer >= GAME_DURATION) {
            endGame(false);
            return;
        }

        boolean running = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                       || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        float speed = running ? CAT_RUN_SPEED : CAT_SPEED;

        float dx = 0f, dy = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  { dx -= speed * dt; facingLeft = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) { dx += speed * dt; facingLeft = false; }
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    dy -= speed * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  dy += speed * dt;

        boolean moving = (dx != 0 || dy != 0);
        if (dx != 0 && canMoveTo(catImgX + dx, catImgY)) catImgX += dx;
        if (dy != 0 && canMoveTo(catImgX, catImgY + dy)) catImgY += dy;

        // Đảm bảo mèo không vượt biên map sau mọi di chuyển
        catImgX = MathUtils.clamp(catImgX, HITBOX_HALF, mazeW - HITBOX_HALF);
        catImgY = MathUtils.clamp(catImgY, HITBOX_HALF, mazeH - HITBOX_HALF);

        // Cập nhật trạng thái animation; reset stateTime khi đổi trạng thái
        AnimState targetAnim = !moving ? AnimState.IDLE : (running ? AnimState.RUN : AnimState.WALK);
        if (targetAnim != animState) { animState = targetAnim; stateTime = 0f; }
        stateTime += dt;

        // Bóng tối fade-in khi mèo rời spawn
        if (!leftSpawn && !isInSpawnZone()) leftSpawn = true;
        if (leftSpawn && darkAlpha < 1f)
            darkAlpha = Math.min(1f, darkAlpha + DARK_FADE_SPEED * dt);

        // Camera cuộn 2 chiều theo mèo
        float viewportImgW = screenW / MAZE_SCALE;
        float viewportImgH = screenH / MAZE_SCALE;
        camImgX = MathUtils.clamp(catImgX - viewportImgW / 2f,
                                  0f, Math.max(0f, mazeW - viewportImgW));
        camImgY = MathUtils.clamp(catImgY - viewportImgH / 2f,
                                  0f, Math.max(0f, mazeH - viewportImgH));

        // Thắng: mèo chạm vùng 150×150 góc dưới-PHẢI
        if (catImgX >= mazeW - GOAL_AREA && catImgY >= mazeH - GOAL_AREA) endGame(true);
    }

    private void endGame(boolean playerWon) {
        won      = playerWon;
        gameOver = true;
        StoryManager.getInstance().recordResult(MinigameID.DAILY_ESCAPE_SEWER, won);
    }

    private boolean isInSpawnZone() {
        return catImgX <= SPAWN_AREA && catImgY <= SPAWN_AREA;
    }

    /**
     * Kiểm tra vị trí mới hợp lệ: phải nằm trong biên maze VÀ trên pixel đường đi.
     */
    private boolean canMoveTo(float nx, float ny) {
        // Chặn ra khỏi biên ảnh maze
        if (nx - HITBOX_HALF < 0 || nx + HITBOX_HALF > mazeW - 1) return false;
        if (ny - HITBOX_HALF < 0 || ny + HITBOX_HALF > mazeH - 1) return false;
        return isWalkable(nx - HITBOX_HALF, ny - HITBOX_HALF)
            && isWalkable(nx + HITBOX_HALF, ny - HITBOX_HALF)
            && isWalkable(nx - HITBOX_HALF, ny + HITBOX_HALF)
            && isWalkable(nx + HITBOX_HALF, ny + HITBOX_HALF);
    }

    private boolean isWalkable(float mx, float my) {
        int px = MathUtils.clamp((int) mx, 0, mazeW - 1);
        int py = MathUtils.clamp((int) my, 0, mazeH - 1);
        return (roadPixmap.getPixel(px, py) & 0xFF) > 64;
    }

    // =========================================================================

    @Override
    public void render(SpriteBatch batch) {
        // ── 1. Vẽ mê cung ────────────────────────────────────────────────────
        // mazeDrawX: cột ảnh camImgX xuất hiện ở screen x=0
        // mazeDrawY: hàng ảnh camImgY xuất hiện ở screen y=screenH (đỉnh)
        float mazeDrawX = -camImgX * MAZE_SCALE;
        float mazeDrawY = screenH - mazeDisplayH + camImgY * MAZE_SCALE;
        batch.draw(mazeTexture, mazeDrawX, mazeDrawY, mazeDisplayW, mazeDisplayH);

        // ── 2. Vẽ chìa khóa (ở vùng đích góc dưới-phải) ─────────────────────
        float keyCX = (keyImgX - camImgX) * MAZE_SCALE;
        float keyCY = screenH + (camImgY - keyImgY) * MAZE_SCALE;
        float keySize = catDisplaySize * 1.5f;
        batch.draw(keyTexture, keyCX - keySize / 2f, keyCY - keySize / 2f, keySize, keySize);

        // ── 3. Vẽ mèo ────────────────────────────────────────────────────────
        float catCX = (catImgX - camImgX) * MAZE_SCALE;
        float catCY = screenH + (camImgY - catImgY) * MAZE_SCALE;
        float hs    = catDisplaySize / 2f;

        Animation<TextureRegion> currentAnim = (animState == AnimState.RUN) ? runAnim
                                             : (animState == AnimState.WALK) ? walkAnim : idleAnim;
        TextureRegion frame = currentAnim.getKeyFrame(stateTime);

        // Sprite mặc định quay trái; flip sang phải khi cần
        if (!facingLeft && !frame.isFlipX()) frame.flip(true, false);
        if (facingLeft  &&  frame.isFlipX()) frame.flip(true, false);

        batch.draw(frame, catCX - hs, catCY - hs, catDisplaySize, catDisplaySize);

        // ── 4. Bóng tối ───────────────────────────────────────────────────────
        if (darkAlpha > 0f) {
            float dfW = screenW * 1.9f;
            float dfH = dfW * darkFrameTexture.getHeight() / darkFrameTexture.getWidth();
            batch.setColor(1f, 1f, 1f, darkAlpha);
            batch.draw(darkFrameTexture,
                catCX - dfW / 2f, catCY - dfH / 2f, dfW, dfH);
            batch.setColor(Color.WHITE);
        }

        // ── 5. HUD: timeframe ở góc trên-PHẢI ────────────────────────────────
        float tfScale = 3f;
        float tfW = timeFrameTexture.getWidth()  * tfScale;
        float tfH = timeFrameTexture.getHeight() * tfScale;
        float tfX = screenW - tfW - 10f; // góc trên-phải
        float tfY = screenH - tfH - 10f;
        batch.draw(timeFrameTexture, tfX, tfY, tfW, tfH);

        float timeLeft = Math.max(0f, GAME_DURATION - timer);
        hudFont.setColor(timeLeft < 10f ? Color.RED : Color.WHITE);
        hudFont.draw(batch,
            String.format("%.0f", timeLeft) + bundle.get("thoat_cong_time_unit"),
            tfX + 8f, tfY + tfH * 0.85f);
        hudFont.setColor(Color.WHITE);

        dialogFont.setColor(Color.WHITE);
        dialogFont.draw(batch, bundle.get("thoat_cong_ctrl_hint"), tfX, tfY - 10f);

        // ── 6. Game-over overlay ──────────────────────────────────────────────
        if (gameOver) renderGameOver(batch);
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
        hudFont.draw(batch,
            bundle.get(won ? "thoat_cong_win" : "thoat_cong_lose"),
            panelX + 20f, panelY + panelH * 0.82f);
        hudFont.setColor(Color.WHITE);

        dialogFont.setColor(0.8f, 0.8f, 0.8f, 1f);
        dialogFont.draw(batch,
            bundle.get("thoat_cong_exit_hint"),
            panelX + 20f, panelY + panelH * 0.35f);
        dialogFont.setColor(Color.WHITE);
    }

    // =========================================================================

    @Override public boolean isFinished() { return exitRequested; }
    @Override public boolean isWon()      { return won; }

    @Override
    public void dispose() {
        safeDispose(mazeTexture);
        safeDispose(darkFrameTexture);
        safeDispose(timeFrameTexture);
        safeDispose(keyTexture);
        safeDispose(idleTexture);
        safeDispose(walkTexture);
        safeDispose(runTexture);
        safeDispose(dimTexture);
        if (roadPixmap != null) roadPixmap.dispose();
    }

    private void safeDispose(Texture t) { if (t != null) t.dispose(); }
}
