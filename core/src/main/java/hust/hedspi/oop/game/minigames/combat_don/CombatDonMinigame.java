package hust.hedspi.oop.game.minigames.combat_don;

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
import hust.hedspi.oop.game.utils.MinigameID;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Minigame "Võ Mèo Lang Thang Đơn" / "Combat Don" (GANG_FIGHT_1VN).
 *
 * Chỉ dùng opponent_white.png làm đối thủ và hnad_bicolor.png làm cú đấm cho cả 3 làn đường.
 */
public class CombatDonMinigame implements IMinigameStrategy {

    private static final String ASSET_BASE = "minigames/combat_don/";
    private static final float GAME_DURATION = 45f;

    // ──────────────────────────────────────────────────────────────────────────
    // BẠN CÓ THỂ ĐỔI VỊ TRÍ Y CỦA KHUNG ĐỠ VÀ ĐỐI THỦ TẠI ĐÂY / ADJUST Y POSITIONS HERE:
    // ──────────────────────────────────────────────────────────────────────────
    private static final float TARGET_Y = 75f; // Vị trí Y của khung đỡ đòn (frame.png)
    private static final float TOLERANCE = 45f; // Sai số khoảng cách cho phép để đỡ đòn thành công
    private static final float OPPONENT_Y = 350f; // Vị trí Y của 3 đối thủ ở trên cùng

    // ── Cú đấm (Falling Hand Struct) ──────────────────────────────────────────
    private static class FallingHand {
        int lane;      // 0: Trái, 1: Giữa, 2: Phải
        float y;       // Vị trí Y hiện tại
        float speed;   // Tốc độ rơi (pixels/giây)
        boolean hit;   // Đã đỡ thành công
        boolean missed;// Bị hụt (vượt quá khung đỡ)

        FallingHand(int lane, float y, float speed) {
            this.lane = lane;
            this.y = y;
            this.speed = speed;
            this.hit = false;
            this.missed = false;
        }

        void update(float dt) {
            y -= speed * dt;
        }
    }

    // ── Textures ─────────────────────────────────────────────────────────────
    private Texture bgTexture;
    private Texture frameTexture;
    private Texture catHandTexture;
    private Texture timeFrameTexture;
    private Texture dimTexture;

    // Chỉ dùng 1 loại đối thủ và 1 loại đòn đấm cho cả 3 làn
    private Texture opponentTexture;
    private Texture handTexture;

    // ── Screen size ──────────────────────────────────────────────────────────
    private int screenW, screenH;

    // ── Làn đường & Tọa độ X ─────────────────────────────────────────────────
    private float[] laneX = new float[3];

    // ── Game State ───────────────────────────────────────────────────────────
    private final ArrayList<FallingHand> fallingHands = new ArrayList<>();
    private float spawnTimer;
    private float timer;
    private int missCount;
    private boolean gameOver, won, exitRequested;

    private float[] catHandActiveTimer = new float[3];
    private static final float CAT_HAND_SHOW_DURATION = 0.18f;

    // ── UI ───────────────────────────────────────────────────────────────────
    private BitmapFont hudFont, dialogFont;
    private I18NBundle bundle;

    @Override
    public void start() {
        screenW = Gdx.graphics.getWidth();
        screenH = Gdx.graphics.getHeight();

        laneX[0] = screenW * 0.25f;
        laneX[1] = screenW * 0.50f;
        laneX[2] = screenW * 0.75f;

        loadAssets();

        hudFont = ResourceManager.getInstance().hudFont;
        dialogFont = ResourceManager.getInstance().dialogFont;
        bundle = ResourceManager.getInstance().getBundle();

        fallingHands.clear();
        spawnTimer = 0f;
        timer = 0f;
        missCount = 0;
        gameOver = false;
        won = false;
        exitRequested = false;

        for (int i = 0; i < 3; i++) {
            catHandActiveTimer[i] = 0f;
        }
    }

