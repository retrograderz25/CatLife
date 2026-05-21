package hust.hedspi.oop.game.screens.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import hust.hedspi.oop.game.entities.Cat;
import hust.hedspi.oop.game.managers.GameManager;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.utils.IObserver;

public class PlayerHUD implements IObserver {
    private Table table;
    private Label hpLabel;
    private Label hungerLabel;
    private Label energyLabel;
    private Cat player;

    public PlayerHUD() {
        table = new Table();
        table.top().left();
        table.setFillParent(true);

        Label.LabelStyle labelStyle = new Label.LabelStyle(ResourceManager.getInstance().nameFont, Color.WHITE);
        
        hpLabel = new Label("", labelStyle);
        hungerLabel = new Label("", labelStyle);
        energyLabel = new Label("", labelStyle);

        table.add(hpLabel).padTop(20).padLeft(20).left().row();
        table.add(hungerLabel).padTop(10).padLeft(20).left().row();
        table.add(energyLabel).padTop(10).padLeft(20).left();

        // Register as observer
        this.player = GameManager.getInstance().getPlayer();
        if (this.player != null) {
            this.player.addObserver(this);
            updateLabels();
        }
    }

    private void updateLabels() {
        if (player == null) return;
        hpLabel.setText("Máu: " + player.getHp() + "/100");
        hungerLabel.setText("Đói: " + player.getHunger() + "/100");
        energyLabel.setText("Năng lượng: " + player.getEnergy() + "/100");
    }

    @Override
    public void onNotify(Object... args) {
        updateLabels();
    }

    public Table getTable() {
        return table;
    }

    public void dispose() {
        if (player != null) {
            player.removeObserver(this);
        }
    }
}
