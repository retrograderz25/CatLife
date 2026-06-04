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
import com.badlogic.gdx.graphics.Texture;
import hust.hedspi.oop.game.screens.hud.InteractionUI;
import hust.hedspi.oop.game.screens.hud.PlayerHUD;
import hust.hedspi.oop.game.screens.hud.TimeHUD;
import hust.hedspi.oop.game.debug.DebugMenu;
import hust.hedspi.oop.game.utils.Constants;

public class PlayScreen implements Screen {
    private OrthographicCamera gameCamera;
    private Viewport gamePort;
    private SpriteBatch batch;
    private TriggerZone currentTrigger = null; 

    // UI
    private Stage uiStage;
    private PlayerHUD playerHUD;
    private TimeHUD timeHUD;
    private InteractionUI interactionUI;
    private DebugMenu debugMenu;
    
    // Icon
    private Texture hasTaskIcon;

    public PlayScreen() {
        batch = new SpriteBatch();
        
        gameCamera = new OrthographicCamera();
        gamePort = new ExtendViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT, gameCamera);
        
        gameCamera.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        gameCamera.zoom = 0.20f; 

        MapManager.getInstance().loadMap("images/HUD/street.tmx");
        
        GameManager.getInstance().startNewGame(true);
        GameManager.getInstance().getPlayer().setPosition(250f, 150f);

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
    }

    @Override
    public void show() {
        // Nhận event click và bàn phím cho UI
        Gdx.input.setInputProcessor(uiStage); 
    }

    @Override
    public void render(float delta) {
        // F12: Bật tắt Debug Menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.F12)) {
            debugMenu.toggle();
        }

        // Nếu UI hội thoại đang mở hoặc Debug Menu đang mở, dừng update logic game
        if (!interactionUI.isVisible() && !debugMenu.isVisible()) {
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

        Cat player = GameManager.getInstance().getPlayer();

        if (player != null && !interactionUI.isVisible()) {
            float playerX = player.getX();
            float playerY = player.getY();
            
            gameCamera.position.x += (playerX - gameCamera.position.x) * 0.1f;
            gameCamera.position.y += (playerY - gameCamera.position.y) * 0.1f;
            
            float camHalfWidth = gamePort.getWorldWidth() * gameCamera.zoom / 2f;
            float camHalfHeight = gamePort.getWorldHeight() * gameCamera.zoom / 2f;
            
            float mapWidth = MapManager.getInstance().getMapPixelWidth();
            float mapHeight = MapManager.getInstance().getMapPixelHeight();
            
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
                            interactionUI.show(zone); // Hiện hội thoại
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

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        MapManager.getInstance().render(gameCamera);

        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();
        
        // Vẽ NPCs (Chỉ vẽ khi TriggerZone tương ứng được phép hoạt động)
        for (NPC npc : MapManager.getInstance().getNpcs()) {
            String zoneName = npc.getNpcName().substring(4); // Cắt bỏ chữ "NPC " để lấy tên Zone
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
            
            // Vẽ icon nhiệm vụ trên đầu mèo (kích thước và vị trí bám sát hitbox)
            if (currentTrigger != null) {
                float iconSize = 10f;
                float iconX = player.getHitbox().x + (player.getHitbox().width - iconSize) / 2f;
                float iconY = player.getY() + 22f; // Đỉnh đầu của sprite mèo rơi vào khoảng y + 18
                batch.draw(hasTaskIcon, iconX, iconY, iconSize, iconSize);
            }
        }
        
        batch.end();
        
        uiStage.draw();
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
        if (batch != null) batch.dispose();
        if (uiStage != null) uiStage.dispose();
        if (playerHUD != null) playerHUD.dispose();
        if (timeHUD != null) timeHUD.dispose();
        if (interactionUI != null) interactionUI.dispose();
        if (debugMenu != null) debugMenu.dispose();
        if (hasTaskIcon != null) hasTaskIcon.dispose();
        // Không gọi MapManager.dispose() hay GameManager.getPlayer().dispose() ở đây
        // vì chúng là Singleton dùng chung, có thể đã được khởi tạo mới bởi Screen khác.
    }
}
