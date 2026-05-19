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

        if (!screenStack.isEmpty()) {
            Screen current = screenStack.peek();
            // We might want to just hide or let LibGDX handle it
        }
        
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
            } else {
                // Quit game or show empty screen?
            }
        }
    }

    public void setScreen(Screen screen) {
        if (game == null) throw new IllegalStateException("ScreenManager must be initialized with Game instance first!");

        // Clear stack and set new root screen
        while (!screenStack.isEmpty()) {
            Screen old = screenStack.pop();
            old.dispose();
        }
        
        screenStack.push(screen);
        game.setScreen(screen);
    }
}
