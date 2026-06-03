package hust.hedspi.oop.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import hust.hedspi.oop.game.minigames.IMinigameStrategy;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.managers.ScreenManager;
import hust.hedspi.oop.game.managers.GameManager;
import hust.hedspi.oop.game.utils.Constants;

public class MinigameScreen implements Screen {
    private IMinigameStrategy strategy;
    private SpriteBatch batch;
    private boolean shouldExit = false;

    public static Viewport viewport;
    private OrthographicCamera camera;

    // --- PAUSE MENU ---
    private boolean isPaused = false;
    private Texture dimTexture;
    private BitmapFont font;
    private int selectedOption = 0; // 0: Resume, 1: Exit

    public MinigameScreen(IMinigameStrategy strategy) {
        this.strategy = strategy;
        this.batch = new SpriteBatch();

        this.camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT, camera);
        viewport.apply();

        // Đổi trạng thái game tổng để tạm dừng Map chính
        GameManager.getInstance().pauseGame();

        // Khởi tạo tài nguyên cho Pause Menu
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        dimTexture = new Texture(pix);
        pix.dispose();

        font = ResourceManager.getInstance().dialogFont;
    }

    public static Vector2 unproject(int screenX, int screenY) {
        if (viewport == null) return new Vector2(screenX, screenY);
        Vector3 unprojected = viewport.unproject(new Vector3(screenX, screenY, 0));
        return new Vector2(unprojected.x, unprojected.y);
    }

    @Override
    public void show() {
        strategy.start();
    }

    @Override
    public void render(float delta) {
        if (shouldExit || strategy.isFinished()) {
            ScreenManager.getInstance().popScreen();
            GameManager.getInstance().resumeGame();
            return;
        }

        // Bắt phím ESC để bật/tắt Pause Menu (chỉ khi game chưa kết thúc)
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE) && !strategy.isFinished()) {
            isPaused = !isPaused;
            selectedOption = 0; // Mặc định trỏ vào "Tiếp tục"
        }

        if (isPaused) {
            // Điều khiển Menu Pause
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.UP) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.W)) {
                selectedOption = 0;
            } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S)) {
                selectedOption = 1;
            } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
                if (selectedOption == 0) {
                    isPaused = false;
                } else {
                    shouldExit = true;
                }
            }
        } else {
            // Cập nhật logic game nếu không Pause
            strategy.update(delta);
        }

        ScreenUtils.clear(0f, 0f, 0f, 1);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        
        // Luôn vẽ minigame bên dưới
        strategy.render(batch);
        
        // Vẽ lớp Pause Menu đè lên trên
        if (isPaused) {
            batch.setColor(0f, 0f, 0f, 0.7f);
            batch.draw(dimTexture, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
            batch.setColor(Color.WHITE);
            
            font.getData().setScale(2.0f);
            font.setColor(Color.YELLOW);
            font.draw(batch, "TẠM DỪNG", 0, Constants.VIRTUAL_HEIGHT / 2f + 120, Constants.VIRTUAL_WIDTH, Align.center, false);
            
            font.getData().setScale(1.2f);
            font.setColor(selectedOption == 0 ? Color.YELLOW : Color.WHITE);
            font.draw(batch, selectedOption == 0 ? "> Tiếp Tục <" : "Tiếp Tục", 0, Constants.VIRTUAL_HEIGHT / 2f + 20, Constants.VIRTUAL_WIDTH, Align.center, false);
            
            font.setColor(selectedOption == 1 ? Color.YELLOW : Color.WHITE);
            font.draw(batch, selectedOption == 1 ? "> Thoát về Đường Phố <" : "Thoát về Đường Phố", 0, Constants.VIRTUAL_HEIGHT / 2f - 40, Constants.VIRTUAL_WIDTH, Align.center, false);
            
            font.getData().setScale(1.0f); // Reset scale
            font.setColor(Color.WHITE);
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        if (dimTexture != null) {
            dimTexture.dispose();
        }
        strategy.dispose();
        viewport = null;
    }
}
