package hust.hedspi.oop.game.minigames.tim_tieu_tam;

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

/**
 * Minigame "Tìm Tiểu Tam" (LOVE_DETECTIVE).
 *
 * Người chơi được hỏi TTT_QUESTION_COUNT câu. Mỗi câu hiển thị TTT_OPTIONS_PER_Q
 * ảnh ở nửa dưới màn hình (1 đúng prefix "true_" + 2 sai prefix "false_").
 * Chọn sai hoặc hết giờ → thua ngay. Đúng hết tất cả → thắng.
 *
 * Hình ảnh:
 *   true_1..3.png  → đáp án đúng (3 ảnh, xoay vòng theo câu)
 *   false_1..6.png → đáp án sai  (6 ảnh, xoay vòng theo câu)
 */
public class TimTieuTamMinigame implements IMinigameStrategy {

    private static final String ASSET_BASE = "minigames/tim_tieu_tam/";

    private static final int   QUESTION_COUNT    = Constants.TTT_QUESTION_COUNT;
    private static final float QUESTION_DURATION = Constants.TTT_QUESTION_DURATION;
    private static final int   OPTIONS_PER_Q     = Constants.TTT_OPTIONS_PER_Q;
    private static final float FEEDBACK_DURATION = Constants.TTT_FEEDBACK_DURATION;
    private static final int   CARD_PAD          = Constants.TTT_CARD_PADDING;

    // ── Textures ──────────────────────────────────────────────────────────────
    private Texture bgTexture, timeFrameTex, dimTexture;
    private Texture[] trueTex;  // true_1..3
    private Texture[] falseTex; // false_1..6

    // ── Question data ─────────────────────────────────────────────────────────
    private static class Question {
        final Texture[] options;  // textures cho từng slot
        final int correctSlot;    // index slot chứa đáp án đúng
        Question(Texture[] options, int correctSlot) {
            this.options     = options;
            this.correctSlot = correctSlot;
        }
    }
    private Question[] questions;

    // ── Layout (computed in start) ────────────────────────────────────────────
    private int   screenW, screenH;
    private float bgDrawX, bgDrawY, bgDrawW, bgDrawH;
    private float cardW, cardH, cardY;
    private float[] cardX;

    // ── Game state ────────────────────────────────────────────────────────────
    private int   currentQ;
    private float qTimer;
    private boolean gameOver, won, exitRequested;

    // ── Feedback (brief flash after answering) ────────────────────────────────
    private boolean feedbackActive;
    private boolean feedbackCorrect;
    private int     feedbackSlot;   // slot clicked; -1 = timeout
    private float   feedbackTimer;

    // ── UI ────────────────────────────────────────────────────────────────────
    private BitmapFont hudFont, dialogFont;
    private I18NBundle bundle;

    // =========================================================================

    @Override
    public void start() {
        screenW = hust.hedspi.oop.game.utils.Constants.VIRTUAL_WIDTH;
        screenH = hust.hedspi.oop.game.utils.Constants.VIRTUAL_HEIGHT;

        loadAssets();
        buildQuestions();

        hudFont    = ResourceManager.getInstance().hudFont;
        dialogFont = ResourceManager.getInstance().dialogFont;
        bundle     = ResourceManager.getInstance().getBundle();

        // Background: contain fit
        float bgSc = Math.min((float) screenW / bgTexture.getWidth(),
                              (float) screenH / bgTexture.getHeight());
        bgDrawW = bgTexture.getWidth()  * bgSc;
        bgDrawH = bgTexture.getHeight() * bgSc;
        bgDrawX = (screenW - bgDrawW) / 2f;
        bgDrawY = (screenH - bgDrawH) / 2f;

        // Card layout: OPTIONS_PER_Q cards chiếm đều nửa dưới màn hình
        // Tổng chiều rộng dành cho card = screenW - (OPTIONS_PER_Q+1) khoảng đệm
        cardW = (float)(screenW - (OPTIONS_PER_Q + 1) * CARD_PAD) / OPTIONS_PER_Q;
        cardH = screenH / 2f - 2 * CARD_PAD;
        cardY = CARD_PAD;
        cardX = new float[OPTIONS_PER_Q];
        for (int i = 0; i < OPTIONS_PER_Q; i++) {
            cardX[i] = CARD_PAD + i * (cardW + CARD_PAD);
        }

        currentQ       = 0;
        qTimer         = QUESTION_DURATION;
        feedbackActive = false;
        feedbackSlot   = -1;
        gameOver       = false;
        won            = false;
        exitRequested  = false;
    }

