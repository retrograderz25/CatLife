package hust.hedspi.oop.game.minigames.cao_mong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.I18NBundle;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.managers.SoundManager;
import hust.hedspi.oop.game.managers.StoryManager;
import hust.hedspi.oop.game.minigames.IMinigameStrategy;
import hust.hedspi.oop.game.utils.Constants;
import hust.hedspi.oop.game.utils.MinigameID;

/**
 * Minigame "Cào Móng" (DAILY_SCRATCH).
 *
 * Bố cục tay:
 *   Tay PHẢI (góc dưới-phải) → chạm zone UP và RIGHT
 *   Tay TRÁI (góc dưới-trái) → chạm zone DOWN và LEFT
 *
 * Animation: mỗi khi bấm đúng, tay tương ứng vươn dần lên đến zone
 * rồi rút về vị trí nghỉ (lerp 0→1→0 trong HAND_SHOW_TIME giây).
 */
public class CaoMongMinigame implements IMinigameStrategy {

    private static final float GAME_DURATION  = Constants.CAO_MONG_DURATION;
    private static final int   WIN_THRESHOLD  = Constants.CAO_MONG_WIN_THRESHOLD;
    private static final float HAND_SHOW_TIME = Constants.CAO_MONG_HAND_SHOW_TIME;
    private static final float SPRITE_SCALE   = Constants.CAO_MONG_SPRITE_SCALE;
    private static final float HAND_SCALE     = Constants.CAO_MONG_HAND_SCALE;

    private static final String ASSET_BASE = "minigames/cao_mong/";

    // Góc quay mũi tên: UP=0° DOWN=180° LEFT=90°CCW RIGHT=-90°CW (sprite gốc hướng lên)
    private static final float[] ARROW_ROTATIONS = { 0f, 180f, 90f, -90f };

    // Tỉ lệ vị trí zone trên board
    private static final float ZONE_FAR_RATIO  = 0.75f;
    private static final float ZONE_NEAR_RATIO = 0.25f;

    // Phím điều hướng theo thứ tự: 0=UP 1=DOWN 2=LEFT 3=RIGHT
    private static final int[] DIRECTION_KEYS = {
        Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT
    };

    // Index tay
    private static final int LEFT_HAND  = 0;
    private static final int RIGHT_HAND = 1;

    // Textures
    private Texture        bgTexture, boardTexture, catHandTexture, timeFrameTexture, dimTexture;
    private Texture[]      arrowTextures;
    private TextureRegion[] arrowRegions;
    private TextureRegion  handRegionRight; // bàn tay phải (sprite gốc)
    private TextureRegion  handRegionLeft;  // bàn tay trái  (flipX)

    // Layout
    private int   screenW, screenH;
    private float boardX, boardY, boardW, boardH;
    private float arrowW, arrowH;
    private float handW,  handH;
    private float tfX, tfY, tfW, tfH;

    // Hint panel (bên phải board)
    private float hintX, hintY, hintW, hintH;
    private String[] hintLines; // load từ bundle trong start()

    // Vị trí nghỉ của từng tay (tâm sprite, một phần ẩn dưới màn hình)
    private final float[] handRestCX = new float[2];
    private final float[] handRestCY = new float[2];

    // Trạng thái animation tay
    private final boolean[] handActive    = new boolean[2];
    private final float[]   handAnimTime  = new float[2];
    private final int[]     handTarget    = new int[2]; // zone (0-3) mà tay đang vươn tới

    // Game state
    private float   timer;
    private int     score;
    private boolean gameOver, exitRequested, won;
    private int     activeArrow; // zone đang hiển thị mũi tên (0-3)

    // Fonts & bundle từ ResourceManager (không tạo mới, không dispose)
    private BitmapFont hudFont;
    private BitmapFont dialogFont;
    private I18NBundle bundle;

    // -------------------------------------------------------------------------

