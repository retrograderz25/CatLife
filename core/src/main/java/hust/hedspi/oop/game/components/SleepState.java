package hust.hedspi.oop.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.hedspi.oop.game.entities.Cat;

import hust.hedspi.oop.game.utils.Constants;

public class SleepState implements ICatState {
    private float sleepTimer;

    @Override
    public void enter(Cat cat) {
        System.out.println("Cat starts sleeping...");
        sleepTimer = 0f;
    }

    @Override
    public void update(Cat cat, float dt) {
        sleepTimer += dt;
        
        // Tự động thức dậy nếu nhấn Space
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            cat.changeState(new IdleState());
        }
    }

    @Override
    public void render(Cat cat, SpriteBatch batch) {
        cat.renderAnimation(batch, "IDLE", Gdx.graphics.getDeltaTime());
    }

    @Override
    public void exit(Cat cat) {
        System.out.println("Cat woke up!");
    }
}
