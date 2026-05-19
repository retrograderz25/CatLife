package hust.hedspi.oop.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import hust.hedspi.oop.game.minigames.IMinigameStrategy;
import hust.hedspi.oop.game.managers.ScreenManager;
import hust.hedspi.oop.game.managers.GameManager;

public class MinigameScreen implements Screen {
    private IMinigameStrategy strategy;
    private SpriteBatch batch;
    private BitmapFont font;
    private boolean shouldExit = false;

    public MinigameScreen(IMinigameStrategy strategy) {
        this.strategy = strategy;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.getData().setScale(2f);
        
        // Đổi trạng thái game tổng để tạm dừng Map chính
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
        
        font.setColor(Color.WHITE);
        font.draw(batch, "--- MINIGAME SCREEN ---", 50, Gdx.graphics.getHeight() - 50);
        
        if (strategy.isFinished()) {
            String result = strategy.isWon() ? "YOU WON!" : "YOU LOST!";
            font.setColor(strategy.isWon() ? Color.GREEN : Color.RED);
            font.draw(batch, result + " Press ESC to exit.", 50, Gdx.graphics.getHeight() / 2);
            
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
                // Đánh dấu cần thoát để xử lý ở frame tiếp theo (an toàn cho bộ nhớ)
                shouldExit = true;
            }
        } else {
            font.draw(batch, "Press SPACE to hit the note (2-4s)!", 50, Gdx.graphics.getHeight() / 2);
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
        font.dispose();
        strategy.dispose();
    }
}
