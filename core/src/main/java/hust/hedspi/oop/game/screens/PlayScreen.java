package hust.hedspi.oop.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import hust.hedspi.oop.game.entities.Cat;
import hust.hedspi.oop.game.entities.TriggerZone;
import hust.hedspi.oop.game.entities.NPC;
import hust.hedspi.oop.game.managers.GameManager;
import hust.hedspi.oop.game.managers.MapManager;
import hust.hedspi.oop.game.managers.TimeManager;
import hust.hedspi.oop.game.managers.ScreenManager;
import hust.hedspi.oop.game.managers.SoundManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import hust.hedspi.oop.game.screens.MainMenuScreen;
import hust.hedspi.oop.game.screens.hud.InteractionUI;
import hust.hedspi.oop.game.screens.hud.PlayerHUD;
import hust.hedspi.oop.game.screens.hud.TimeHUD;
import hust.hedspi.oop.game.debug.DebugMenu;
import hust.hedspi.oop.game.screens.EndingScreen;
import hust.hedspi.oop.game.utils.EndingCondition;
import hust.hedspi.oop.game.utils.Constants;

public class PlayScreen implements Screen {
    private OrthographicCamera gameCamera;
    private Viewport gamePort;
    private SpriteBatch batch;
    private TriggerZone currentTrigger = null; 

    
    private Stage uiStage;
    private PlayerHUD playerHUD;
    private TimeHUD timeHUD;
    private InteractionUI interactionUI;
    private DebugMenu debugMenu;
    
    
    private Texture hasTaskIcon;

    
    private boolean isPaused = false;
    private Texture dimTexture;
    private BitmapFont font;
    private int selectedOption = 0; 

    
    private Texture darkLayerTexture;
    private boolean isNight = false;
    private Texture haloTexture;
    private FrameBuffer fbo;
    private OrthographicCamera fboCamera;
    private float mapWidth;
    private float mapHeight;

    public PlayScreen() {
        this(false);
    }

    public PlayScreen(boolean isContinue) {
        batch = new SpriteBatch();
        
        gameCamera = new OrthographicCamera();
        gamePort = new ExtendViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT, gameCamera);
        
        gameCamera.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        gameCamera.zoom = 0.20f; 

        MapManager.getInstance().loadMap("images/HUD/street.tmx");
        
        if (!isContinue || GameManager.getInstance().getPlayer() == null) {
            GameManager.getInstance().startNewGame(true);
            GameManager.getInstance().getPlayer().setPosition(250f, 150f);
        } else {
            GameManager.getInstance().resumeGame();
        }

        uiStage = new Stage(new ScreenViewport(), batch);
        playerHUD = new PlayerHUD();
        timeHUD = new TimeHUD();
        interactionUI = new InteractionUI(GameManager.getInstance().getPlayer());
        debugMenu = new DebugMenu();
        
        uiStage.addActor(playerHUD.getTable());
        uiStage.addActor(timeHUD.getTable());
        uiStage.addActor(interactionUI.getTable());
        uiStage.addActor(debugMenu.getTable());
        
        hasTaskIcon = new Texture(Gdx.files.internal("images/HUD/Cat/has_task(stack_with_cat).png"));

        
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        dimTexture = new Texture(pix);
        pix.dispose();
        font = hust.hedspi.oop.game.managers.ResourceManager.getInstance().dialogFont;

        
        mapWidth = MapManager.getInstance().getMapPixelWidth();
        mapHeight = MapManager.getInstance().getMapPixelHeight();
        
