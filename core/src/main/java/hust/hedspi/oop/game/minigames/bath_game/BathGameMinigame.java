package hust.hedspi.oop.game.minigames.bath_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.I18NBundle;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.managers.StoryManager;
import hust.hedspi.oop.game.minigames.IMinigameStrategy;
import hust.hedspi.oop.game.utils.Constants;
import hust.hedspi.oop.game.utils.MinigameID;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Minigame "Tắm Mèo" (PET_BATH).
 *
 * Người chơi dùng chuột bấm vào bong bóng đang bay lên trong vùng bồn tắm.
 * Mục tiêu: bấm đủ BATH_WIN_THRESHOLD bong bóng trong BATH_DURATION giây.
 *
 * Vùng spawn bong bóng (ZONE_*) được định nghĩa trong toạ độ màn hình
 * (Y=0 ở đáy màn hình). Chỉnh tay các hằng số đó để khớp với vùng nước
 * trong ảnh cat_bath.png.
 */
public class BathGameMinigame implements IMinigameStrategy {

    private static final String ASSET_BASE = "minigames/bath_game/";

    private static final float DURATION        = Constants.BATH_DURATION;
    private static final int   WIN_THRESHOLD   = Constants.BATH_WIN_THRESHOLD;
    private static final float BUBBLE_SPEED    = Constants.BATH_BUBBLE_SPEED;
    private static final float SPAWN_INTERVAL  = Constants.BATH_SPAWN_INTERVAL;
    private static final int   BUBBLE_SIZE     = 24 * Constants.BATH_BUBBLE_SCALE; // 48px sau scale
    private static final float CURSOR_SCALE    = Constants.BATH_CURSOR_SCALE;

    // ── Vùng spawn bong bóng (toạ độ màn hình, Y=0 ở đáy) ───────────────────
    // !! CHỈNH TAY: sửa 4 hằng số dưới để vùng spawn khớp với mặt nước bồn tắm
    //    trong cat_bath.png. Chạy game và điều chỉnh đến khi bong bóng nổi
    //    đúng trong lòng bồn tắm.
    private static final int ZONE_X = 148;  // cạnh trái vùng spawn (pixel từ trái màn hình)
    private static final int ZONE_Y = 175;   // cạnh dưới vùng spawn (pixel từ đáy màn hình)
    private static final int ZONE_W = 344;  // chiều rộng vùng spawn
    private static final int ZONE_H = 300;  // chiều cao vùng spawn (bong bóng biến mất ở đây)

    // ── Textures ──────────────────────────────────────────────────────────────
    private Texture bgTexture, bubbleTex, cursorTex, timeFrameTex, dimTexture;

    // ── Layout background ─────────────────────────────────────────────────────
    private float bgScale, bgDrawX, bgDrawY, bgDrawW, bgDrawH;

    // ── Screen ────────────────────────────────────────────────────────────────
    private int screenW, screenH;

    // ── Bong bóng ─────────────────────────────────────────────────────────────
    private static class Bubble {
        float x, y;
        boolean alive;
        Bubble(float x, float y) { this.x = x; this.y = y; this.alive = true; }
    }

    private final ArrayList<Bubble> bubbles = new ArrayList<>();
    private float spawnTimer;

    // ── Game state ────────────────────────────────────────────────────────────
    private float   timer;
    private int     score;
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

        // Căn background "contain" (hiển thị toàn bộ ảnh, có thể có viền)
        bgScale = Math.min((float) screenW / bgTexture.getWidth(),
                           (float) screenH / bgTexture.getHeight());
        bgDrawW = bgTexture.getWidth()  * bgScale;
        bgDrawH = bgTexture.getHeight() * bgScale;
        bgDrawX = (screenW - bgDrawW) / 2f;
        bgDrawY = (screenH - bgDrawH) / 2f;

