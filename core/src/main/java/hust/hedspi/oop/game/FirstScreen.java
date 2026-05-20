package hust.hedspi.oop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import hust.hedspi.oop.game.entities.StrayCat;
import hust.hedspi.oop.game.skills.DashSkill;
import hust.hedspi.oop.game.skills.HissSkill;
import hust.hedspi.oop.game.skills.ScratchSkill;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    
    private StrayCat myCat;

    @Override
    public void show() {
        // Khởi tạo mèo hoang và thêm các kỹ năng
        myCat = new StrayCat("Mèo Mun");
        myCat.addSkill(new ScratchSkill()); // Skill 0 (Phím 1)
        myCat.addSkill(new HissSkill());    // Skill 1 (Phím 2)
        myCat.addSkill(new DashSkill());    // Skill 2 (Phím 3)
        
        System.out.println("==================================================");
        System.out.println("Trò chơi bắt đầu! Bạn đang điều khiển: " + myCat.getName());
        System.out.println("Nhấn phím số 1: Cào (Scratch)  - Sát thương vật lý");
        System.out.println("Nhấn phím số 2: Khè (Hiss)     - Gây hoảng sợ");
        System.out.println("Nhấn phím số 3: Lẩn trốn (Dash)- Tăng tốc độ");
        System.out.println("==================================================");
    }

    @Override
    public void render(float delta) {
        // Xóa màn hình
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Cập nhật logic của mèo (hồi chiêu, hồi thể lực)
        myCat.update(delta);

        // Xử lý Input từ bàn phím để test skill
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            myCat.useSkill(0);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            myCat.useSkill(1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            myCat.useSkill(2);
        }
    }

    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0) return;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
