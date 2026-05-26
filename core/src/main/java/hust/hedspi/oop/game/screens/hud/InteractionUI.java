package hust.hedspi.oop.game.screens.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import hust.hedspi.oop.game.entities.Cat;
import hust.hedspi.oop.game.entities.TriggerZone;
import hust.hedspi.oop.game.managers.ResourceManager;

public class InteractionUI {
    private Table rootTable;
    private Label dialogLabel;
    private TextButton btnYes;
    private TextButton btnNo;
    
    private Texture panelTex;
    private Texture btnTex;
    private Texture btnPressedTex;
    
    private TriggerZone currentZone;
    private Cat player;
    
    private int selectedButtonIndex = 0; // 0 for Yes, 1 for No

    public InteractionUI(Cat player) {
        this.player = player;
        
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.bottom().padBottom(50); // Đặt ở dưới cùng màn hình
        rootTable.setVisible(false);

        panelTex = new Texture(Gdx.files.internal("images/HUD/ui/panel/panel.png"));
        NinePatch panelPatch = new NinePatch(panelTex, 8, 8, 8, 8);
        NinePatchDrawable panelBg = new NinePatchDrawable(panelPatch);

        btnTex = new Texture(Gdx.files.internal("images/HUD/ui/button/button_blue.png"));
        btnPressedTex = new Texture(Gdx.files.internal("images/HUD/ui/button/button_blue_pressed.png"));
        
        NinePatch btnPatch = new NinePatch(btnTex, 4, 4, 4, 4);
        NinePatch btnPressedPatch = new NinePatch(btnPressedTex, 4, 4, 4, 4);
        
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = new NinePatchDrawable(btnPatch);
        btnStyle.down = new NinePatchDrawable(btnPressedPatch);
        btnStyle.font = ResourceManager.getInstance().dialogFont;
        btnStyle.fontColor = Color.WHITE;

        Table dialogTable = new Table();
        dialogTable.setBackground(panelBg);
        dialogTable.pad(20);

        Label.LabelStyle labelStyle = new Label.LabelStyle(ResourceManager.getInstance().dialogFont, Color.WHITE);
        dialogLabel = new Label("", labelStyle);
        dialogLabel.setWrap(true);
        
        dialogTable.add(dialogLabel).width(600).colspan(2).padBottom(20).center().row();
        
        btnYes = new TextButton(ResourceManager.getInstance().getBundle().get("btn_yes"), btnStyle);
        btnNo = new TextButton(ResourceManager.getInstance().getBundle().get("btn_no"), btnStyle);
        
        dialogTable.add(btnYes).width(150).height(50).padRight(50);
        dialogTable.add(btnNo).width(150).height(50);
        
        rootTable.add(dialogTable);

        // Bắt sự kiện Click chuột
        btnYes.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                acceptTask();
            }
        });
        
        btnNo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        // Bắt sự kiện bàn phím
        rootTable.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (!rootTable.isVisible()) return false;
                
                if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
                    selectedButtonIndex = 0;
                    updateButtonFocus();
                    return true;
                } else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
                    selectedButtonIndex = 1;
                    updateButtonFocus();
                    return true;
                } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE || keycode == Input.Keys.E) {
                    if (selectedButtonIndex == 0) {
                        acceptTask();
                    } else {
                        hide();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void updateButtonFocus() {
        if (selectedButtonIndex == 0) {
            btnYes.getLabel().setColor(Color.YELLOW);
            btnNo.getLabel().setColor(Color.WHITE);
        } else {
            btnYes.getLabel().setColor(Color.WHITE);
            btnNo.getLabel().setColor(Color.YELLOW);
        }
    }

    public void show(TriggerZone zone) {
        if (currentZone == zone && rootTable.isVisible()) return; 
        this.currentZone = zone;
        
        String zoneKey = "task_" + zone.getZoneName().replace(" ", "_");
        String text = ResourceManager.getInstance().getBundle().get("task_default");
        try {
            text = ResourceManager.getInstance().getBundle().get(zoneKey);
        } catch (Exception e) {
            // Fallback to default
        }
        
        dialogLabel.setText(text);
        rootTable.setVisible(true);
        selectedButtonIndex = 0;
        updateButtonFocus();
        
        // Cấp quyền focus cho bàn phím
        if (rootTable.getStage() != null) {
            rootTable.getStage().setKeyboardFocus(rootTable);
        }
    }

    public void hide() {
        rootTable.setVisible(false);
        currentZone = null;
        if (rootTable.getStage() != null) {
            rootTable.getStage().unfocus(rootTable);
        }
    }
    
    public boolean isVisible() {
        return rootTable.isVisible();
    }

    private void acceptTask() {
        if (currentZone != null) {
            currentZone.onInteract(player);
        }
        hide();
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
