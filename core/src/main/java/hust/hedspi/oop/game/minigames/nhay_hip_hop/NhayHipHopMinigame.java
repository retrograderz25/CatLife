package hust.hedspi.oop.game.minigames.nhay_hip_hop;

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
import hust.hedspi.oop.game.managers.StoryManager;
import hust.hedspi.oop.game.minigames.IMinigameStrategy;
import hust.hedspi.oop.game.utils.MinigameID;

import java.util.ArrayList;

/**
 * Minigame "Nhay Hip Hop" (LOVE_HIPHOP).
 * 
 * Người chơi cần bấm đúng chuỗi 10 mũi tên (2 hàng, mỗi hàng 5 mũi tên) trong 10 giây.
 * Có tổng cộng 5 lượt chơi độc lập. Thắng tối thiểu 3 lượt để chiến thắng minigame.
 * Hoạt ảnh nhân vật (idle.png và spin1-3.png) hiển thị phía dưới khung mũi tên.
 */
public class NhayHipHopMinigame implements IMinigameStrategy {

    private static final String ASSET_BASE = "minigames/nhay_hip_hop/";
    private static final float ROUND_DURATION = 10f;
    private static final float SPIN_FRAME_DURATION = 0.15f;
    private static final float SPIN_TOTAL_DURATION = 0.45f;

    private static class Arrow {
        int direction;   // 0 = UP, 1 = LEFT, 2 = DOWN, 3 = RIGHT
        int colorIndex;  // Determined by direction: 0 = yellow, 1 = blue, 2 = green, 3 = red
        boolean isPressed;

        Arrow(int direction) {
            this.direction = direction;
            this.colorIndex = direction; // 1-to-1 mapping: UP->yellow, LEFT->blue, DOWN->green, RIGHT->red
            this.isPressed = false;
        }
    }

    private enum DancerState { IDLE, SPIN }

    // Textures
    private Texture bgTexture;
    private Texture frameTexture;
    private Texture idleTexture;
    private Texture spin1Texture;
    private Texture spin2Texture;
    private Texture spin3Texture;
    private Texture timeFrameTexture;
    private Texture dimTexture;
    private Texture[] arrowTextures;

    // Game state variables
    private int screenW, screenH;
    private int currentRound;
    private int successfulRounds;
    private float roundTimer;
    private boolean roundOver;
    private boolean roundWon;
    private boolean gameOver;
    private boolean won;
    private boolean exitRequested;

    private final ArrayList<Arrow> currentArrows = new ArrayList<>();
    private int activeArrowIndex;

    // Dancer animation state
    private DancerState dancerState;
    private float spinTimer;
    private float stateTime;

    // Feedback message overlay
    private float feedbackTimer;
    private String feedbackText = "";
    private Color feedbackColor = Color.WHITE;

    private BitmapFont hudFont;
    private BitmapFont dialogFont;
    private I18NBundle bundle;

    @Override
    public void start() {
        screenW = hust.hedspi.oop.game.utils.Constants.VIRTUAL_WIDTH;
        screenH = hust.hedspi.oop.game.utils.Constants.VIRTUAL_HEIGHT;

        loadAssets();

        hudFont = ResourceManager.getInstance().hudFont;
        dialogFont = ResourceManager.getInstance().dialogFont;
        bundle = ResourceManager.getInstance().getBundle();

        successfulRounds = 0;
        gameOver = false;
        won = false;
        exitRequested = false;

        dancerState = DancerState.IDLE;
        spinTimer = 0f;
        stateTime = 0f;

        feedbackTimer = 0f;
        feedbackText = "";

        startRound(1);
    }

