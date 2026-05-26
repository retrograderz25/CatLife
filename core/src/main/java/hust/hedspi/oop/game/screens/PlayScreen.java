package hust.hedspi.oop.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import hust.hedspi.oop.game.entities.Cat;
import hust.hedspi.oop.game.entities.TriggerZone;
import hust.hedspi.oop.game.managers.GameManager;
import hust.hedspi.oop.game.managers.MapManager;
import hust.hedspi.oop.game.managers.TimeManager;
import hust.hedspi.oop.game.utils.Constants;

public class PlayScreen implements Screen {
    private OrthographicCamera gameCamera;
    private Viewport gamePort;
    private SpriteBatch batch;
    private TriggerZone currentTrigger = null; // Tránh spam log

    public PlayScreen() {
        batch = new SpriteBatch();
        
        // Khởi tạo Camera và Viewport
        gameCamera = new OrthographicCamera();
        // Dùng ExtendViewport để xóa 2 dải đen (Letterboxing) khi thu phóng màn hình
        gamePort = new ExtendViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT, gameCamera);
        
        // Đặt camera ở trung tâm
        gameCamera.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        gameCamera.zoom = 0.20f; // Zoom in mạnh hơn để tạo cảm giác bản đồ rộng lớn

        // Load Map (street.tmx)
        MapManager.getInstance().loadMap("images/HUD/street.tmx");
        
        // Start game session (Player là StrayCat)
        GameManager.getInstance().startNewGame(true);
        // Tạm thời đặt mèo ở giữa màn hình
        GameManager.getInstance().getPlayer().setPosition(Constants.VIRTUAL_WIDTH / 2, Constants.VIRTUAL_HEIGHT / 2);
    }

    @Override
    public void show() {
        // Tương tự hàm start, gọi khi Screen được hiển thị
    }

    @Override
    public void render(float delta) {
        // 1. UPDATE LOGIC
        TimeManager.getInstance().update(delta);
        GameManager.getInstance().update(delta);

        // F11: Chuyển đổi Fullscreen / Windowed 3:4
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            if (Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setWindowedMode(768, 1024); // Kích thước 3:4
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }

        Cat player = GameManager.getInstance().getPlayer();

        // Camera bám theo Player và check TriggerZone
        if (player != null) {
            float playerX = player.getX();
            float playerY = player.getY();
            
            // Lerp camera (di chuyển mượt)
            gameCamera.position.x += (playerX - gameCamera.position.x) * 0.1f;
            gameCamera.position.y += (playerY - gameCamera.position.y) * 0.1f;
            
            // Giới hạn (Clamp) Camera không đi ra ngoài biên của Map
            float camHalfWidth = gamePort.getWorldWidth() * gameCamera.zoom / 2f;
            float camHalfHeight = gamePort.getWorldHeight() * gameCamera.zoom / 2f;
            
            float mapWidth = MapManager.getInstance().getMapPixelWidth();
            float mapHeight = MapManager.getInstance().getMapPixelHeight();
            
            // Tính toán biên
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

            // Kiểm tra va chạm Trigger
            boolean isTouchingAny = false;
            for (TriggerZone zone : MapManager.getInstance().getTriggerZones()) {
                if (player.getHitbox().overlaps(zone.getHitbox())) {
                    isTouchingAny = true;
                    if (currentTrigger != zone) { // Chỉ in log 1 lần khi mới chạm vào
                        currentTrigger = zone;
                        System.out.println("Đã chạm vào Trigger: Mời nhấn [E] (Mô phỏng gọi onInteract)");
                        zone.onInteract(player); // Tự động gọi tương tác luôn để test
                    }
                    break;
                }
            }
            if (!isTouchingAny) {
                currentTrigger = null; // Rời khỏi trigger
            }
        }

        // 2. RENDER GRAPHICS
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Vẽ Map nền trước
        MapManager.getInstance().render(gameCamera);

        // Bật batch để vẽ Entity (Player, NPC)
        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();
        
        if (player != null) {
            player.render(batch);
        }
        
        batch.end();
        
        // TODO: Vẽ UI (HUD) sau cùng
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        batch.dispose();
        MapManager.getInstance().dispose();
        if (GameManager.getInstance().getPlayer() != null) {
            GameManager.getInstance().getPlayer().dispose();
        }
    }
}
