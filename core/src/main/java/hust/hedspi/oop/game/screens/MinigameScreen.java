package hust.hedspi.oop.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import hust.hedspi.oop.game.minigames.IMinigameStrategy;
import hust.hedspi.oop.game.managers.ScreenManager;
import hust.hedspi.oop.game.managers.GameManager;

public class MinigameScreen implements Screen {
    private IMinigameStrategy strategy;
    private SpriteBatch batch;
    private boolean shouldExit = false;

    public MinigameScreen(IMinigameStrategy strategy) {
        this.strategy = strategy;
        this.batch = new SpriteBatch();
        GameManager.getInstance().pauseGame();
    }

    @Override
    public void show() {
        strategy.start();
    }

    @Override
    public void render(float delta) {
        if (shouldExit) {
            ScreenManager.getInstance().popScreen();
            GameManager.getInstance().resumeGame();
            return;
        }

        strategy.update(delta);

        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
        
        batch.begin();
        strategy.render(batch);

        if (strategy.isFinished()) {
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
                shouldExit = true;
            }
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

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
    }
}
