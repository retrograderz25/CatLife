package hust.hedspi.oop.game.minigames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import hust.hedspi.oop.game.managers.ResourceManager;

/**
 * Minigame hiển thị màn hình "Coming Soon" cho các sự kiện/trigger chưa hoàn thiện.
 * Tuân thủ tuyệt đối Strategy Pattern.
 */
public class ComingSoonMinigame implements IMinigameStrategy {
    private boolean finished = false;
    private BitmapFont hudFont;
    private BitmapFont dialogFont;
    
    @Override
    public void start() {
        hudFont = ResourceManager.getInstance().hudFont;
        dialogFont = ResourceManager.getInstance().dialogFont;
        finished = false;
    }

    @Override
    public void update(float dt) {
        // Nhấn phím bất kỳ (phổ biến) để thoát
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || 
            Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || 
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            finished = true;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        int screenW = hust.hedspi.oop.game.utils.Constants.VIRTUAL_WIDTH;
        int screenH = hust.hedspi.oop.game.utils.Constants.VIRTUAL_HEIGHT;
        
        hudFont.setColor(Color.YELLOW);
        hudFont.draw(batch, "COMING SOON!", 0, screenH / 2f + 50, screenW, Align.center, false);
        
        dialogFont.setColor(Color.WHITE);
        dialogFont.draw(batch, "Minigame này đang được các Boss phát triển.\nNhấn ESC hoặc ENTER để quay lại đi dạo nhé!", 
                        0, screenH / 2f - 20, screenW, Align.center, false);
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public boolean isWon() {
        return false; // Không tính là thắng hay thua
    }

    @Override
    public void dispose() {
        // Không dispose font của ResourceManager
    }

    @Override
    public void forceEnd(boolean win) {
        this.finished = true;
    }
}
