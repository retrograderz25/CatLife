package hust.hedspi.oop.game;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameCore extends Game {
    @Override
    public void create() {
        setScreen(new FirstScreen());
    }
}