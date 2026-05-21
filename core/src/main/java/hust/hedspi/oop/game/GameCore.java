package hust.hedspi.oop.game;

import com.badlogic.gdx.Game;
import hust.hedspi.oop.game.managers.ScreenManager;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.screens.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameCore extends Game {
    @Override
    public void create() {
        ResourceManager.getInstance().initialize(); // Load fonts
        ScreenManager.getInstance().initialize(this);
        ScreenManager.getInstance().clearAndSetScreen(new MainMenuScreen());
    }
    
    @Override
    public void dispose() {
        super.dispose();
        ResourceManager.getInstance().dispose();
    }
}