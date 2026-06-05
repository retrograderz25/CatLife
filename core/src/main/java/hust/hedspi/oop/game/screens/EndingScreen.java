package hust.hedspi.oop.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import hust.hedspi.oop.game.managers.GameManager;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.managers.SaveManager;
import hust.hedspi.oop.game.managers.ScreenManager;
import hust.hedspi.oop.game.managers.SoundManager;
import hust.hedspi.oop.game.utils.Constants;
import hust.hedspi.oop.game.utils.EndingCondition;

public class EndingScreen implements Screen {
    private Stage stage;
    private Viewport viewport;
    private SpriteBatch batch;
    
    private String endingName;
    private String endingDescription;
    private Texture endingBgTexture;

    public EndingScreen(EndingCondition ending) {
        this(ending, true);
    }

    public EndingScreen(EndingCondition ending, boolean shouldSave) {
        batch = new SpriteBatch();
        viewport = new FitViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
        stage = new Stage(viewport, batch);

        if (ending != null) {
            endingName = ending.getEndingName();
            endingDescription = "Bạn đã đạt được kết cục: " + endingName + "\nXin chúc mừng!";
            if (shouldSave) {
                SaveManager.unlockEnding(endingName);
            }
        } else {
            endingName = "Sống sót ngoài đường";
            endingDescription = "Bạn tiếp tục cuộc sống lang bạt kỳ hồ...";
            // Fallback ending is not counted in TOTAL_ENDINGS (7)
        }

        loadBackground();
        buildUI();
    }
    
    public EndingScreen(String forcedEndingKey) {
        this(forcedEndingKey, true);
    }

    public EndingScreen(String forcedEndingKey, boolean shouldSave) {
        batch = new SpriteBatch();
        viewport = new FitViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
        stage = new Stage(viewport, batch);

        endingName = forcedEndingKey;
        endingDescription = "Kết cục: " + forcedEndingKey;
        if (shouldSave) {
            SaveManager.unlockEnding(endingName);
        }

        loadBackground();
        buildUI();
    }

    private void loadBackground() {
        String bgPath = getEndingBgPath(endingName);
        if (bgPath != null && Gdx.files.internal(bgPath).exists()) {
            endingBgTexture = new Texture(Gdx.files.internal(bgPath));
        }
    }

    private String getEndingBgPath(String name) {
        switch (name) {
            case "Thánh Đổ Vỏ": return "ending/thanh_do_vo.png";
            case "Gia Đình Hạnh Phúc": return "ending/gia_dinh_hanh_phuc.png";
            case "Mãi Mãi Kiếp Culi": return "ending/mai_mai_kiep_cu_li.png";
            case "Làm Đại Ca Mèo": return "ending/lam_dai_ca_meo.png";
            case "Hoàng Thượng Có Hoàng Hậu": return "ending/hoang_thuong_family.png";
            case "Hoàng Thượng Thái Giám": return "ending/hoang_thuong_alone.png";
            case "Quán Thịt Hổ": return "ending/quan_thit_ho.png";
            default: return null;
        }
    }

    private void buildUI() {
        Table rootTable = new Table();
        rootTable.setFillParent(true);

        Texture panelTex = new Texture(Gdx.files.internal("images/HUD/ui/panel/panel.png"));
        NinePatch panelPatch = new NinePatch(panelTex, 8, 8, 8, 8);
        NinePatchDrawable panelBg = new NinePatchDrawable(panelPatch);

        Texture btnTex = new Texture(Gdx.files.internal("images/HUD/ui/button/button_blue.png"));
        Texture btnPressedTex = new Texture(Gdx.files.internal("images/HUD/ui/button/button_blue_pressed.png"));
        
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = new NinePatchDrawable(new NinePatch(btnTex, 4, 4, 4, 4));
        btnStyle.down = new NinePatchDrawable(new NinePatch(btnPressedTex, 4, 4, 4, 4));
        btnStyle.font = ResourceManager.getInstance().dialogFont;
        btnStyle.fontColor = Color.WHITE;

        Table dialogTable = new Table();
        dialogTable.setBackground(panelBg);
        dialogTable.pad(40);

        BitmapFont font = ResourceManager.getInstance().hudFont;
        BitmapFont dialogFont = ResourceManager.getInstance().dialogFont;

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.YELLOW);
        Label titleLabel = new Label("KẾT CỤC: " + endingName.toUpperCase(), titleStyle);
        titleLabel.setAlignment(Align.center);
        
        Label.LabelStyle descStyle = new Label.LabelStyle(dialogFont, Color.BLACK);
        Label descLabel = new Label(endingDescription, descStyle);
        descLabel.setAlignment(Align.center);
        descLabel.setWrap(true);

        Label unlockLabel = new Label("Đã mở khóa: " + SaveManager.getUnlockedEndingsCount() + " / " + SaveManager.TOTAL_ENDINGS + " Endings", descStyle);
        unlockLabel.setAlignment(Align.center);
        unlockLabel.setColor(Color.GRAY);

        dialogTable.add(titleLabel).padBottom(20).row();
        
        if (endingBgTexture != null) {
            Image endingImg = new Image(endingBgTexture);
            dialogTable.add(endingImg).width(480).height(262).padBottom(20).row();
        }
        
        dialogTable.add(descLabel).width(600).padBottom(30).row();
        dialogTable.add(unlockLabel).padBottom(30).row();

        TextButton btnNewLife = new TextButton("Bắt Đầu Cuộc Sống Mới", btnStyle);
        btnNewLife.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameManager.getInstance().startNewGame(true); 
                ScreenManager.getInstance().clearAndSetScreen(new PlayScreen());
            }
        });

        TextButton btnContinue = new TextButton("Tiếp Tục Khám Phá", btnStyle);
        btnContinue.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().popScreen(); 
                if (GameManager.getInstance().getPlayer().getHp() <= 0) {
                    GameManager.getInstance().getPlayer().setHp(100); // Hồi sinh
                }
                GameManager.getInstance().resumeGame();
            }
        });

        Table btnTable = new Table();
        if ("Quán Thịt Hổ".equals(endingName)) {
            // Ending "Quán Thịt Hổ" là Instant Death vĩnh viễn, vô hiệu hóa nút Tiếp Tục
            btnTable.add(btnNewLife).width(300).height(60);
        } else {
            btnTable.add(btnNewLife).width(300).height(60).padRight(30);
            btnTable.add(btnContinue).width(300).height(60);
        }

        dialogTable.add(btnTable);
        rootTable.add(dialogTable);

        stage.addActor(rootTable);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        SoundManager.getInstance().playBGM(SoundManager.BGM_ENDING);
        if ("Quán Thịt Hổ".equals(endingName)) {
            SoundManager.getInstance().playSFX(SoundManager.SFX_DANGER_ALERT);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        SoundManager.getInstance().stopBGM();
        batch.dispose();
        stage.dispose();
        if (endingBgTexture != null) {
            endingBgTexture.dispose();
        }
    }
}
