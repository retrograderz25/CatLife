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
        // Chuyển sang trạng thái Chạy (RunState) nếu người chơi bấm phím điều hướng
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.A) ||
            Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            cat.changeState(new RunState());
        }
        
        // Ví dụ: Nhấn phím Z để ngủ
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            cat.changeState(new SleepState());
        }
    }

    @Override
    public void render(Cat cat, SpriteBatch batch) {
        if (cat.getTexture() != null) {
            batch.setColor(Color.WHITE); // Reset color
            batch.draw(cat.getTexture(), cat.getX(), cat.getY(), cat.getWidth(), cat.getHeight());
        }
    }

    @Override
    public void exit(Cat cat) {
    }
}
