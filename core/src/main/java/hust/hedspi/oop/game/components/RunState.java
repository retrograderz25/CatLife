package hust.hedspi.oop.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.hedspi.oop.game.entities.Cat;
import hust.hedspi.oop.game.skills.DashSkill;
import hust.hedspi.oop.game.utils.Constants;

public class RunState implements ICatState {

    private float energyDrainTimer = 0f;
    private DashSkill dashSkill;

    @Override
    public void enter(Cat cat) {
        // System.out.println("Cat enters Run State.");
        energyDrainTimer = 0f;
        if (dashSkill == null) {
            dashSkill = new DashSkill();
        }
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
        float speed = cat.getSpeed();

        // Xử lý kỹ năng Dash
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)) {
            if (dashSkill.canUse(cat)) {
                dashSkill.use(cat);
                speed *= 3f; // Tăng tốc độ tức thời khi dash (tùy chỉnh logic Dash thêm nếu muốn)
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) { y += speed * dt; isMoving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { y -= speed * dt; isMoving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { x -= speed * dt; isMoving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { x += speed * dt; isMoving = true; }

        // Giàng buộc: Không cho phép nhân vật đi ra ngoài bản đồ (màn hình)
        x = Math.max(0, Math.min(x, Constants.VIRTUAL_WIDTH - cat.getWidth()));
        y = Math.max(0, Math.min(y, Constants.VIRTUAL_HEIGHT - cat.getHeight()));

        cat.setPosition(x, y);

        // Trừ thể lực theo thời gian khi di chuyển
        if (isMoving) {
            energyDrainTimer += dt;
            if (energyDrainTimer >= 1.0f) { // Cứ 1 giây chạy liên tục thì trừ 2 Energy
                cat.decreaseEnergy(2);
                energyDrainTimer -= 1.0f;
            }
        } else {
            // Nếu không bấm phím nào, tự động chuyển về trạng thái Đứng yên (IdleState)
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
