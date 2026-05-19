package hust.hedspi.oop.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.hedspi.oop.game.entities.Cat;

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
        
        // Hồi phục 5 Energy mỗi giây thực
        if (sleepTimer >= 1.0f) {
            cat.increaseEnergy(5);
            sleepTimer -= 1.0f;
        }

        // Tự động thức dậy nếu đầy Energy hoặc nhấn Space
        if (cat.getEnergy() >= 100 || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            cat.changeState(new IdleState());
        }
    }

    @Override
    public void render(Cat cat, SpriteBatch batch) {
        // TODO: Vẽ sprite Mèo đang ngủ (Sleep Animation)
        // và vẽ chữ "Zzz..." bay lên
    }

    @Override
    public void exit(Cat cat) {
        System.out.println("Cat woke up!");
    }
}
