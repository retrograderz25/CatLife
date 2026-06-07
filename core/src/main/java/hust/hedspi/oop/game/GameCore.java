package hust.hedspi.oop.game;

import com.badlogic.gdx.Game;
import hust.hedspi.oop.game.managers.ScreenManager;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.managers.SoundManager;
import hust.hedspi.oop.game.screens.MainMenuScreen;


public class GameCore extends Game {
    @Override
    public void create() {
        ResourceManager.getInstance().initialize(); 
        ScreenManager.getInstance().initialize(this);
        ScreenManager.getInstance().clearAndSetScreen(new MainMenuScreen());
    }

    @Override
    public void dispose() {
        super.dispose();
        SoundManager.getInstance().dispose();
        ResourceManager.getInstance().dispose();
    }
}