    private void loadAssets() {
        bgTexture = new Texture(Gdx.files.internal(ASSET_BASE + "background.png"));
        frameTexture = new Texture(Gdx.files.internal(ASSET_BASE + "frame.png"));
        idleTexture = new Texture(Gdx.files.internal(ASSET_BASE + "idle.png"));
        spin1Texture = new Texture(Gdx.files.internal(ASSET_BASE + "spin1.png"));
        spin2Texture = new Texture(Gdx.files.internal(ASSET_BASE + "spin2.png"));
        spin3Texture = new Texture(Gdx.files.internal(ASSET_BASE + "spin3.png"));
        timeFrameTexture = new Texture(Gdx.files.internal(ASSET_BASE + "timeframe.png"));

        arrowTextures = new Texture[4];
        arrowTextures[0] = new Texture(Gdx.files.internal(ASSET_BASE + "arrow/yellow.png"));
        arrowTextures[1] = new Texture(Gdx.files.internal(ASSET_BASE + "arrow/blue.png"));
        arrowTextures[2] = new Texture(Gdx.files.internal(ASSET_BASE + "arrow/green.png"));
        arrowTextures[3] = new Texture(Gdx.files.internal(ASSET_BASE + "arrow/red.png"));

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        dimTexture = new Texture(pix);
        pix.dispose();
    }

    private void startRound(int roundNum) {
        currentRound = roundNum;
        activeArrowIndex = 0;
        roundTimer = ROUND_DURATION;
        roundOver = false;
        roundWon = false;

        currentArrows.clear();
        for (int i = 0; i < 10; i++) {
            int randomDir = MathUtils.random(0, 3);
            currentArrows.add(new Arrow(randomDir));
        }
    }

