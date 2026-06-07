package hust.hedspi.oop.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.hedspi.oop.game.entities.Cat;

public class IdleState implements ICatState {
    @Override
    public void enter(Cat cat) {
        
    }

    @Override
    public void update(Cat cat, float dt) {
        float dx = 0;
        float dy = 0;

        if ((Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))) { dy += 1; }
        if ((Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))) { dy -= 1; }
        if ((Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))) { dx -= 1; }
        if ((Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))) { dx += 1; }

        
        if (dx != 0 || dy != 0) {
            cat.changeState(new RunState());
        }
        
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            cat.changeState(new SleepState());
        }
    }

    @Override
    public void render(Cat cat, SpriteBatch batch) {
        cat.renderAnimation(batch, "IDLE", Gdx.graphics.getDeltaTime());
    }

    @Override
    public void exit(Cat cat) {
    }
}
