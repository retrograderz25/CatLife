package hust.hedspi.oop.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import hust.hedspi.oop.game.managers.GameManager;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.managers.ScreenManager;
import hust.hedspi.oop.game.utils.Constants;

public class MainMenuScreen implements Screen {
    private Stage stage;
    private Texture buttonUpTexture;
    private Texture buttonDownTexture;

    public MainMenuScreen() {
        stage = new Stage(new FitViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Tạo Texture giả cho nút bấm
        buttonUpTexture = createColorTexture(new Color(0.2f, 0.4f, 0.8f, 1f));
        buttonDownTexture = createColorTexture(new Color(0.1f, 0.3f, 0.6f, 1f));

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(buttonUpTexture);
        buttonStyle.down = new TextureRegionDrawable(buttonDownTexture);
        buttonStyle.font = ResourceManager.getInstance().nameFont;

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label.LabelStyle titleStyle = new Label.LabelStyle(ResourceManager.getInstance().hudFont, Color.YELLOW);
        Label titleLabel = new Label("C A T   L I F E", titleStyle);
        titleLabel.setFontScale(1.5f);

        TextButton btnPlayStray = new TextButton("Chơi Mèo Hoang", buttonStyle);
        TextButton btnPlayHouse = new TextButton("Chơi Mèo Nhà", buttonStyle);
        TextButton btnExit = new TextButton("Thoát", buttonStyle);

        // Sự kiện các nút
        btnPlayStray.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameManager.getInstance().startNewGame(true);
                ScreenManager.getInstance().clearAndSetScreen(new PlayScreen());
            }
        });

        btnPlayHouse.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameManager.getInstance().startNewGame(false);
                ScreenManager.getInstance().clearAndSetScreen(new PlayScreen());
            }
        });

        btnExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Add to table
        table.add(titleLabel).padBottom(50).row();
        table.add(btnPlayStray).width(300).height(50).padBottom(20).row();
        table.add(btnPlayHouse).width(300).height(50).padBottom(20).row();
        table.add(btnExit).width(300).height(50).row();

        stage.addActor(table);
    }

    private Texture createColorTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        buttonUpTexture.dispose();
        buttonDownTexture.dispose();
    }
}