    @Override
    public void update(float dt) {
        stateTime += dt;

        if (gameOver) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                exitRequested = true;
            }
            return;
        }

        // Dancer animation updates
        if (dancerState == DancerState.SPIN) {
            spinTimer += dt;
            if (spinTimer >= SPIN_TOTAL_DURATION) {
                dancerState = DancerState.IDLE;
                spinTimer = 0f;
            }
        }

        // Update feedback message timer
        if (feedbackTimer > 0f) {
            feedbackTimer -= dt;
        }

        // If currently displaying feedback from the end of a round
        if (roundOver) {
            if (feedbackTimer <= 0f) {
                if (currentRound < 5) {
                    startRound(currentRound + 1);
                } else {
                    endGame();
                }
            }
            return;
        }

        // Round timer countdown
        roundTimer -= dt;
        if (roundTimer <= 0f) {
            roundTimer = 0f;
            roundOver = true;
            roundWon = false;
            feedbackTimer = 1.2f;
            feedbackText = "TIME'S UP!";
            feedbackColor = Color.RED;
            dancerState = DancerState.IDLE;
            return;
        }

        // Input processing
        int pressedDir = -1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            pressedDir = 0; // UP
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            pressedDir = 1; // LEFT
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            pressedDir = 2; // DOWN
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            pressedDir = 3; // RIGHT
        }

        if (pressedDir != -1) {
            Arrow activeArrow = currentArrows.get(activeArrowIndex);
            if (pressedDir == activeArrow.direction) {
                // Correct key pressed!
                activeArrow.isPressed = true;
                activeArrowIndex++;

                // Trigger spin animation
                dancerState = DancerState.SPIN;
                spinTimer = 0f;

                // Check if the whole sequence is completed
                if (activeArrowIndex >= 10) {
                    roundWon = true;
                    roundOver = true;
                    successfulRounds++;
                    feedbackTimer = 1.2f;
                    feedbackText = "PERFECT!";
                    feedbackColor = Color.GREEN;
                }
            } else {
                // Wrong key pressed! Reset sequence of arrows for this round
                for (Arrow arrow : currentArrows) {
                    arrow.isPressed = false;
                }
                activeArrowIndex = 0;
                dancerState = DancerState.IDLE;

                feedbackTimer = 0.5f;
                feedbackText = "MISS!";
                feedbackColor = Color.RED;
            }
        }
    }

    private void endGame() {
        won = successfulRounds >= 3;
        gameOver = true;
        StoryManager.getInstance().recordResult(MinigameID.LOVE_HIPHOP, won);
    }

    @Override
    public void render(SpriteBatch batch) {
        // 1. Background
        batch.draw(bgTexture, 0, 0, screenW, screenH);

        // 2. Frame container for arrows (centered at the top half)
        float frameW = 300f;
        float frameH = 175f;
        float frameX = (screenW - frameW) / 2f;
        float frameY = 280f;
        batch.draw(frameTexture, frameX, frameY, frameW, frameH);

        // 3. Render 10 Arrows arranged in 2 rows of 5
        // Row 1 (top): indexes 0 to 4
        // Row 2 (bottom): indexes 5 to 9
        float arrowW = 27f;
        float arrowH = 39f;
        float[] slotCentersX = { frameX + 50f, frameX + 100f, frameX + 150f, frameX + 200f, frameX + 250f };
        float[] slotCentersY = { frameY + 120f, frameY + 55f }; // Top row, Bottom row

        for (int i = 0; i < 10; i++) {
            Arrow arrow = currentArrows.get(i);
            if (arrow.isPressed) {
                continue; // Disappear when successfully pressed
            }

            int col = i % 5;
            int row = i / 5;
            float cx = slotCentersX[col];
            float cy = slotCentersY[row];

            Texture arrowTex = arrowTextures[arrow.colorIndex];
            TextureRegion region = new TextureRegion(arrowTex);

            // Calculate rotation angle
            // 0 = UP (0 deg), 1 = LEFT (90 deg), 2 = DOWN (180 deg), 3 = RIGHT (270 deg)
            float angle = 0f;
            if (arrow.direction == 1) angle = 90f;
            else if (arrow.direction == 2) angle = 180f;
            else if (arrow.direction == 3) angle = 270f;

            // Highlight the active arrow with a pulsing scale effect
            float scale = 1.0f;
            if (i == activeArrowIndex && !roundOver) {
                scale = 1.0f + 0.12f * MathUtils.sin(stateTime * 10f);
                // Draw a subtle background indicator for active arrow
                batch.setColor(1f, 1f, 1f, 0.25f);
                batch.draw(dimTexture, cx - arrowW * scale / 2f, cy - arrowH * scale / 2f, arrowW * scale, arrowH * scale);
                batch.setColor(Color.WHITE);
            }

            batch.draw(region, cx - arrowW / 2f, cy - arrowH / 2f, arrowW / 2f, arrowH / 2f, arrowW, arrowH, scale, scale, angle);
        }

        // 4. Render Timer Bar right under the arrow frame
        float timerBarW = 300f;
        float timerBarH = 10f;
        float timerBarX = (screenW - timerBarW) / 2f;
        float timerBarY = frameY - 20f;

        // Draw Timer Bar Background (Dark Gray)
        batch.setColor(Color.DARK_GRAY);
        batch.draw(dimTexture, timerBarX, timerBarY, timerBarW, timerBarH);

        // Draw Timer Bar Foreground based on remaining time
        float percent = roundTimer / ROUND_DURATION;
        if (percent > 0.5f) {
            batch.setColor(Color.GREEN);
        } else if (percent > 0.3f) {
            batch.setColor(Color.YELLOW);
        } else {
            batch.setColor(Color.RED);
        }
        batch.draw(dimTexture, timerBarX, timerBarY, timerBarW * percent, timerBarH);
        batch.setColor(Color.WHITE); // Reset color to white

        // 5. Render Dancer Character (centered below the frame)
        Texture activeDancerTex = idleTexture;
        if (dancerState == DancerState.SPIN) {
            int frameIdx = (int) (spinTimer / SPIN_FRAME_DURATION);
            if (frameIdx == 0) activeDancerTex = spin1Texture;
            else if (frameIdx == 1) activeDancerTex = spin2Texture;
            else activeDancerTex = spin3Texture;
        }

        float dancerScale = 2.5f;
        float dW = activeDancerTex.getWidth() * dancerScale;
        float dH = activeDancerTex.getHeight() * dancerScale;
        float dX = screenW / 2f - dW / 2f;
        float dY = 80f;
        batch.draw(activeDancerTex, dX, dY, dW, dH);

        // 6. Render HUD labels (Round, Successes, Instructions)
        String roundText = String.format(bundle.get("hiphop_round"), currentRound);
        String successText = String.format(bundle.get("hiphop_success"), successfulRounds);
        String hintText = bundle.get("hiphop_ctrl_hint");

        // Top-left: Round indicator
        hudFont.setColor(Color.YELLOW);
        hudFont.draw(batch, roundText, 30f, screenH - 30f);

        // Top-right: Success count
        hudFont.setColor(Color.GREEN);
        float successWidth = 280f; // estimated width
        hudFont.draw(batch, successText, screenW - successWidth, screenH - 30f);

        // Bottom-left: Controls instruction hint
        dialogFont.setColor(Color.LIGHT_GRAY);
        dialogFont.draw(batch, hintText, 30f, 40f);

        // Bottom-right: Timer frame & Time text
        float tfScale = 3.5f;
        float tfW = timeFrameTexture.getWidth() * tfScale;
        float tfH = timeFrameTexture.getHeight() * tfScale;
        float tfX = screenW - tfW - 30f;
        float tfY = 30f;
        batch.draw(timeFrameTexture, tfX, tfY, tfW, tfH);

        hudFont.setColor(roundTimer < 3f ? Color.RED : Color.WHITE);
        String timeStr = String.format("%.1f", roundTimer) + bundle.get("hiphop_time_unit");
        hudFont.draw(batch, timeStr, tfX + 18f, tfY + tfH * 0.68f);
        hudFont.setColor(Color.WHITE);

        // 7. Render Success/Miss Feedback Message (Fades in/out)
        if (feedbackTimer > 0f && !feedbackText.isEmpty()) {
            float alpha = Math.min(1.0f, feedbackTimer / 0.3f);
            hudFont.setColor(feedbackColor.r, feedbackColor.g, feedbackColor.b, alpha);
            // Centered overlay text
            float textScale = 1.0f + 0.3f * (1.2f - feedbackTimer);
            hudFont.getData().setScale(textScale);
            float textW = 200f * textScale; // approximation
            hudFont.draw(batch, feedbackText, screenW / 2f - textW / 2f, timerBarY - 35f);
            hudFont.getData().setScale(1.0f); // Reset font scale
            hudFont.setColor(Color.WHITE);
        }

        // 8. Render Game Over overlay screen
        if (gameOver) {
            renderGameOver(batch);
        }
    }

    private void renderGameOver(SpriteBatch batch) {
        // Dim the background
        batch.setColor(0f, 0f, 0f, 0.72f);
        batch.draw(dimTexture, 0, 0, screenW, screenH);
        batch.setColor(Color.WHITE);

        float panelW = screenW * 0.45f;
        float panelH = screenH * 0.45f;
        float panelX = (screenW - panelW) / 2f;
        float panelY = (screenH - panelH) / 2f;

        // Draw a panel background
        batch.draw(timeFrameTexture, panelX, panelY, panelW, panelH);

        hudFont.setColor(won ? Color.GREEN : Color.RED);
        String resultText = bundle.get(won ? "hiphop_win" : "hiphop_lose");
        hudFont.draw(batch, resultText, panelX + 30f, panelY + panelH * 0.8f);

        hudFont.setColor(Color.WHITE);
        String finalScore = String.format(bundle.get("hiphop_success"), successfulRounds);
        dialogFont.draw(batch, finalScore, panelX + 30f, panelY + panelH * 0.55f);
        dialogFont.draw(batch, bundle.get("hiphop_exit_hint"), panelX + 30f, panelY + panelH * 0.3f);
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
        safeDispose(idleTexture);
        safeDispose(spin1Texture);
        safeDispose(spin2Texture);
        safeDispose(spin3Texture);
        safeDispose(timeFrameTexture);
        safeDispose(dimTexture);

        if (arrowTextures != null) {
            for (Texture t : arrowTextures) {
                safeDispose(t);
            }
        }
    }

    private void safeDispose(Texture t) {
        if (t != null) {
            t.dispose();
        }
    }
}