    private void loadAssets() {
        bgTexture    = new Texture(Gdx.files.internal(ASSET_BASE + "background.png"));
        timeFrameTex = new Texture(Gdx.files.internal(ASSET_BASE + "timeframe.png"));

        trueTex  = new Texture[3];
        falseTex = new Texture[6];
        for (int i = 0; i < 3; i++)
            trueTex[i]  = new Texture(Gdx.files.internal(ASSET_BASE + "true_"  + (i + 1) + ".png"));
        for (int i = 0; i < 6; i++)
            falseTex[i] = new Texture(Gdx.files.internal(ASSET_BASE + "false_" + (i + 1) + ".png"));

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        dimTexture = new Texture(pix);
        pix.dispose();
    }

    /**
     * Xây dựng QUESTION_COUNT câu hỏi. Mỗi câu: 1 ảnh đúng (xoay vòng trueTex)
     * + (OPTIONS_PER_Q-1) ảnh sai (xoay vòng falseTex), rồi shuffle vị trí.
     */
    private void buildQuestions() {
        questions = new Question[QUESTION_COUNT];
        int fi = 0; // con trỏ xoay vòng vào falseTex
        for (int q = 0; q < QUESTION_COUNT; q++) {
            Texture correct = trueTex[q % trueTex.length];
            Texture[] opts  = new Texture[OPTIONS_PER_Q];
            opts[0] = correct;
            for (int i = 1; i < OPTIONS_PER_Q; i++) {
                opts[i] = falseTex[fi % falseTex.length];
                fi++;
            }
            // Fisher-Yates shuffle
            for (int i = OPTIONS_PER_Q - 1; i > 0; i--) {
                int j = MathUtils.random(i);
                Texture tmp = opts[i]; opts[i] = opts[j]; opts[j] = tmp;
            }
            // Tìm slot đúng sau shuffle
            int correctSlot = 0;
            for (int i = 0; i < OPTIONS_PER_Q; i++) {
                if (opts[i] == correct) { correctSlot = i; break; }
            }
            questions[q] = new Question(opts, correctSlot);
        }
    }

    // =========================================================================

