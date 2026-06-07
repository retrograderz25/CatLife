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
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.minigames.RhythmMinigame;
import hust.hedspi.oop.game.minigames.cao_mong.CaoMongMinigame;
import hust.hedspi.oop.game.utils.IObserver;

public class TestScreen implements Screen, IObserver {
    private SpriteBatch batch;
    
    
    private String timeString = "";
    private String dayString = "";
    private boolean uiNeedsUpdate = true;

    public TestScreen() {
        batch = new SpriteBatch();
        
        
        GameManager.getInstance().startNewGame(true);
        
        
        TimeManager.getInstance().addObserver(this);
        
        updateUIData();
    }

    private void updateUIData() {
        TimeManager tm = TimeManager.getInstance();
        timeString = String.format("%02d:%02d", tm.getInGameHour(), tm.getInGameMinute());
        dayString = "Giai đoạn: " + tm.getCurrentPhase() + " - " + tm.getCurrentDayOfWeek();
        uiNeedsUpdate = false;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        
        TimeManager.getInstance().update(delta);
        GameManager.getInstance().update(delta);

        
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            ScreenManager.getInstance().pushScreen(new MinigameScreen(new RhythmMinigame()));
            return; 
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            ScreenManager.getInstance().pushScreen(new MinigameScreen(new CaoMongMinigame()));
            return;
        }

        
        if (uiNeedsUpdate) {
            updateUIData();
        }

        
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);
        
        batch.begin();
        
        
        if (GameManager.getInstance().getPlayer() != null) {
            GameManager.getInstance().getPlayer().render(batch);
        }
        
        BitmapFont hudFont = ResourceManager.getInstance().hudFont;
        BitmapFont dialogFont = ResourceManager.getInstance().dialogFont;
        BitmapFont nameFont = ResourceManager.getInstance().nameFont;

        hudFont.draw(batch, "--- THỬ NGHIỆM FONT TIẾNG VIỆT ---", 50, Gdx.graphics.getHeight() - 50);
        
        hudFont.draw(batch, "Thời gian: " + timeString, 50, Gdx.graphics.getHeight() - 100);
        hudFont.draw(batch, dayString, 50, Gdx.graphics.getHeight() - 150);
        
        hudFont.draw(batch, "Trạng thái Game: " + GameManager.getInstance().getCurrentState(), 50, Gdx.graphics.getHeight() - 250);
        
        if (GameManager.getInstance().getPlayer() != null) {
            nameFont.draw(batch, "Mèo Mun (Stray Cat)", 50, Gdx.graphics.getHeight() - 320);
            dialogFont.draw(batch, "Máu: " + GameManager.getInstance().getPlayer().getHp() + " Hành vi: " + GameManager.getInstance().getPlayer().getCurrentState().getClass().getSimpleName(), 50, Gdx.graphics.getHeight() - 350);
        }

        dialogFont.draw(batch, "Nhấn [M] Rhythm | [C] CaoMong | [WASD] Di chuyển | [Z] Ngủ | [Space] Thức", 50, 100);
        
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
        TimeManager.getInstance().removeObserver(this);
        if (GameManager.getInstance().getPlayer() != null) {
            GameManager.getInstance().getPlayer().dispose();
        }
    }

    @Override
    public void onNotify(Object... args) {
        
        uiNeedsUpdate = true;
    }
}
