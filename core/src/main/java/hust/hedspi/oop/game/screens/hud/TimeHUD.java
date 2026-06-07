package hust.hedspi.oop.game.screens.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.managers.TimeManager;
import hust.hedspi.oop.game.utils.IObserver;

public class TimeHUD implements IObserver {
    private Table table;
    private Label timeLabel;
    private Label dayLabel;
    private Texture panelTex;

    public TimeHUD() {
        table = new Table();
        table.top().right(); 
        table.setFillParent(true);

        panelTex = new Texture(Gdx.files.internal("images/HUD/ui/panel/timeframe.png"));
        
        NinePatch panelPatch = new NinePatch(panelTex, 8, 8, 8, 8);
        NinePatchDrawable panelBg = new NinePatchDrawable(panelPatch);

        Table contentTable = new Table();
        contentTable.setBackground(panelBg);
        contentTable.pad(15); 

        Label.LabelStyle labelStyle = new Label.LabelStyle(ResourceManager.getInstance().hudFont, Color.WHITE);
        
        timeLabel = new Label("", labelStyle);
        dayLabel = new Label("", labelStyle);

        
        contentTable.add(timeLabel).center().padBottom(5).row();
        contentTable.add(dayLabel).center();

        
        table.add(contentTable).padTop(10).padRight(10);

        
        TimeManager.getInstance().addObserver(this);
        
        
        updateLabels();
    }

    private void updateLabels() {
        TimeManager tm = TimeManager.getInstance();
        timeLabel.setText(String.format("%02d:%02d", tm.getInGameHour(), tm.getInGameMinute()));
        
        dayLabel.setText(tm.getCurrentDayOfWeek().toString());
    }

    @Override
    public void onNotify(Object... args) {
        updateLabels();
    }

    public Table getTable() {
        return table;
    }

    public void dispose() {
        TimeManager.getInstance().removeObserver(this);
        if (panelTex != null) {
            panelTex.dispose();
        }
    }
}