    @Override
    public void start() {
        screenW = hust.hedspi.oop.game.utils.Constants.VIRTUAL_WIDTH;
        screenH = hust.hedspi.oop.game.utils.Constants.VIRTUAL_HEIGHT;

        loadTextures();
        calculateLayout();

        hudFont    = ResourceManager.getInstance().hudFont;
        dialogFont = ResourceManager.getInstance().dialogFont;
        bundle     = ResourceManager.getInstance().getBundle();

        timer         = 0f;
        score         = 0;
        gameOver      = false;
        exitRequested = false;
        won           = false;
        activeArrow   = MathUtils.random(3);

        handActive[LEFT_HAND]  = handActive[RIGHT_HAND]  = false;
        handAnimTime[LEFT_HAND] = handAnimTime[RIGHT_HAND] = 0f;

        hintLines = new String[]{
            bundle.get("cao_mong_ctrl_0"),
            bundle.get("cao_mong_ctrl_1"),
            bundle.get("cao_mong_ctrl_2"),
            bundle.get("cao_mong_ctrl_3")
        };
    }

    private void loadTextures() {
        bgTexture        = new Texture(Gdx.files.internal(ASSET_BASE + "Background.png"));
        boardTexture     = new Texture(Gdx.files.internal(ASSET_BASE + "final_board.png"));
        catHandTexture   = new Texture(Gdx.files.internal(ASSET_BASE + "cat_hand.png"));
        timeFrameTexture = new Texture(Gdx.files.internal(ASSET_BASE + "timeframe.png"));

        arrowTextures = new Texture[4];
        arrowTextures[0] = new Texture(Gdx.files.internal(ASSET_BASE + "arrow/blue.png"));
        arrowTextures[1] = new Texture(Gdx.files.internal(ASSET_BASE + "arrow/green.png"));
        arrowTextures[2] = new Texture(Gdx.files.internal(ASSET_BASE + "arrow/red.png"));
        arrowTextures[3] = new Texture(Gdx.files.internal(ASSET_BASE + "arrow/yellow.png"));

        arrowRegions = new TextureRegion[4];
        for (int i = 0; i < 4; i++) arrowRegions[i] = new TextureRegion(arrowTextures[i]);

        handRegionRight = new TextureRegion(catHandTexture);
        handRegionLeft  = new TextureRegion(catHandTexture);
        handRegionLeft.flip(true, false); // tay trái = mirror ngang của tay phải

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        dimTexture = new Texture(pix);
        pix.dispose();
    }

    private void calculateLayout() {
        boardW = boardTexture.getWidth()  * SPRITE_SCALE;
        boardH = boardTexture.getHeight() * SPRITE_SCALE;
        boardX = (screenW - boardW) / 2f;
        boardY = (screenH - boardH) / 2f;

        arrowW = arrowTextures[0].getWidth()  * SPRITE_SCALE;
        arrowH = arrowTextures[0].getHeight() * SPRITE_SCALE;

        handW = catHandTexture.getWidth()  * HAND_SCALE;
        handH = catHandTexture.getHeight() * HAND_SCALE;

        // Tay đứng ở vị trí 1/3 và 2/3 chiều ngang màn hình (dưới board).
        // Tâm sprite nằm ở handH*0.3 → phần dưới bị cắt bởi mép màn hình.
        handRestCX[LEFT_HAND]  = screenW / 3f;
        handRestCX[RIGHT_HAND] = screenW * 2f / 3f;
        handRestCY[LEFT_HAND]  = handH * 0.3f;
        handRestCY[RIGHT_HAND] = handH * 0.3f;

        tfW = timeFrameTexture.getWidth()  * 5f;
        tfH = timeFrameTexture.getHeight() * 1.8f;
        tfX = 10f;
        tfY = screenH - tfH - 10f;

        // Hint panel: ngay bên phải board, căn giữa dọc
        float iconSz = 18f; // icon mũi tên nhỏ (px, square bounding box)
        float lineH  = 26f;
        hintW = iconSz + 55f;         // icon + khoảng cách + text
        hintH = 4 * lineH + 14f;      // 4 dòng + padding trên/dưới
        hintX = boardX + boardW + 10f;
        hintY = (screenH - hintH) / 2f;
    }

