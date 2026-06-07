package hust.hedspi.oop.game.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import hust.hedspi.oop.game.entities.Cat;
import hust.hedspi.oop.game.managers.GameManager;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.managers.TimeManager;

public class DebugMenu {
    private Table rootTable;
    private Texture panelTex, btnTex, btnPressedTex;
    private Label hourLabel, minLabel, hpLabel, energyLabel;
    private boolean isVisible = false;
    
    private TextButton winBtn, loseBtn, unadoptBtn, resetSavesBtn;

    public DebugMenu() {
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();
        rootTable.setVisible(false);

        panelTex = new Texture(Gdx.files.internal("images/HUD/ui/panel/panel.png"));
        NinePatchDrawable panelBg = new NinePatchDrawable(new NinePatch(panelTex, 8, 8, 8, 8));

        btnTex = new Texture(Gdx.files.internal("images/HUD/ui/button/button_blue.png"));
        btnPressedTex = new Texture(Gdx.files.internal("images/HUD/ui/button/button_blue_pressed.png"));
        NinePatchDrawable btnUp = new NinePatchDrawable(new NinePatch(btnTex, 4, 4, 4, 4));
        NinePatchDrawable btnDown = new NinePatchDrawable(new NinePatch(btnPressedTex, 4, 4, 4, 4));

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = btnUp;
        btnStyle.down = btnDown;
        btnStyle.font = ResourceManager.getInstance().dialogFont;
        btnStyle.fontColor = Color.BLACK;

        Table contentTable = new Table();
        contentTable.setBackground(panelBg);
        contentTable.pad(30);

        Label.LabelStyle labelStyle = new Label.LabelStyle(ResourceManager.getInstance().dialogFont, Color.BLACK);
        Label titleLabel = new Label("--- DEBUG MENU ---", new Label.LabelStyle(ResourceManager.getInstance().hudFont, Color.YELLOW));
        contentTable.add(titleLabel).colspan(3).padBottom(20).center().row();

        // 1. Time: Hour
        hourLabel = new Label("Giờ: ", labelStyle);
        TextButton subHourBtn = new TextButton(" - ", btnStyle);
        TextButton addHourBtn = new TextButton(" + ", btnStyle);
        subHourBtn.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) { TimeManager.getInstance().addTime(-60); updateLabels(); }
        });
        addHourBtn.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) { TimeManager.getInstance().addTime(60); updateLabels(); }
        });
        contentTable.add(subHourBtn).width(40).height(40).pad(5);
        contentTable.add(hourLabel).width(150).align(Align.center);
        contentTable.add(addHourBtn).width(40).height(40).pad(5).row();

        // 2. Time: Minute
        minLabel = new Label("Phút: ", labelStyle);
        TextButton subMinBtn = new TextButton(" - ", btnStyle);
        TextButton addMinBtn = new TextButton(" + ", btnStyle);
        subMinBtn.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) { TimeManager.getInstance().addTime(-10); updateLabels(); }
        });
        addMinBtn.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) { TimeManager.getInstance().addTime(10); updateLabels(); }
        });
        contentTable.add(subMinBtn).width(40).height(40).pad(5);
        contentTable.add(minLabel).width(150).align(Align.center);
        contentTable.add(addMinBtn).width(40).height(40).pad(5).row();

        // 3. Player: HP
        hpLabel = new Label("HP: ", labelStyle);
        TextButton subHpBtn = new TextButton(" - ", btnStyle);
        TextButton addHpBtn = new TextButton(" + ", btnStyle);
        subHpBtn.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                Cat p = GameManager.getInstance().getPlayer();
                if(p != null) { p.decreaseHp(10); updateLabels(); }
            }
        });
        addHpBtn.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                Cat p = GameManager.getInstance().getPlayer();
                if(p != null) { p.increaseHp(10); updateLabels(); }
            }
        });
        contentTable.add(subHpBtn).width(40).height(40).pad(5);
        contentTable.add(hpLabel).width(150).align(Align.center);
        contentTable.add(addHpBtn).width(40).height(40).pad(5).row();

        // 6. Adopt / Unadopt
        unadoptBtn = new TextButton("Bỏ Nhận Nuôi", btnStyle);
        unadoptBtn.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                hust.hedspi.oop.game.managers.StoryManager.getInstance().recordResult(hust.hedspi.oop.game.utils.MinigameID.PET_BEG, false);
                Cat p = GameManager.getInstance().getPlayer();
                if(p != null) { p.notifyObservers(); }
            }
        });
        
        resetSavesBtn = new TextButton("Reset Thành Tích", btnStyle);
        resetSavesBtn.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                hust.hedspi.oop.game.managers.SaveManager.clearAllSaves();
                hust.hedspi.oop.game.managers.StoryManager.getInstance().resetStoryFlags();
                System.out.println("Đã reset toàn bộ thành tích và lịch sử chơi!");
            }
        });

        Table actionTable = new Table();
        actionTable.add(unadoptBtn).width(150).height(40).pad(5);
        actionTable.add(resetSavesBtn).width(150).height(40).pad(5);
        contentTable.add(actionTable).colspan(3).center().row();

        // 7. Force Minigame Result
        winBtn = new TextButton("Thắng Game", btnStyle);
        loseBtn = new TextButton("Thua Game", btnStyle);
        winBtn.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                if (hust.hedspi.oop.game.managers.ScreenManager.getInstance().getCurrentScreen() instanceof hust.hedspi.oop.game.screens.MinigameScreen) {
                    ((hust.hedspi.oop.game.screens.MinigameScreen)hust.hedspi.oop.game.managers.ScreenManager.getInstance().getCurrentScreen()).forceEnd(true);
                    toggle(); // Hide menu
                }
            }
        });
        loseBtn.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                if (hust.hedspi.oop.game.managers.ScreenManager.getInstance().getCurrentScreen() instanceof hust.hedspi.oop.game.screens.MinigameScreen) {
                    ((hust.hedspi.oop.game.screens.MinigameScreen)hust.hedspi.oop.game.managers.ScreenManager.getInstance().getCurrentScreen()).forceEnd(false);
                    toggle(); // Hide menu
                }
            }
        });
        contentTable.add(winBtn).width(150).height(40).pad(5).colspan(1).right();
        contentTable.add(loseBtn).width(150).height(40).pad(5).colspan(2).left().row();

        rootTable.add(contentTable);
    }

    public void updateLabels() {
        TimeManager tm = TimeManager.getInstance();
        hourLabel.setText("Giờ: " + tm.getInGameHour());
        minLabel.setText("Phút: " + tm.getInGameMinute());

        Cat p = GameManager.getInstance().getPlayer();
        if (p != null) {
            hpLabel.setText("HP: " + p.getHp());
        }
        
        // Hide minigame buttons if not in minigame
        boolean isMinigame = hust.hedspi.oop.game.managers.ScreenManager.getInstance().getCurrentScreen() instanceof hust.hedspi.oop.game.screens.MinigameScreen;
        winBtn.setVisible(isMinigame);
        loseBtn.setVisible(isMinigame);
    }

    public void toggle() {
        isVisible = !isVisible;
        rootTable.setVisible(isVisible);
        if (isVisible) {
            updateLabels();
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public Table getTable() {
        return rootTable;
    }

    public void dispose() {
        if (panelTex != null) panelTex.dispose();
        if (btnTex != null) btnTex.dispose();
        if (btnPressedTex != null) btnPressedTex.dispose();
    }
}
