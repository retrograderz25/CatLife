package hust.hedspi.oop.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import hust.hedspi.oop.game.minigames.IMinigameStrategy;
import hust.hedspi.oop.game.managers.ScreenManager;
import hust.hedspi.oop.game.managers.GameManager;
import hust.hedspi.oop.game.utils.Constants;

public class MinigameScreen implements Screen {
    private IMinigameStrategy strategy;
    private SpriteBatch batch;
    private boolean shouldExit = false;

    public static Viewport viewport;
    private OrthographicCamera camera;

    public MinigameScreen(IMinigameStrategy strategy) {
        this.strategy = strategy;
        this.batch = new SpriteBatch();

        this.camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT, camera);
        viewport.apply();

        // Đổi trạng thái game tổng để tạm dừng Map chính
        GameManager.getInstance().pauseGame();
    }

    public static Vector2 unproject(int screenX, int screenY) {
        if (viewport == null) return new Vector2(screenX, screenY);
        Vector3 unprojected = viewport.unproject(new Vector3(screenX, screenY, 0));
        return new Vector2(unprojected.x, unprojected.y);
    }

    @Override
    public void show() {
        strategy.start();
    }

    @Override
    public void render(float delta) {
        if (shouldExit || strategy.isFinished()) {
            ScreenManager.getInstance().popScreen();
            GameManager.getInstance().resumeGame();
            return;
        }

        strategy.update(delta);

        ScreenUtils.clear(0f, 0f, 0f, 1);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        strategy.render(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        strategy.dispose();
        viewport = null;
    }
}
