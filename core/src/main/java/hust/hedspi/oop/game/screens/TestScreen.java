package hust.hedspi.oop.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import hust.hedspi.oop.game.managers.GameManager;
import hust.hedspi.oop.game.managers.TimeManager;
import hust.hedspi.oop.game.managers.ScreenManager;
import hust.hedspi.oop.game.minigames.RhythmMinigame;
import hust.hedspi.oop.game.utils.IObserver;

public class TestScreen implements Screen, IObserver {
    private SpriteBatch batch;
    private BitmapFont font;
    
    // Cached data for UI (Observer Pattern)
    private String timeString = "";
    private String dayString = "";
    private boolean uiNeedsUpdate = true;

    public TestScreen() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);
        
        // Start game session with StrayCat (true)
        GameManager.getInstance().startNewGame(true);
        
        // Đăng ký nhận thông báo từ TimeManager
        TimeManager.getInstance().addObserver(this);
        
        updateUIData();
    }

    private void updateUIData() {
        TimeManager tm = TimeManager.getInstance();
        timeString = String.format("%02d:%02d", tm.getInGameHour(), tm.getInGameMinute());
        dayString = "Phase: " + tm.getCurrentPhase() + " - " + tm.getCurrentDayOfWeek();
        uiNeedsUpdate = false;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Cập nhật logic các Manager
        TimeManager.getInstance().update(delta);
        GameManager.getInstance().update(delta);

        // Nút tắt mở nhanh Minigame
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            ScreenManager.getInstance().pushScreen(new MinigameScreen(new RhythmMinigame()));
            return; // Tránh render tiếp TestScreen trong frame này
        }

        // UI update based on flags
        if (uiNeedsUpdate) {
            updateUIData();
        }

        // Draw
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);
        
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "--- MANAGER TEST SCREEN ---", 50, Gdx.graphics.getHeight() - 50);
        
        font.setColor(Color.YELLOW);
        font.draw(batch, "Time: " + timeString, 50, Gdx.graphics.getHeight() - 100);
        font.draw(batch, dayString, 50, Gdx.graphics.getHeight() - 150);
        
        font.setColor(Color.GREEN);
        font.draw(batch, "Game State: " + GameManager.getInstance().getCurrentState(), 50, Gdx.graphics.getHeight() - 250);
        
        if (GameManager.getInstance().getPlayer() != null) {
            font.setColor(Color.RED);
            font.draw(batch, "Player HP: " + GameManager.getInstance().getPlayer().getHp(), 50, Gdx.graphics.getHeight() - 300);
        }

        font.setColor(Color.CYAN);
        font.draw(batch, "Press [M] to test Rhythm Minigame", 50, 100);
        
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        TimeManager.getInstance().removeObserver(this);
    }

    @Override
    public void onNotify(Object... args) {
        // Gọi khi TimeManager hoặc các Subject khác bắn Notify
        uiNeedsUpdate = true;
    }
}
