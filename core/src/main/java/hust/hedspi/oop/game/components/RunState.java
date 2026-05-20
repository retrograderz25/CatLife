package hust.hedspi.oop.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.hedspi.oop.game.entities.Cat;

public class RunState implements ICatState {

    @Override
    public void enter(Cat cat) {
        // System.out.println("Cat enters Run State.");
    }

    @Override
    public void update(Cat cat, float dt) {
        boolean isMoving = false;
        float x = cat.getX();
        float y = cat.getY();
        float speed = cat.getSpeed();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) { y += speed * dt; isMoving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { y -= speed * dt; isMoving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { x -= speed * dt; isMoving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { x += speed * dt; isMoving = true; }

        cat.setPosition(x, y);

        // Nếu không bấm phím nào, tự động chuyển về trạng thái Đứng yên (IdleState)
        if (!isMoving) {
            cat.changeState(new IdleState());
        }
    }

    @Override
    public void render(Cat cat, SpriteBatch batch) {
        if (cat.getTexture() != null) {
            batch.setColor(Color.WHITE); // Reset color
            // Thêm hiệu ứng bóp méo nhẹ hoặc rung để thể hiện chạy nếu muốn, tạm thời vẽ bình thường
            batch.draw(cat.getTexture(), cat.getX(), cat.getY(), cat.getWidth(), cat.getHeight());
        }
    }

    @Override
    public void exit(Cat cat) {
    }
}