        bubbles.clear();
        spawnTimer    = 0f;
        timer         = 0f;
        score         = 0;
        gameOver      = false;
        won           = false;
        exitRequested = false;
    }

    private void loadAssets() {
        bgTexture    = new Texture(Gdx.files.internal(ASSET_BASE + "cat_bath.png"));
        bubbleTex    = new Texture(Gdx.files.internal(ASSET_BASE + "bubble.png"));
        cursorTex    = new Texture(Gdx.files.internal(ASSET_BASE + "cat_cursor.png"));
        timeFrameTex = new Texture(Gdx.files.internal(ASSET_BASE + "timeframe.png"));

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        dimTexture = new Texture(pix);
        pix.dispose();
    }

    // =========================================================================

    @Override
    public void update(float dt) {
        if (gameOver) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) exitRequested = true;
            return;
        }

        timer += dt;
        if (timer >= DURATION) {
            endGame(false);
            return;
        }

        // Spawn bong bóng mới ở vị trí ngẫu nhiên dọc theo cạnh dưới vùng spawn
        spawnTimer += dt;
        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer = 0f;
            float bx = ZONE_X + MathUtils.random(0, ZONE_W - BUBBLE_SIZE);
            float by = ZONE_Y; // bắt đầu ở đáy vùng spawn
            bubbles.add(new Bubble(bx, by));
        }

        // Di chuyển bong bóng lên trên; loại bỏ những cái vượt đỉnh vùng
        Iterator<Bubble> it = bubbles.iterator();
        while (it.hasNext()) {
            Bubble b = it.next();
            b.y += BUBBLE_SPEED * dt;
            if (!b.alive || b.y > ZONE_Y + ZONE_H) it.remove();
        }

        // Xử lý click chuột: bấm trúng bong bóng → bong bóng biến mất, +1 điểm
        if (Gdx.input.justTouched()) {
            float mx = hust.hedspi.oop.game.screens.MinigameScreen.unproject(Gdx.input.getX(), Gdx.input.getY()).x;
            float my = hust.hedspi.oop.game.screens.MinigameScreen.unproject(Gdx.input.getX(), Gdx.input.getY()).y; // LibGDX Y flip
            for (Bubble b : bubbles) {
                if (b.alive && isBubbleHit(mx, my, b)) {
                    b.alive = false;
                    score++;
                    if (score >= WIN_THRESHOLD) endGame(true);
                    break; // chỉ bấm được 1 bong bóng mỗi click
                }
            }
        }
    }

    private boolean isBubbleHit(float mx, float my, Bubble b) {
        return mx >= b.x && mx <= b.x + BUBBLE_SIZE
            && my >= b.y && my <= b.y + BUBBLE_SIZE;
    }

    private void endGame(boolean playerWon) {
        won      = playerWon;
        gameOver = true;
        StoryManager.getInstance().recordResult(MinigameID.PET_BATH, won);
    }

    // =========================================================================

    @Override
    public void render(SpriteBatch batch) {
        // ── 1. Background ─────────────────────────────────────────────────────
        batch.draw(bgTexture, bgDrawX, bgDrawY, bgDrawW, bgDrawH);

        // ── 2. Bong bóng ──────────────────────────────────────────────────────
        for (Bubble b : bubbles) {
            if (b.alive) {
                batch.draw(bubbleTex, b.x, b.y, BUBBLE_SIZE, BUBBLE_SIZE);
            }
        }

        // ── 3. HUD: timeframe góc trên-phải ───────────────────────────────────
        float tfScale = 3f;
        float tfW = timeFrameTex.getWidth()  * tfScale;
        float tfH = timeFrameTex.getHeight() * tfScale;
        float tfX = screenW - tfW - 10f;
        float tfY = screenH - tfH - 10f;
        batch.draw(timeFrameTex, tfX, tfY, tfW, tfH);

        float timeLeft = Math.max(0f, DURATION - timer);
        hudFont.setColor(timeLeft < 10f ? Color.RED : Color.WHITE);
        hudFont.draw(batch,
            String.format("%.0f", timeLeft) + bundle.get("bath_time_unit"),
            tfX + 8f, tfY + tfH * 0.85f);
        hudFont.setColor(Color.WHITE);

        // Điểm số bên dưới timeframe
        hudFont.setColor(score >= WIN_THRESHOLD ? Color.GREEN : Color.WHITE);
        hudFont.draw(batch, score + "/" + WIN_THRESHOLD,
            tfX + 8f, tfY + tfH * 0.55f);
        hudFont.setColor(Color.WHITE);

        dialogFont.setColor(Color.WHITE);
        dialogFont.draw(batch, bundle.get("bath_ctrl_hint"), tfX, tfY - 10f);

        // ── 4. Con trỏ chuột tùy chỉnh (vẽ sau cùng, luôn trên đỉnh) ─────────
        float cursorW = cursorTex.getWidth()  * CURSOR_SCALE;
        float cursorH = cursorTex.getHeight() * CURSOR_SCALE;
        float mx = hust.hedspi.oop.game.screens.MinigameScreen.unproject(Gdx.input.getX(), Gdx.input.getY()).x;
        float my = hust.hedspi.oop.game.screens.MinigameScreen.unproject(Gdx.input.getX(), Gdx.input.getY()).y; // LibGDX Y flip
        batch.draw(cursorTex, mx, my - cursorH, cursorW, cursorH);

        // ── 5. Game-over overlay ──────────────────────────────────────────────
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
        batch.draw(timeFrameTex, panelX, panelY, panelW, panelH);

        hudFont.setColor(won ? Color.GREEN : Color.RED);
        hudFont.draw(batch,
            bundle.get(won ? "bath_win" : "bath_lose"),
            panelX + 20f, panelY + panelH * 0.82f);
        hudFont.setColor(Color.WHITE);

        dialogFont.setColor(0.8f, 0.8f, 0.8f, 1f);
        dialogFont.draw(batch,
            bundle.get("bath_exit_hint"),
            panelX + 20f, panelY + panelH * 0.35f);
        dialogFont.setColor(Color.WHITE);
    }

    // =========================================================================

    @Override public boolean isFinished() { return exitRequested; }
    @Override public boolean isWon()      { return won; }

    @Override
    public void dispose() {
        safeDispose(bgTexture);
        safeDispose(bubbleTex);
        safeDispose(cursorTex);
        safeDispose(timeFrameTex);
        safeDispose(dimTexture);
    }

    private void safeDispose(Texture t) { if (t != null) t.dispose(); }
}