    @Override
    public void update(float dt) {
        if (gameOver) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) exitRequested = true;
            return;
        }

        // Pha feedback: chờ hết FEEDBACK_DURATION rồi chuyển câu hoặc kết thúc
        if (feedbackActive) {
            feedbackTimer -= dt;
            if (feedbackTimer <= 0) {
                feedbackActive = false;
                if (feedbackCorrect) {
                    if (currentQ + 1 >= QUESTION_COUNT) {
                        endGame(true);
                    } else {
                        currentQ++;
                        qTimer = QUESTION_DURATION;
                    }
                } else {
                    endGame(false);
                }
            }
            return;
        }

        // Đếm ngược thời gian câu hỏi
        qTimer -= dt;
        if (qTimer <= 0) {
            // Hết giờ = sai
            triggerFeedback(-1, false);
            return;
        }

        // Click chuột vào card
        if (Gdx.input.justTouched()) {
            float mx = hust.hedspi.oop.game.screens.MinigameScreen.unproject(Gdx.input.getX(), Gdx.input.getY()).x;
            float my = hust.hedspi.oop.game.screens.MinigameScreen.unproject(Gdx.input.getX(), Gdx.input.getY()).y;
            if (my <= screenH / 2f) {
                for (int i = 0; i < OPTIONS_PER_Q; i++) {
                    if (mx >= cardX[i] && mx <= cardX[i] + cardW
                     && my >= cardY    && my <= cardY + cardH) {
                        boolean correct = (i == questions[currentQ].correctSlot);
                        triggerFeedback(i, correct);
                        break;
                    }
                }
            }
        }
    }

    private void triggerFeedback(int slot, boolean correct) {
        feedbackActive  = true;
        feedbackCorrect = correct;
        feedbackSlot    = slot;
        feedbackTimer   = FEEDBACK_DURATION;
    }

    private void endGame(boolean playerWon) {
        won      = playerWon;
        gameOver = true;
        StoryManager.getInstance().recordResult(MinigameID.LOVE_DETECTIVE, won);
    }

    // =========================================================================

    @Override
    public void render(SpriteBatch batch) {
        // ── 1. Background ─────────────────────────────────────────────────────
        batch.draw(bgTexture, bgDrawX, bgDrawY, bgDrawW, bgDrawH);

        // ── 2. Answer cards (nửa dưới) ────────────────────────────────────────
        if (!gameOver) {
            Question q = questions[currentQ];
            for (int i = 0; i < OPTIONS_PER_Q; i++) {
                // Màu viền card theo trạng thái feedback
                Color frameColor = Color.WHITE;
                if (feedbackActive) {
                    if (i == q.correctSlot)              frameColor = Color.GREEN;
                    else if (i == feedbackSlot)          frameColor = Color.RED;
                }

                // Vẽ khung timeframe (scale đầy card)
                batch.setColor(frameColor);
                batch.draw(timeFrameTex, cardX[i], cardY, cardW, cardH);
                batch.setColor(Color.WHITE);

                // Vẽ ảnh đáp án (contain fit trong phần trong của card)
                float innerPad = 8f;
                float areaW = cardW - 2 * innerPad;
                float areaH = cardH - 2 * innerPad;
                Texture tex  = q.options[i];
                float imgSc  = Math.min(areaW / tex.getWidth(), areaH / tex.getHeight());
                float imgW   = tex.getWidth()  * imgSc;
                float imgH   = tex.getHeight() * imgSc;
                float imgX   = cardX[i] + innerPad + (areaW - imgW) / 2f;
                float imgY   = cardY   + innerPad + (areaH - imgH) / 2f;
                batch.draw(tex, imgX, imgY, imgW, imgH);
            }
        }

        // ── 3. HUD: timeframe góc trên-phải ───────────────────────────────────
        float tfScale = 3f;
        float tfW = timeFrameTex.getWidth()  * tfScale;
        float tfH = timeFrameTex.getHeight() * tfScale;
        float tfX = screenW - tfW - 10f;
        float tfY = screenH - tfH - 10f;
        batch.draw(timeFrameTex, tfX, tfY, tfW, tfH);

        float timeLeft = feedbackActive ? 0f : Math.max(0f, qTimer);
        hudFont.setColor(timeLeft < 2f ? Color.RED : Color.WHITE);
        hudFont.draw(batch, String.format("%.0f", timeLeft) + "s",
            tfX + 8f, tfY + tfH * 0.85f);
        hudFont.setColor(Color.WHITE);

        hudFont.draw(batch, (currentQ + 1) + "/" + QUESTION_COUNT,
            tfX + 8f, tfY + tfH * 0.5f);

        // ── 4. Game-over overlay ──────────────────────────────────────────────
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
            bundle.get(won ? "ttt_win" : "ttt_lose"),
            panelX + 20f, panelY + panelH * 0.82f);
        hudFont.setColor(Color.WHITE);

        dialogFont.setColor(0.8f, 0.8f, 0.8f, 1f);
        dialogFont.draw(batch,
            bundle.get("ttt_exit_hint"),
            panelX + 20f, panelY + panelH * 0.35f);
        dialogFont.setColor(Color.WHITE);
    }

    // =========================================================================

    @Override public boolean isFinished() { return exitRequested; }
    @Override public boolean isWon()      { return won; }

    @Override
    public void dispose() {
        safeDispose(bgTexture);
        safeDispose(timeFrameTex);
        safeDispose(dimTexture);
        if (trueTex  != null) for (Texture t : trueTex)  safeDispose(t);
        if (falseTex != null) for (Texture t : falseTex) safeDispose(t);
    }

    private void safeDispose(Texture t) { if (t != null) t.dispose(); }
}