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
import hust.hedspi.oop.game.managers.SoundManager;
import hust.hedspi.oop.game.managers.StoryManager;
import hust.hedspi.oop.game.minigames.IMinigameStrategy;
import hust.hedspi.oop.game.utils.MinigameID;

import java.util.ArrayList;








public class NhayHipHopMinigame implements IMinigameStrategy {

    private static final String ASSET_BASE = "minigames/nhay_hip_hop/";
    private static final float ROUND_DURATION = 10f;
    private static final float SPIN_FRAME_DURATION = 0.15f;
    private static final float SPIN_TOTAL_DURATION = 0.45f;

    private static class Arrow {
        int direction;   
        int colorIndex;  
        boolean isPressed;

        Arrow(int direction) {
            this.direction = direction;
            this.colorIndex = direction; 
            this.isPressed = false;
        }
    }

    private enum DancerState { IDLE, SPIN }

    
    private Texture bgTexture;
    private Texture frameTexture;
    private Texture idleTexture;
    private Texture spin1Texture;
    private Texture spin2Texture;
    private Texture spin3Texture;
    private Texture timeFrameTexture;
    private Texture dimTexture;
    private Texture[] arrowTextures;

    
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

    
    private DancerState dancerState;
    private float spinTimer;
    private float stateTime;

    
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

        
        if (dancerState == DancerState.SPIN) {
            spinTimer += dt;
            if (spinTimer >= SPIN_TOTAL_DURATION) {
                dancerState = DancerState.IDLE;
                spinTimer = 0f;
            }
        }

        
        if (feedbackTimer > 0f) {
            feedbackTimer -= dt;
        }

        
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

        
        int pressedDir = -1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP))) {
            pressedDir = 0; 
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT))) {
            pressedDir = 1; 
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN))) {
            pressedDir = 2; 
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))) {
            pressedDir = 3; 
        }

        if (pressedDir != -1) {
            // check xem người chơi bấm đúng nút mũi tên tiếp theo chưa
            Arrow activeArrow = currentArrows.get(activeArrowIndex);
            if (pressedDir == activeArrow.direction) {
                activeArrow.isPressed = true;
                activeArrowIndex++;

                
                dancerState = DancerState.SPIN;
                spinTimer = 0f;

                
                if (activeArrowIndex >= 10) {
                    roundWon = true;
                    roundOver = true;
                    successfulRounds++;
                    feedbackTimer = 1.2f;
                    feedbackText = "PERFECT!";
                    feedbackColor = Color.GREEN;
                }
            } else {
                
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
        
        batch.draw(bgTexture, 0, 0, screenW, screenH);

        
        float frameW = 300f;
        float frameH = 175f;
        float frameX = (screenW - frameW) / 2f;
        float frameY = 280f;
        batch.draw(frameTexture, frameX, frameY, frameW, frameH);

        
        
        
        float arrowW = 27f;
        float arrowH = 39f;
        float[] slotCentersX = { frameX + 50f, frameX + 100f, frameX + 150f, frameX + 200f, frameX + 250f };
        float[] slotCentersY = { frameY + 120f, frameY + 55f }; 

        for (int i = 0; i < 10; i++) {
            Arrow arrow = currentArrows.get(i);
            if (arrow.isPressed) {
                continue; 
            }

            int col = i % 5;
            int row = i / 5;
            float cx = slotCentersX[col];
            float cy = slotCentersY[row];

            Texture arrowTex = arrowTextures[arrow.colorIndex];
            TextureRegion region = new TextureRegion(arrowTex);

            
            
            float angle = 0f;
            if (arrow.direction == 1) angle = 90f;
            else if (arrow.direction == 2) angle = 180f;
            else if (arrow.direction == 3) angle = 270f;

            
            float scale = 1.0f;
            if (i == activeArrowIndex && !roundOver) {
                scale = 1.0f + 0.12f * MathUtils.sin(stateTime * 10f);
                
                batch.setColor(1f, 1f, 1f, 0.25f);
                batch.draw(dimTexture, cx - arrowW * scale / 2f, cy - arrowH * scale / 2f, arrowW * scale, arrowH * scale);
                batch.setColor(Color.WHITE);
            }

            batch.draw(region, cx - arrowW / 2f, cy - arrowH / 2f, arrowW / 2f, arrowH / 2f, arrowW, arrowH, scale, scale, angle);
        }

        
        float timerBarW = 300f;
        float timerBarH = 10f;
        float timerBarX = (screenW - timerBarW) / 2f;
        float timerBarY = frameY - 20f;

        
        batch.setColor(Color.DARK_GRAY);
        batch.draw(dimTexture, timerBarX, timerBarY, timerBarW, timerBarH);

        
        float percent = roundTimer / ROUND_DURATION;
        if (percent > 0.5f) {
            batch.setColor(Color.GREEN);
        } else if (percent > 0.3f) {
            batch.setColor(Color.YELLOW);
        } else {
            batch.setColor(Color.RED);
        }
        batch.draw(dimTexture, timerBarX, timerBarY, timerBarW * percent, timerBarH);
        batch.setColor(Color.WHITE); 

        
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

        
        String roundText = String.format(bundle.get("hiphop_round"), currentRound);
        String successText = String.format(bundle.get("hiphop_success"), successfulRounds);
        String hintText = bundle.get("hiphop_ctrl_hint");

        
        hudFont.setColor(Color.YELLOW);
        hudFont.draw(batch, roundText, 30f, screenH - 30f);

        
        hudFont.setColor(Color.GREEN);
        float successWidth = 280f; 
        hudFont.draw(batch, successText, screenW - successWidth, screenH - 30f);

        
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

        
        if (feedbackTimer > 0f && !feedbackText.isEmpty()) {
            float alpha = Math.min(1.0f, feedbackTimer / 0.3f);
            hudFont.setColor(feedbackColor.r, feedbackColor.g, feedbackColor.b, alpha);
            
            float textScale = 1.0f + 0.3f * (1.2f - feedbackTimer);
            hudFont.getData().setScale(textScale);
            float textW = 200f * textScale; 
            hudFont.draw(batch, feedbackText, screenW / 2f - textW / 2f, timerBarY - 35f);
            hudFont.getData().setScale(1.0f); 
            hudFont.setColor(Color.WHITE);
        }

        
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
        String resultText = bundle.get(won ? "hiphop_win" : "hiphop_lose");
        hudFont.draw(batch, resultText, panelX, panelY + panelH * 0.76f, panelW, com.badlogic.gdx.utils.Align.center, false);
        hudFont.setColor(Color.WHITE);

        hudFont.setColor(Color.WHITE);
        String finalScore = String.format(bundle.get("hiphop_success"), successfulRounds);
        dialogFont.draw(batch, finalScore, panelX, panelY + panelH * 0.58f, panelW, com.badlogic.gdx.utils.Align.center, false);

        dialogFont.getData().setScale(0.8f);
        dialogFont.setColor(Color.LIGHT_GRAY);
        dialogFont.draw(batch, bundle.get("hiphop_exit_hint"), panelX, panelY + panelH * 0.44f, panelW, com.badlogic.gdx.utils.Align.center, false);
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

    @Override
    public void forceEnd(boolean win) {
        this.won = win;
        this.gameOver = true;
        hust.hedspi.oop.game.managers.StoryManager.getInstance().recordResult(MinigameID.LOVE_HIPHOP, win);
    }
}
