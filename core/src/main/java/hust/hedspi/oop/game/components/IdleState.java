package hust.hedspi.oop.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.hedspi.oop.game.entities.Cat;

public class IdleState implements ICatState {
    @Override
    public void enter(Cat cat) {
        // System.out.println("Cat enters Idle State.");
    }

    @Override
    public void update(Cat cat, float dt) {
        float dx = 0;
        float dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) { dy += 1; }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { dy -= 1; }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { dx -= 1; }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { dx += 1; }

        // Chuyển sang trạng thái Chạy (RunState) nếu người chơi có hướng di chuyển thực sự
        if (dx != 0 || dy != 0) {
            cat.changeState(new RunState());
        }
        
        // Ví dụ: Nhấn phím Z để ngủ
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
