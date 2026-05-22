package hust.hedspi.oop.game.screens.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.managers.TimeManager;
import hust.hedspi.oop.game.utils.IObserver;

public class TimeHUD implements IObserver {
    private Table table;
    private Label timeLabel;
    private Label dayLabel;

    public TimeHUD() {
        table = new Table();
        table.top().right();
        table.setFillParent(true);

        Label.LabelStyle labelStyle = new Label.LabelStyle(ResourceManager.getInstance().hudFont, Color.WHITE);
        
        timeLabel = new Label("", labelStyle);
        dayLabel = new Label("", labelStyle);

        table.add(timeLabel).padTop(20).padRight(20).row();
        table.add(dayLabel).padRight(20);

        // Register as observer
        TimeManager.getInstance().addObserver(this);
        
        // Initial update
        updateLabels();
    }

    private void updateLabels() {
        TimeManager tm = TimeManager.getInstance();
        timeLabel.setText(String.format("Thời gian: %02d:%02d", tm.getInGameHour(), tm.getInGameMinute()));
        dayLabel.setText(tm.getCurrentPhase() + " - " + tm.getCurrentDayOfWeek());
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
    }
}
