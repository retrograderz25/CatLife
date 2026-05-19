package hust.hedspi.oop.game;

import com.badlogic.gdx.Game;
import hust.hedspi.oop.game.managers.ScreenManager;
import hust.hedspi.oop.game.screens.TestScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameCore extends Game {
    @Override
    public void create() {
        ScreenManager.getInstance().initialize(this);
        ScreenManager.getInstance().setScreen(new TestScreen());
    }
}