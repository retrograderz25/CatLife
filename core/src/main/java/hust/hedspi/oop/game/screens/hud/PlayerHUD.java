package hust.hedspi.oop.game.screens.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import hust.hedspi.oop.game.entities.Cat;
import hust.hedspi.oop.game.managers.GameManager;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.managers.StoryManager;
import hust.hedspi.oop.game.utils.IObserver;

public class PlayerHUD implements IObserver {
    private Table rootTable;
    private Table statsTable;
    private Table iconTable;
    private Label hpLabel;
    private Label energyLabel;
    private Image adoptedIcon;
    private Cat player;
    private Texture panelTex;
    private Texture iconTex;

    public PlayerHUD() {
        rootTable = new Table();
        rootTable.setFillParent(true);

        statsTable = new Table();
        statsTable.top().left();

        panelTex = new Texture(Gdx.files.internal("images/HUD/ui/panel/panel.png"));
        
        NinePatch panelPatch = new NinePatch(panelTex, 8, 8, 8, 8); 
        NinePatchDrawable panelBg = new NinePatchDrawable(panelPatch);

        Table contentTable = new Table();
        contentTable.setBackground(panelBg);
        contentTable.pad(15); 

        
        Label.LabelStyle labelStyle = new Label.LabelStyle(ResourceManager.getInstance().hudFont, Color.WHITE);
        
        hpLabel = new Label("", labelStyle);
        energyLabel = new Label("", labelStyle);

        contentTable.add(hpLabel).left().padBottom(5).row();
        contentTable.add(energyLabel).left();

        
        statsTable.add(contentTable).padTop(10).padLeft(10);
        
        iconTable = new Table();
        iconTable.bottom().left();

        try {
            iconTex = new Texture(Gdx.files.internal("images/adopted_icon.png"));
            adoptedIcon = new Image(new TextureRegionDrawable(iconTex));
            
            iconTable.add(adoptedIcon).size(64, 64).padBottom(10).padLeft(10);
            adoptedIcon.setVisible(false);
        } catch (Exception e) {
            
        }
        
        rootTable.add(statsTable).expand().top().left();
        rootTable.row();
        rootTable.add(iconTable).expand().bottom().left();

        
        this.player = GameManager.getInstance().getPlayer();
        if (this.player != null) {
            this.player.addObserver(this);
            updateLabels();
        }
    }

    private void updateLabels() {
        if (player == null) return;
        hpLabel.setText("HP: " + player.getHp() + "/100");
        
        if (adoptedIcon != null) {
            
            boolean isAdopted = StoryManager.getInstance().isZoneUnlocked("Bubble");
            adoptedIcon.setVisible(isAdopted);
        }
    }

    @Override
    public void onNotify(Object... args) {
        updateLabels();
    }

    public Table getTable() {
        return rootTable;
    }

    public void dispose() {
        if (player != null) {
            player.removeObserver(this);
        }
        if (panelTex != null) {
            panelTex.dispose();
        }
        if (iconTex != null) {
            iconTex.dispose();
        }
    }
}
