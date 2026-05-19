package hust.hedspi.oop.game.minigames;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IMinigameStrategy {
    void start();
    void update(float dt);
    void render(SpriteBatch batch);
    boolean isFinished();
    boolean isWon();
    void dispose();
}
