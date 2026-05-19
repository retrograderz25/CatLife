package hust.hedspi.oop.game.managers;

import hust.hedspi.oop.game.entities.Cat;
import hust.hedspi.oop.game.utils.GameState;

public class GameManager {
    private static GameManager instance;
    
    private GameState currentState;
    private Cat player;

    private GameManager() {
        currentState = GameState.PLAYING;
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void initNewGame() {
        StoryManager.getInstance().reset();
        TimeManager.getInstance().reset();
        
        // TODO: Initialize specific Cat based on selection (StrayCat or HouseCat)
        player = new hust.hedspi.oop.game.entities.StrayCat(0, 0, 32, 32);
        player.setHp(100);
        
        currentState = GameState.PLAYING;
    }

    public void update(float dt) {
        if (currentState != GameState.PLAYING) {
            return;
        }

        // Logic Sinh tử (Life & Death Cycle)
        if (player != null && player.getHp() <= 0) {
            handleGameOver();
        }
    }

    private void handleGameOver() {
        currentState = GameState.GAME_OVER;
        // Giao cho StoryManager hiển thị Bad Ending hoặc Game Over
        System.out.println("Game Over Triggered!");
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setGameState(GameState state) {
        this.currentState = state;
    }

    public Cat getPlayer() {
        return player;
    }
}
