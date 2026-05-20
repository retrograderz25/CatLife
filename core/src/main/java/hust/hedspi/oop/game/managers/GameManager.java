package hust.hedspi.oop.game.managers;

import hust.hedspi.oop.game.entities.Cat;
import hust.hedspi.oop.game.entities.HouseCat;
import hust.hedspi.oop.game.entities.StrayCat;
import hust.hedspi.oop.game.utils.GameState;

public class GameManager {
    private static GameManager instance;
    
    private GameState currentState;
    private Cat player;

    private GameManager() {
        currentState = GameState.MENU;
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void startNewGame(boolean isStrayCat) {
        StoryManager.getInstance().resetStoryFlags();
        TimeManager.getInstance().resetTime();
        
        if (isStrayCat) {
            player = new StrayCat(400, 300, 64, 64); // Center of screen, larger
        } else {
            player = new HouseCat(400, 300, 64, 64);
        }
        
        currentState = GameState.PLAYING;
    }

    public void update(float dt) {
        if (currentState != GameState.PLAYING) {
            return;
        }

        if (player != null) {
            player.update(dt);
            
            // Logic Sinh tử (Life & Death Cycle)
            if (player.getHp() <= 0) {
                currentState = GameState.GAME_OVER;
                StoryManager.getInstance().evaluateFinalEnding(player);
                System.out.println("Game Over Triggered!");
            }
        }
    }

    public void pauseGame() {
        if (currentState == GameState.PLAYING) {
            currentState = GameState.PAUSED;
        }
    }

    public void resumeGame() {
        if (currentState == GameState.PAUSED) {
            currentState = GameState.PLAYING;
        }
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public Cat getPlayer() {
        return player;
    }
}
