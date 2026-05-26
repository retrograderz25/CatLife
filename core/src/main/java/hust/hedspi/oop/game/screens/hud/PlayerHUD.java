package hust.hedspi.oop.game.screens.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
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
    private Texture panelTex;

    public PlayerHUD() {
        table = new Table();
        table.top().left();
        table.setFillParent(true);

        panelTex = new Texture(Gdx.files.internal("images/HUD/ui/panel/panel.png"));
        // Sử dụng NinePatch để kéo dãn khung giao diện không bị vỡ viền
        NinePatch panelPatch = new NinePatch(panelTex, 8, 8, 8, 8); 
        NinePatchDrawable panelBg = new NinePatchDrawable(panelPatch);

        Table contentTable = new Table();
        contentTable.setBackground(panelBg);
        contentTable.pad(15); // Lề bên trong khung

        // Dùng font chuyên dụng cho HUD
        Label.LabelStyle labelStyle = new Label.LabelStyle(ResourceManager.getInstance().hudFont, Color.WHITE);
        
        hpLabel = new Label("", labelStyle);
        hungerLabel = new Label("", labelStyle);
        energyLabel = new Label("", labelStyle);

        contentTable.add(hpLabel).left().padBottom(5).row();
        contentTable.add(hungerLabel).left().padBottom(5).row();
        contentTable.add(energyLabel).left();

        // Thêm contentTable vào table gốc với lề 10px từ góc trên trái
        table.add(contentTable).padTop(10).padLeft(10);

        // Register as observer
        this.player = GameManager.getInstance().getPlayer();
        if (this.player != null) {
            this.player.addObserver(this);
            updateLabels();
        }
    }

    private void updateLabels() {
        if (player == null) return;
        hpLabel.setText("HP: " + player.getHp() + "/100");
        hungerLabel.setText("Hunger: " + player.getHunger() + "/100");
        energyLabel.setText("Energy: " + player.getEnergy() + "/100");
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
        if (panelTex != null) {
            panelTex.dispose();
        }
    }
}