    /** Tay nào xử lý hướng dir: UP/RIGHT → tay phải, DOWN/LEFT → tay trái. */
    private int handOf(int dir) {
        return (dir == 0 || dir == 3) ? RIGHT_HAND : LEFT_HAND;
    }

    /**
     * Easing cho animation "mèo vờn": vươn nhanh (ease-out) → giữ → rút nhẹ.
     *   0–25% thời gian : vươn lên, ease-out quadratic (snappy)
     *   25–50%           : giữ tại target (brief touch)
     *   50–100%          : rút về, linear
     */
    private float getLerpFactor(float animTime) {
        float norm = MathUtils.clamp(animTime / HAND_SHOW_TIME, 0f, 1f);
        if (norm < 0.25f) {
            float t = norm / 0.25f;
            return 1f - (1f - t) * (1f - t); // ease-out quad
        } else if (norm < 0.5f) {
            return 1f;                         // hold
        } else {
            float t = (norm - 0.5f) / 0.5f;
            return 1f - t;                     // linear retract
        }
    }

    /** Tâm (cx, cy) của zone dir trên board. */
    private float[] zoneCenter(int dir) {
        float cx = boardX + boardW / 2f;
        float cy = boardY + boardH / 2f;
        switch (dir) {
            case 0: return new float[]{ cx, boardY + boardH * (ZONE_FAR_RATIO + 0.03f)  }; // UP
            case 1: return new float[]{ cx, boardY + boardH * (ZONE_NEAR_RATIO - 0.03f) }; // DOWN
            case 2: return new float[]{ boardX + boardW * (ZONE_NEAR_RATIO - 0.03f), cy }; // LEFT
            case 3: return new float[]{ boardX + boardW * (ZONE_FAR_RATIO + 0.03f),  cy }; // RIGHT
        }
        return new float[]{ cx, cy };
    }

    /** Vẽ TextureRegion căn tâm tại (cx, cy), cho phép rotation. */
    private void drawCentered(SpriteBatch batch, TextureRegion region,
                               float cx, float cy, float w, float h, float rotation) {
        batch.draw(region,
            cx - w / 2f, cy - h / 2f,
            w / 2f, h / 2f,
            w, h,
            1f, 1f,
            rotation);
    }

    // -------------------------------------------------------------------------

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

        // Tiến animation từng tay
        for (int h = 0; h < 2; h++) {
            if (handActive[h]) {
                handAnimTime[h] += dt;
                if (handAnimTime[h] >= HAND_SHOW_TIME) {
                    handActive[h]   = false;
                    handAnimTime[h] = 0f;
                }
            }
        }

        if (timer >= GAME_DURATION) {
            won      = score >= WIN_THRESHOLD;
            gameOver = true;
            StoryManager.getInstance().recordResult(MinigameID.DAILY_SCRATCH, won);
            return;
        }

