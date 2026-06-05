package hust.hedspi.oop.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import hust.hedspi.oop.game.entities.Cat;
import hust.hedspi.oop.game.managers.MapManager;

public class RunState implements ICatState {

    private boolean isRunning = false;

    @Override
    public void enter(Cat cat) {
        isRunning = false;
    }

    @Override
    public void update(Cat cat, float dt) {
        // Cập nhật cooldown của skill
        if (dashSkill != null) {
            dashSkill.update(dt);
        }

        // Giàng buộc: Cần có đủ thể lực (Energy > 0) mới được phép chạy
        if (cat.getEnergy() <= 0) {
            cat.changeState(new IdleState());
            return;
        }

        boolean isMoving = false;
        float x = cat.getX();
        float y = cat.getY();
        
        // Kiểm tra xem người chơi có đang giữ phím Shift không
        isRunning = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        
        // Nếu chạy thì dùng tốc độ gốc, nếu đi bộ thì giảm còn 60%
        float currentSpeed = isRunning ? cat.getSpeed() : cat.getSpeed() * 0.6f;

        float dx = 0;
        float dy = 0;

        if ((Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))) { dy += 1; }
        if ((Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))) { dy -= 1; }
        if ((Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))) {
            dx -= 1;
            cat.setFacingRight(false);
        }
        if ((Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))) {
            dx += 1;
            cat.setFacingRight(true);
        }

        if (dx != 0 || dy != 0) {
            isMoving = true;
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            dx /= length;
            dy /= length;

            float moveX = dx * currentSpeed * dt;
            float moveY = dy * currentSpeed * dt;

            float mapWidth = MapManager.getInstance().getMapPixelWidth();
            float mapHeight = MapManager.getInstance().getMapPixelHeight();

            float hitW = cat.getHitbox().width;
            float hitH = cat.getHitbox().height;

            float newX = MathUtils.clamp(x + moveX, 0, mapWidth - hitW);
            com.badlogic.gdx.math.Rectangle testHitbox = new com.badlogic.gdx.math.Rectangle(newX, y, hitW, hitH);
            boolean collisionX = false;
            for (com.badlogic.gdx.math.Rectangle rect : MapManager.getInstance().getCollisionRectangles()) {
                if (testHitbox.overlaps(rect)) { collisionX = true; break; }
            }
            if (!collisionX) x = newX;

            float newY = MathUtils.clamp(y + moveY, 0, mapHeight - hitH);
            testHitbox.set(x, newY, hitW, hitH);
            boolean collisionY = false;
            for (com.badlogic.gdx.math.Rectangle rect : MapManager.getInstance().getCollisionRectangles()) {
                if (testHitbox.overlaps(rect)) { collisionY = true; break; }
            }
            if (!collisionY) y = newY;
        }

        // Giàng buộc: Không cho phép nhân vật đi ra ngoài bản đồ (màn hình)
        x = Math.max(0, Math.min(x, Constants.VIRTUAL_WIDTH - cat.getWidth()));
        y = Math.max(0, Math.min(y, Constants.VIRTUAL_HEIGHT - cat.getHeight()));

        cat.setPosition(x, y);

        if (!isMoving) {
            cat.changeState(new IdleState());
        }
    }

    @Override
    public void render(Cat cat, SpriteBatch batch) {
        String animToPlay = isRunning ? "RUN" : "WALK";
        cat.renderAnimation(batch, animToPlay, Gdx.graphics.getDeltaTime());
    }

    @Override
    public void exit(Cat cat) {
    }
}
