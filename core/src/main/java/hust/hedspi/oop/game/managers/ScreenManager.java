package hust.hedspi.oop.game.managers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import java.util.Stack;

public class ScreenManager {
    private static ScreenManager instance;
    private Game game;
    private Stack<Screen> screenStack;

    private ScreenManager() {
        screenStack = new Stack<>();
    }

    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    public void initialize(Game game) {
        this.game = game;
    }

    public void pushScreen(Screen screen) {
        if (game == null) throw new IllegalStateException("ScreenManager must be initialized with Game instance first!");
        
        screenStack.push(screen);
        game.setScreen(screen);
    }

    public void popScreen() {
        if (game == null) throw new IllegalStateException("ScreenManager must be initialized with Game instance first!");

        if (!screenStack.isEmpty()) {
            Screen oldScreen = screenStack.pop();
            oldScreen.dispose();
            
            if (!screenStack.isEmpty()) {
                game.setScreen(screenStack.peek());
            }
        }
    }

    public void clearAndSetScreen(Screen screen) {
        if (game == null) throw new IllegalStateException("ScreenManager must be initialized with Game instance first!");

        while (!screenStack.isEmpty()) {
            Screen old = screenStack.pop();
            old.dispose();
        }
        
        screenStack.push(screen);
        game.setScreen(screen);
    }

    public Screen getCurrentScreen() {
        if (screenStack.isEmpty()) return null;
        return screenStack.peek();
    }
}
