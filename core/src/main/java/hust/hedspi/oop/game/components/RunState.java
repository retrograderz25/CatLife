package hust.hedspi.oop.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import hust.hedspi.oop.game.entities.Cat;
import hust.hedspi.oop.game.managers.MapManager;

public class RunState implements ICatState {

    @Override
    public void enter(Cat cat) {
    }

    @Override
    public void update(Cat cat, float dt) {
        boolean isMoving = false;
        float x = cat.getX();
        float y = cat.getY();
        float speed = cat.getSpeed();

        float dx = 0;
        float dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) { dy += 1; }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { dy -= 1; }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { 
            dx -= 1; 
            cat.setFacingRight(false);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { 
            dx += 1; 
            cat.setFacingRight(true);
        }

        if (dx != 0 || dy != 0) {
            isMoving = true;
            // Normalize for diagonal movement
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            dx /= length;
            dy /= length;
            
            float moveX = dx * speed * dt;
            float moveY = dy * speed * dt;

            // Map boundaries
            float mapWidth = MapManager.getInstance().getMapPixelWidth();
            float mapHeight = MapManager.getInstance().getMapPixelHeight();
            
            float hitW = cat.getHitbox().width;
            float hitH = cat.getHitbox().height;

            // 1. Test X-axis movement
            float newX = MathUtils.clamp(x + moveX, 0, mapWidth - hitW);
            com.badlogic.gdx.math.Rectangle testHitbox = new com.badlogic.gdx.math.Rectangle(newX, y, hitW, hitH);
            boolean collisionX = false;
            for (com.badlogic.gdx.math.Rectangle rect : MapManager.getInstance().getCollisionRectangles()) {
                if (testHitbox.overlaps(rect)) {
                    collisionX = true;
                    break;
                }
            }
            if (!collisionX) {
                x = newX;
            }

            // 2. Test Y-axis movement
            float newY = MathUtils.clamp(y + moveY, 0, mapHeight - hitH);
            testHitbox.set(x, newY, hitW, hitH);
            boolean collisionY = false;
            for (com.badlogic.gdx.math.Rectangle rect : MapManager.getInstance().getCollisionRectangles()) {
                if (testHitbox.overlaps(rect)) {
                    collisionY = true;
                    break;
                }
            }
            if (!collisionY) {
                y = newY;
            }
        }

        cat.setPosition(x, y);

        if (!isMoving) {
            cat.changeState(new IdleState());
        }
    }

    @Override
    public void render(Cat cat, SpriteBatch batch) {
        cat.renderAnimation(batch, "RUN", Gdx.graphics.getDeltaTime());
    }

    @Override
    public void exit(Cat cat) {
    }
}