        darkLayerTexture = new Texture(Gdx.files.internal("images/HUD/dark_layer.png"));
        haloTexture = createLightHaloTexture(256, 256);
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, (int)mapWidth, (int)mapHeight, false);
        fboCamera = new OrthographicCamera(mapWidth, mapHeight);
        fboCamera.position.set(mapWidth / 2f, mapHeight / 2f, 0);
        fboCamera.update();
    }

    private Texture createLightHaloTexture(int width, int height) {
        // tạo vòng sáng elip bao quanh con mèo cho đẹp
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        float centerX = width / 2f;
        float centerY = height / 2f;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float dx = (x - centerX) / (width / 2f);
                float dy = (y - centerY) / (height / 2f);
                float distSq = dx * dx + dy * dy;
                
                if (distSq < 1f) {
                    float alpha = 1f - distSq;
                    alpha = alpha * alpha; 
                    alpha = Math.max(0f, Math.min(1f, alpha));
                    
                    pixmap.setColor(1f, 1f, 1f, alpha);
                    pixmap.drawPixel(x, y);
                } else {
                    pixmap.setColor(0f, 0f, 0f, 0f);
                    pixmap.drawPixel(x, y);
                }
            }
        }
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(uiStage);
        isNight = isCurrentlyNight();
        SoundManager.getInstance().playBGM(isNight ? SoundManager.BGM_NIGHT : SoundManager.BGM_DAY);
        SoundManager.getInstance().playAmbient(isNight ? SoundManager.AMB_NIGHT : SoundManager.AMB_DAY);
    }

    private boolean isCurrentlyNight() {
        int hour = TimeManager.getInstance().getInGameHour();
        return hour >= 18 || hour < 5;
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F12)) {
            debugMenu.toggle();
        }

        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !interactionUI.isVisible() && !debugMenu.isVisible()) {
            isPaused = !isPaused;
            selectedOption = 0; 
        }

        if (isPaused) {
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                selectedOption = 0;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                selectedOption = 1;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                if (selectedOption == 0) {
                    isPaused = false;
                } else {
                    ScreenManager.getInstance().clearAndSetScreen(new MainMenuScreen());
                    return;
                }
            }
        }

        if (!interactionUI.isVisible() && !debugMenu.isVisible() && !isPaused) {
            TimeManager.getInstance().update(delta);
            GameManager.getInstance().update(delta);
        }
        
        uiStage.act(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            if (Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setWindowedMode(768, 1024);
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }

        
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            ScreenManager.getInstance().pushScreen(new EndingScreen(new EndingCondition.Builder("Thánh Đổ Vỏ", 0).build(), false));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            ScreenManager.getInstance().pushScreen(new EndingScreen(new EndingCondition.Builder("Gia Đình Hạnh Phúc", 0).build(), false));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            ScreenManager.getInstance().pushScreen(new EndingScreen(new EndingCondition.Builder("Mãi Mãi Kiếp Culi", 0).build(), false));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
            ScreenManager.getInstance().pushScreen(new EndingScreen(new EndingCondition.Builder("Làm Đại Ca Mèo", 0).build(), false));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            ScreenManager.getInstance().pushScreen(new EndingScreen(new EndingCondition.Builder("Hoàng Thượng Có Hoàng Hậu", 0).build(), false));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F6)) {
            ScreenManager.getInstance().pushScreen(new EndingScreen(new EndingCondition.Builder("Hoàng Thượng Thái Giám", 0).build(), false));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F7)) {
            ScreenManager.getInstance().pushScreen(new EndingScreen("Quán Thịt Hổ", false));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F8)) {
            ScreenManager.getInstance().pushScreen(new EndingScreen((EndingCondition) null, false));
            return;
        }

        Cat player = GameManager.getInstance().getPlayer();

        if (player != null && !interactionUI.isVisible() && !isPaused) {
            float playerX = player.getX();
            float playerY = player.getY();
            
            gameCamera.position.x += (playerX - gameCamera.position.x) * 0.1f;
            gameCamera.position.y += (playerY - gameCamera.position.y) * 0.1f;
            
            float camHalfWidth = gamePort.getWorldWidth() * gameCamera.zoom / 2f;
            float camHalfHeight = gamePort.getWorldHeight() * gameCamera.zoom / 2f;
            
            float minX = camHalfWidth;
            float maxX = mapWidth - camHalfWidth;
            float minY = camHalfHeight;
            float maxY = mapHeight - camHalfHeight;
            
            if (maxX < minX) {
                gameCamera.position.x = mapWidth / 2f;
            } else {
                gameCamera.position.x = com.badlogic.gdx.math.MathUtils.clamp(gameCamera.position.x, minX, maxX);
            }
            
            if (maxY < minY) {
                gameCamera.position.y = mapHeight / 2f;
            } else {
                gameCamera.position.y = com.badlogic.gdx.math.MathUtils.clamp(gameCamera.position.y, minY, maxY);
            }

            gameCamera.update();

            boolean isTouchingAny = false;
            for (TriggerZone zone : MapManager.getInstance().getTriggerZones()) {
                if (player.getHitbox().overlaps(zone.getHitbox())) {
                    if (zone.canTrigger()) {
                        isTouchingAny = true;
                        if (currentTrigger != zone) {
                            currentTrigger = zone;
                            interactionUI.show(zone);
                        }
                        break;
                    }
                }
            }
            if (!isTouchingAny) {
                currentTrigger = null;
                interactionUI.hide();
            }
        }

        
        int hour = TimeManager.getInstance().getInGameHour();
        boolean isDark = (hour >= 18 || hour < 5);

        if (isDark != isNight) {
            isNight = isDark;
            SoundManager.getInstance().playBGM(isNight ? SoundManager.BGM_NIGHT : SoundManager.BGM_DAY);
            SoundManager.getInstance().playAmbient(isNight ? SoundManager.AMB_NIGHT : SoundManager.AMB_DAY);
        }

        
        if (player != null && player.getCurrentState() instanceof hust.hedspi.oop.game.components.RunState) {
            SoundManager.getInstance().playSFXThrottled(SoundManager.SFX_CAT_FOOTSTEPS, 0.35f);
        }

        if (isDark && player != null) {
            fboCamera.update();
            fbo.begin();
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.setProjectionMatrix(fboCamera.combined);
            batch.begin();
            batch.draw(darkLayerTexture, 0, 0, mapWidth, mapHeight);

            float haloW = 72f;
            float haloH = 45f;
            float haloX = player.getX() + player.getHitbox().width / 2f - haloW / 2f;
            float haloY = player.getY() + player.getHitbox().height / 2f - haloH / 2f;

            // dùng blend mode để đục một lỗ tròn sáng xung quanh mèo trên lớp bóng tối
            batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);
            batch.draw(haloTexture, haloX, haloY, haloW, haloH);
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            batch.end();
            fbo.end();
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        MapManager.getInstance().render(gameCamera);

        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();
        
        for (NPC npc : MapManager.getInstance().getNpcs()) {
            String zoneName = npc.getNpcName().substring(4);
            boolean isVisible = false;
            for (TriggerZone zone : MapManager.getInstance().getTriggerZones()) {
                if (zone.getZoneName().equals(zoneName)) {
                    isVisible = zone.canTrigger();
                    break;
                }
            }
            if (isVisible) {
                npc.render(batch);
            }
        }

        if (player != null) {
            player.render(batch);
            
            if (currentTrigger != null) {
                float iconSize = 10f;
                float iconX = player.getHitbox().x + (player.getHitbox().width - iconSize) / 2f;
                float iconY = player.getY() + 22f;
                batch.draw(hasTaskIcon, iconX, iconY, iconSize, iconSize);
            }
        }
        
        
        if (isDark) {
            Texture fboTexture = fbo.getColorBufferTexture();
            batch.draw(fboTexture, 0, 0, mapWidth, mapHeight, 0, 0, fboTexture.getWidth(), fboTexture.getHeight(), false, true);

            if (player != null) {
                float glowW = 72f;
                float glowH = 45f;
                float glowX = player.getX() + player.getHitbox().width / 2f - glowW / 2f;
                float glowY = player.getY() + player.getHitbox().height / 2f - glowH / 2f;

                batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
                batch.setColor(1f, 0.9f, 0.6f, 0.15f);
                batch.draw(haloTexture, glowX, glowY, glowW, glowH);
                batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                batch.setColor(Color.WHITE);
            }
        }

        batch.end();
        
        uiStage.draw();

        if (isPaused) {
            
            batch.setProjectionMatrix(uiStage.getViewport().getCamera().combined);
            batch.begin();
            batch.setColor(0f, 0f, 0f, 0.7f);
            batch.draw(dimTexture, 0, 0, uiStage.getViewport().getWorldWidth(), uiStage.getViewport().getWorldHeight());
            batch.setColor(Color.WHITE);
            
            float vw = uiStage.getViewport().getWorldWidth();
            float vh = uiStage.getViewport().getWorldHeight();

            font.getData().setScale(1.5f);
            font.setColor(Color.YELLOW);
            font.draw(batch, "BẠN CÓ MUỐN QUAY LẠI MENU?", 0, vh / 2f + 100, vw, Align.center, false);
            
            font.getData().setScale(1.2f);
            font.setColor(selectedOption == 0 ? Color.YELLOW : Color.WHITE);
            font.draw(batch, selectedOption == 0 ? "> Chơi tiếp <" : "Chơi tiếp", 0, vh / 2f + 20, vw, Align.center, false);
            
            font.setColor(selectedOption == 1 ? Color.YELLOW : Color.WHITE);
            font.draw(batch, selectedOption == 1 ? "> Quay lại Menu <" : "Quay lại Menu", 0, vh / 2f - 40, vw, Align.center, false);
            
            font.getData().setScale(1.0f);
            font.setColor(Color.WHITE);
            batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        SoundManager.getInstance().stopBGM();
        SoundManager.getInstance().stopAmbient();
        if (batch != null) batch.dispose();
        if (uiStage != null) uiStage.dispose();
        if (playerHUD != null) playerHUD.dispose();
        if (timeHUD != null) timeHUD.dispose();
        if (interactionUI != null) interactionUI.dispose();
        if (debugMenu != null) debugMenu.dispose();
        if (hasTaskIcon != null) hasTaskIcon.dispose();
        if (darkLayerTexture != null) darkLayerTexture.dispose();
        if (haloTexture != null) haloTexture.dispose();
        if (fbo != null) fbo.dispose();
        if (dimTexture != null) dimTexture.dispose();
    }
}