    private void loadAssets() {
        bgTexture = new Texture(Gdx.files.internal(ASSET_BASE + "background.png"));
        frameTexture = new Texture(Gdx.files.internal(ASSET_BASE + "frame.png"));
        catHandTexture = new Texture(Gdx.files.internal(ASSET_BASE + "cat_hand.png"));
        timeFrameTexture = new Texture(Gdx.files.internal(ASSET_BASE + "timeframe.png"));

        // Chỉ dùng opponent_white.png làm đối thủ duy nhất
        opponentTexture = new Texture(Gdx.files.internal(ASSET_BASE + "opponent_white.png"));

        // Chỉ dùng hnad_bicolor.png làm đòn đấm duy nhất
        handTexture = new Texture(Gdx.files.internal(ASSET_BASE + "cat_punch/hnad_bicolor.png"));

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        dimTexture = new Texture(pix);
        pix.dispose();
    }

    @Override
    public void update(float dt) {
        if (gameOver) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                exitRequested = true;
            }
            return;
        }

        timer += dt;
        if (timer >= GAME_DURATION) {
            endGame(true);
            return;
        }

        for (int i = 0; i < 3; i++) {
            if (catHandActiveTimer[i] > 0f) {
                catHandActiveTimer[i] -= dt;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            handleBlock(0);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            handleBlock(1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            handleBlock(2);
        }

        spawnTimer += dt;
        float currentSpawnInterval = Math.max(0.45f, 1.1f - (timer / GAME_DURATION) * 0.65f);
        if (spawnTimer >= currentSpawnInterval) {
            spawnTimer = 0f;

            int count = 1;
            if (timer > 30f && MathUtils.random() < 0.4f) {
                count = 2;
            }

            for (int i = 0; i < count; i++) {
                int lane = MathUtils.random(0, 2);
                float speed = MathUtils.random(200f, 320f) + (timer / GAME_DURATION) * 80f;
                fallingHands.add(new FallingHand(lane, OPPONENT_Y, speed));
            }
        }

        Iterator<FallingHand> it = fallingHands.iterator();
        while (it.hasNext()) {
            FallingHand hand = it.next();
            hand.update(dt);

            if (!hand.hit && !hand.missed && hand.y < TARGET_Y - TOLERANCE) {
                hand.missed = true;
                missCount++;
                if (missCount >= 5) {
                    endGame(false);
                    return;
                }
            }

            // Xóa cú đấm khi rơi ra khỏi màn hình
            if (hand.y < -50f || hand.hit) {
                it.remove();
            }
        }
    }

    private void handleBlock(int lane) {
        catHandActiveTimer[lane] = CAT_HAND_SHOW_DURATION;

        FallingHand targetHand = null;
        float minDistance = Float.MAX_VALUE;

        for (FallingHand hand : fallingHands) {
            if (hand.lane == lane && !hand.hit && !hand.missed) {
                float dist = Math.abs(hand.y - TARGET_Y);
                if (dist < TOLERANCE && dist < minDistance) {
                    minDistance = dist;
                    targetHand = hand;
                }
            }
        }

        if (targetHand != null) {
            targetHand.hit = true;
        }
    }

    private void endGame(boolean playerWon) {
        won = playerWon;
        gameOver = true;
        StoryManager.getInstance().recordResult(MinigameID.GANG_FIGHT_1VN, won);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(bgTexture, 0, 0, screenW, screenH);

        // Vẽ bệ đỡ đối thủ (kéo giãn ngang frame.png)
        float platformW = laneX[2] - laneX[0] + 120f;
        float platformH = 16f; // Kéo giãn mỏng lại thành bệ đỡ nằm ngang
        batch.draw(frameTexture,
                laneX[0] - 60f,
                OPPONENT_Y - platformH / 2f,
                platformW, platformH);

        // Vẽ duy nhất 1 đối thủ mèo trắng ở giữa đứng trên bệ đỡ (Combat Đơn)
        float oppSize = 100f;
        batch.draw(opponentTexture,
                laneX[1] - oppSize / 2f,
                OPPONENT_Y + platformH / 2f,
                oppSize, oppSize);

        // Vẽ 3 khung đỡ
        float frameSize = 88f;
        for (int i = 0; i < 3; i++) {
            batch.draw(frameTexture,
                    laneX[i] - frameSize / 2f,
                    TARGET_Y - frameSize / 2f,
                    frameSize, frameSize);

            if (catHandActiveTimer[i] > 0f) {
                float pulse = 1.0f + 0.15f * MathUtils.sin(catHandActiveTimer[i] * 15f);
                float handW = frameSize * pulse;
                float handH = frameSize * pulse;
                batch.draw(catHandTexture,
                        laneX[i] - handW / 2f,
                        TARGET_Y - handH / 2f,
                        handW, handH);
            }
        }

        // Vẽ các cú đấm đang rơi (Đều dùng handTexture)
        float handSize = 72f;
        for (FallingHand hand : fallingHands) {
            if (!hand.hit) {
                if (hand.y < TARGET_Y + TOLERANCE && hand.y >= TARGET_Y - TOLERANCE) {
                    batch.setColor(1.0f, 0.8f, 0.8f, 1.0f);
                } else if (hand.y < TARGET_Y - TOLERANCE) {
                    batch.setColor(1.0f, 0.4f, 0.4f, 0.8f);
                }

                batch.draw(handTexture,
                        laneX[hand.lane] - handSize / 2f,
                        hand.y - handSize / 2f,
                        handSize, handSize);

                batch.setColor(Color.WHITE);
            }
        }

        // HUD: Khung thời gian & Thông tin hụt đòn
        float tfScale = 3f;
        float tfW = timeFrameTexture.getWidth() * tfScale;
        float tfH = timeFrameTexture.getHeight() * tfScale;
        float tfX = screenW - tfW - 15f;
        float tfY = screenH - tfH - 15f;

        float timeLeft = Math.max(0f, GAME_DURATION - timer);
        float shakeX = 0f, shakeY = 0f;
        if ((timeLeft < 10f || missCount >= 4) && !gameOver) {
            shakeX = MathUtils.random(-3f, 3f);
            shakeY = MathUtils.random(-3f, 3f);
        }

        batch.draw(timeFrameTexture, tfX + shakeX, tfY + shakeY, tfW, tfH);

        hudFont.setColor(timeLeft < 10f ? Color.RED : Color.WHITE);
        hudFont.draw(batch,
                String.format("%.0f", timeLeft) + bundle.get("combat_don_time_unit"),
                tfX + 8f + shakeX, tfY + tfH * 0.85f + shakeY);
        hudFont.setColor(Color.WHITE);

        String missText = String.format(bundle.get("combat_don_miss_status"), missCount);
        dialogFont.setColor(missCount >= 4 ? Color.RED : Color.WHITE);
        dialogFont.draw(batch, missText, tfX - 120f + shakeX, tfY + tfH * 0.6f + shakeY);
        dialogFont.setColor(Color.WHITE);

        dialogFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
        dialogFont.draw(batch, bundle.get("combat_don_ctrl_hint"), tfX - 160f + shakeX, tfY - 10f + shakeY);
        dialogFont.setColor(Color.WHITE);

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
        hudFont.draw(batch,
                bundle.get(won ? "combat_don_win" : "combat_don_lose"),
                panelX + 25f, panelY + panelH * 0.82f);
        hudFont.setColor(Color.WHITE);

        dialogFont.setColor(0.85f, 0.85f, 0.85f, 1f);
        dialogFont.draw(batch,
                bundle.get("combat_don_exit_hint"),
                panelX + 25f, panelY + panelH * 0.35f);
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
        safeDispose(frameTexture);
        safeDispose(catHandTexture);
        safeDispose(timeFrameTexture);
        safeDispose(dimTexture);
        safeDispose(opponentTexture);
        safeDispose(handTexture);
    }

    private void safeDispose(Texture t) {
        if (t != null) {
            t.dispose();
        }
    }
}
