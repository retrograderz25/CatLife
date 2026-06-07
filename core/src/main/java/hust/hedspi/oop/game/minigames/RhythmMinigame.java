package hust.hedspi.oop.game.minigames;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class RhythmMinigame implements IMinigameStrategy {
    private boolean finished;
    private boolean won;
    private float timer;

    @Override
    public void start() {
        System.out.println("Starting Rhythm Minigame!");
        finished = false;
        won = false;
        timer = 0f;
    }

    @Override
    public void update(float dt) {
        if (finished) return;
        
        timer += dt;
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (timer >= 2.0f && timer <= 4.0f) {
                won = true;
                finished = true;
                System.out.println("Rhythm Minigame Won!");
            } else {
                won = false;
                finished = true;
                System.out.println("Rhythm Minigame Lost (Wrong timing)!");
            }
        } else if (timer > 5.0f) {
            won = false;
            finished = true;
            System.out.println("Rhythm Minigame Lost (Timeout)!");
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public boolean isWon() {
        return won;
    }

    @Override
    public void dispose() {
        System.out.println("Disposing Rhythm Minigame resources.");
    }

    @Override
    public void forceEnd(boolean win) {
        this.won = win;
        this.finished = true;
    }
}
