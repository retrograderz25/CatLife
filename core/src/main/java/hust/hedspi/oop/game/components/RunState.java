package hust.hedspi.oop.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.hedspi.oop.game.entities.Cat;

public class RunState implements ICatState {
    private static final float SPEED = 150f;

    @Override
    public void enter(Cat cat) {
        // System.out.println("Cat enters Run State.");
    }

    @Override
    public void update(Cat cat, float dt) {
        boolean isMoving = false;
        float x = cat.getX();
        float y = cat.getY();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) { y += SPEED * dt; isMoving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { y -= SPEED * dt; isMoving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { x -= SPEED * dt; isMoving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { x += SPEED * dt; isMoving = true; }

        cat.setPosition(x, y);

        // Nếu không bấm phím nào, tự động chuyển về trạng thái Đứng yên (IdleState)
        if (!isMoving) {
            cat.changeState(new IdleState());
        }
    }

    @Override
    public void render(Cat cat, SpriteBatch batch) {
        // TODO: Vẽ sprite Mèo đang chạy (Run Animation)
    }

    @Override
    public void exit(Cat cat) {
    }
}
