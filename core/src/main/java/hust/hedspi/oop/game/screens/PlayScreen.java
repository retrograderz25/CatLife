package hust.hedspi.oop.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import hust.hedspi.oop.game.managers.GameManager;
import hust.hedspi.oop.game.managers.TimeManager;
import hust.hedspi.oop.game.screens.hud.PlayerHUD;
import hust.hedspi.oop.game.screens.hud.TimeHUD;
import hust.hedspi.oop.game.utils.Constants;

public class PlayScreen implements Screen {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage hudStage;
    
    private TimeHUD timeHUD;
    private PlayerHUD playerHUD;

    public PlayScreen() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // Game Camera & Viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT, camera);
        camera.position.set(Constants.VIRTUAL_WIDTH / 2f, Constants.VIRTUAL_HEIGHT / 2f, 0);

        // HUD Stage
        hudStage = new Stage(new FitViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT));
        
        // Khởi tạo các thành phần HUD
        timeHUD = new TimeHUD();
        playerHUD = new PlayerHUD();
        
        // Thêm vào Stage
        hudStage.addActor(timeHUD.getTable());
        hudStage.addActor(playerHUD.getTable());
        
        Gdx.input.setInputProcessor(hudStage);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(hudStage);
    }

    @Override
    public void render(float delta) {
        // Cập nhật Logic
        TimeManager.getInstance().update(delta);
        GameManager.getInstance().update(delta);
        camera.update();

        // Xóa màn hình với màu xanh lá cây tượng trưng cho bãi cỏ (Map placeholder)
        ScreenUtils.clear(0.3f, 0.7f, 0.3f, 1);

        // Đặt ma trận chiếu cho game map
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        // Render "Map" placeholder bằng ShapeRenderer (Đường đi, v.v.)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.LIGHT_GRAY);
        // Vẽ một con đường chữ thập làm điểm nhấn
        shapeRenderer.rect(0, Constants.VIRTUAL_HEIGHT / 2 - 50, Constants.VIRTUAL_WIDTH, 100);
        shapeRenderer.rect(Constants.VIRTUAL_WIDTH / 2 - 50, 0, 100, Constants.VIRTUAL_HEIGHT);
        shapeRenderer.end();

        // Vẽ Entities (Mèo)
        batch.begin();
        if (GameManager.getInstance().getPlayer() != null) {
            GameManager.getInstance().getPlayer().render(batch);
        }
        batch.end();

        // Vẽ HUD lên trên cùng
        hudStage.act(delta);
        hudStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        hudStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        GameManager.getInstance().pauseGame();
    }

    @Override
    public void resume() {
        GameManager.getInstance().resumeGame();
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        hudStage.dispose();
        if (timeHUD != null) timeHUD.dispose();
        if (playerHUD != null) playerHUD.dispose();
    }
}
