package hust.hedspi.oop.game.minigames.combat;

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
import hust.hedspi.oop.game.managers.SoundManager;
import hust.hedspi.oop.game.managers.StoryManager;
import hust.hedspi.oop.game.minigames.IMinigameStrategy;
import hust.hedspi.oop.game.utils.MinigameID;

import java.util.ArrayList;
import java.util.Iterator;









public class CombatMinigame implements IMinigameStrategy {

    private static final String ASSET_BASE = "minigames/combat/";
    private static final float GAME_DURATION = 45f;

    
    
    
    
    private static final float TARGET_Y = 75f; 
    
    private static final float TOLERANCE = 45f; 
    private static final float OPPONENT_Y = 350f; 
                                                  

    
    private static class FallingHand {
        int lane; 
        float y; 
        float speed; 
        boolean hit; 
        boolean missed;

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

    
    private Texture bgTexture;
    private Texture frameTexture;
    private Texture catHandTexture;
    private Texture timeFrameTexture;
    private Texture dimTexture;

    
    private Texture[] opponentTextures = new Texture[3];
    private Texture[] handTextures = new Texture[3];

    
    private int screenW, screenH;

    
    private float[] laneX = new float[3];

    
    private final ArrayList<FallingHand> fallingHands = new ArrayList<>();
    private float spawnTimer;
    private float timer;
    private int missCount;
    private int currentMissSoundIndex = 1;
    private boolean gameOver, won, exitRequested;

    
    private float[] catHandActiveTimer = new float[3];
    private static final float CAT_HAND_SHOW_DURATION = 0.18f;

    
    private BitmapFont hudFont, dialogFont;
    private I18NBundle bundle;

    @Override
    public void start() {
        SoundManager.getInstance().playSFX(SoundManager.SFX_CAT_HISS);
        screenW = hust.hedspi.oop.game.utils.Constants.VIRTUAL_WIDTH;
        screenH = hust.hedspi.oop.game.utils.Constants.VIRTUAL_HEIGHT;

        
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
        currentMissSoundIndex = 1;
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

        
        opponentTextures[0] = new Texture(Gdx.files.internal(ASSET_BASE + "opponent_golden.png"));
        opponentTextures[1] = new Texture(Gdx.files.internal(ASSET_BASE + "opponent_gray.png"));
        opponentTextures[2] = new Texture(Gdx.files.internal(ASSET_BASE + "opponent_white.png"));

        
        handTextures[0] = new Texture(Gdx.files.internal(ASSET_BASE + "cat_punch/hand_golden.png"));
        handTextures[1] = new Texture(Gdx.files.internal(ASSET_BASE + "cat_punch/hand_tabby.png"));
        handTextures[2] = new Texture(Gdx.files.internal(ASSET_BASE + "cat_punch/hnad_bicolor.png"));

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        dimTexture = new Texture(pix);
        pix.dispose();
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

        
        for (int i = 0; i < 3; i++) {
            if (catHandActiveTimer[i] > 0f) {
                catHandActiveTimer[i] -= dt;
            }
        }

        
        if ((Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT))) {
            handleBlock(0);
        }
        if ((Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN))) {
            handleBlock(1);
        }
        if ((Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))) {
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
                SoundManager.getInstance().playSFX("sounds/Champion/Champions_2021_Kill_" + currentMissSoundIndex + ".mp3");
                currentMissSoundIndex++;
                if (currentMissSoundIndex > 5) currentMissSoundIndex = 1;
                missCount++;
                if (missCount >= 5) {
                    endGame(false);
                    return;
                }
            }

            
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
            SoundManager.getInstance().playSFX(SoundManager.SFX_FIGHT_PUNCH);
        }
    }

    private void endGame(boolean playerWon) {
        won = playerWon;
        gameOver = true;
        if (playerWon) {
            SoundManager.getInstance().playSFX(SoundManager.SFX_FIGHT_HEAVY);
        } else {
            SoundManager.getInstance().playBGM("sounds/Champion/champions-2021-finisher-music.mp3");
        }
        StoryManager.getInstance().recordResult(MinigameID.GANG_FIGHT_BOSS, won);
    }

    @Override
    public void render(SpriteBatch batch) {
        
        batch.draw(bgTexture, 0, 0, screenW, screenH);

        
        float platformW = laneX[2] - laneX[0] + 120f;
        float platformH = 16f; 
        batch.draw(frameTexture,
                laneX[0] - 60f,
                OPPONENT_Y - platformH / 2f,
                platformW, platformH);

        
        float oppSize = 100f;
        for (int i = 0; i < 3; i++) {
            batch.draw(opponentTextures[i],
                    laneX[i] - oppSize / 2f,
                    OPPONENT_Y + platformH / 2f, 
                    oppSize, oppSize);
        }

        
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

        
        float handSize = 72f;
        for (FallingHand hand : fallingHands) {
            if (!hand.hit) {
                
                if (hand.y < TARGET_Y + TOLERANCE && hand.y >= TARGET_Y - TOLERANCE) {
                    batch.setColor(1.0f, 0.8f, 0.8f, 1.0f);
                } else if (hand.y < TARGET_Y - TOLERANCE) {
                    batch.setColor(1.0f, 0.4f, 0.4f, 0.8f); 
                }

                batch.draw(handTextures[hand.lane],
                        laneX[hand.lane] - handSize / 2f,
                        hand.y - handSize / 2f,
                        handSize, handSize);

                batch.setColor(Color.WHITE);
            }
        }

        
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
                String.format("%.0f", timeLeft) + bundle.get("combat_time_unit"),
                tfX + 8f + shakeX, tfY + tfH * 0.85f + shakeY);
        hudFont.setColor(Color.WHITE);

        
        String missText = String.format(bundle.get("combat_miss_status"), missCount);
        dialogFont.setColor(missCount >= 4 ? Color.RED : Color.WHITE);
        dialogFont.draw(batch, missText, tfX - 120f + shakeX, tfY + tfH * 0.6f + shakeY);
        dialogFont.setColor(Color.WHITE);

        
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
                bundle.get(won ? "combat_win" : "combat_lose"),
                panelX, panelY + panelH * 0.76f, panelW, com.badlogic.gdx.utils.Align.center, false);
        hudFont.setColor(Color.WHITE);

        dialogFont.getData().setScale(0.8f);
        dialogFont.setColor(Color.LIGHT_GRAY);
        dialogFont.draw(batch,
                bundle.get("combat_exit_hint"),
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
        safeDispose(frameTexture);
        safeDispose(catHandTexture);
        safeDispose(timeFrameTexture);
        safeDispose(dimTexture);

        for (int i = 0; i < 3; i++) {
            safeDispose(opponentTextures[i]);
            safeDispose(handTextures[i]);
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
        hust.hedspi.oop.game.managers.StoryManager.getInstance().recordResult(MinigameID.GANG_FIGHT_BOSS, win);
    }
}