        for (int i = 0; i < DIRECTION_KEYS.length; i++) {
            if (Gdx.input.isKeyJustPressed(DIRECTION_KEYS[i])) {
                if (i == activeArrow) {
                    score++;
                    SoundManager.getInstance().playSFX(SoundManager.SFX_CAT_SCRATCH);
                    int h = handOf(i);
                    handActive[h]   = true;
                    handAnimTime[h] = 0f;
                    handTarget[h]   = i;
                    activeArrow = MathUtils.random(3);
                }
                break;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(bgTexture, 0, 0, screenW, screenH);
        batch.draw(boardTexture, boardX, boardY, boardW, boardH);

        // Mũi tên active – xoay đúng hướng
        float[] ac = zoneCenter(activeArrow);
        drawCentered(batch, arrowRegions[activeArrow],
            ac[0], ac[1], arrowW, arrowH, ARROW_ROTATIONS[activeArrow]);

        // Hai bàn tay – luôn hiển thị, tay active thì vươn về phía zone
        renderHand(batch, LEFT_HAND,  handRegionLeft);
        renderHand(batch, RIGHT_HAND, handRegionRight);

        // Timeframe + đếm ngược
        batch.draw(timeFrameTexture, tfX, tfY, tfW, tfH);
        float timeLeft = Math.max(0f, GAME_DURATION - timer);
        hudFont.setColor(timeLeft < 10f ? Color.RED : Color.WHITE);
        hudFont.draw(batch,
            String.format("%.0f", timeLeft) + bundle.get("cao_mong_time_unit"),
            tfX + 8f, tfY + tfH * 0.85f);
        hudFont.setColor(Color.WHITE);

        // Score góc trên phải
        dialogFont.setColor(Color.WHITE);
        dialogFont.draw(batch, score + " / " + WIN_THRESHOLD, screenW - 100f, screenH - 10f);

        if (gameOver) renderGameOver(batch);
    }

    /** Tính vị trí và vẽ một tay (index h). */
    private void renderHand(SpriteBatch batch, int h, TextureRegion region) {
        float cx, cy;
        if (handActive[h]) {
            float[] zone = zoneCenter(handTarget[h]);
            // Đặt tâm sprite sao cho đầu ngón (đỉnh sprite) chạm đúng tâm zone
            float targetCX = zone[0];
            float targetCY = zone[1] - handH / 2f;
            float t = getLerpFactor(handAnimTime[h]);
            cx = MathUtils.lerp(handRestCX[h], targetCX, t);
            cy = MathUtils.lerp(handRestCY[h], targetCY, t);
        } else {
            cx = handRestCX[h];
            cy = handRestCY[h];
        }
        drawCentered(batch, region, cx, cy, handW, handH, 0f);
    }

    private void renderGameOver(SpriteBatch batch) {
        batch.setColor(0f, 0f, 0f, 0.65f);
        batch.draw(dimTexture, 0, 0, screenW, screenH);
        batch.setColor(Color.WHITE);

        float panelW = screenW * 0.45f;
        float panelH = screenH * 0.45f;
        float panelX = (screenW - panelW) / 2f;
        float panelY = (screenH - panelH) / 2f;
        hust.hedspi.oop.game.screens.MinigameScreen.staticPanelPatch.draw(batch, panelX, panelY, panelW, panelH);

        hudFont.setColor(won ? Color.GREEN : Color.RED);
        hudFont.draw(batch,
            bundle.get(won ? "cao_mong_win" : "cao_mong_lose"),
            panelX, panelY + panelH * 0.76f, panelW, com.badlogic.gdx.utils.Align.center, false);
        hudFont.setColor(Color.WHITE);

        dialogFont.setColor(Color.WHITE);
        dialogFont.draw(batch,
            bundle.get("cao_mong_score") + " " + score + " / " + WIN_THRESHOLD,
            panelX, panelY + panelH * 0.58f, panelW, com.badlogic.gdx.utils.Align.center, false);

        dialogFont.getData().setScale(0.8f);
        dialogFont.setColor(Color.LIGHT_GRAY);
        dialogFont.draw(batch,
            bundle.get("cao_mong_exit_hint"),
            panelX, panelY + panelH * 0.44f, panelW, com.badlogic.gdx.utils.Align.center, false);
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

    // -------------------------------------------------------------------------

    @Override public boolean isFinished() { return exitRequested; }
    @Override public boolean isWon()      { return won; }

    @Override
    public void dispose() {
        safeDispose(bgTexture);
        safeDispose(boardTexture);
        safeDispose(catHandTexture);
        safeDispose(timeFrameTexture);
        safeDispose(dimTexture);
        if (arrowTextures != null)
            for (Texture t : arrowTextures) safeDispose(t);
        // hudFont, dialogFont, bundle thuộc ResourceManager → không dispose ở đây
    }

    private void safeDispose(Texture t) { if (t != null) t.dispose(); }

    @Override
    public void forceEnd(boolean win) {
        this.won = win;
        this.gameOver = true;
        hust.hedspi.oop.game.managers.StoryManager.getInstance().recordResult(MinigameID.DAILY_SCRATCH, win);
    }
}
