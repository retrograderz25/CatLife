package hust.hedspi.oop.game.minigames.pet_beg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.managers.StoryManager;
import hust.hedspi.oop.game.minigames.IMinigameStrategy;
import hust.hedspi.oop.game.utils.Constants;
import hust.hedspi.oop.game.utils.MinigameID;

public class PetBegMinigame implements IMinigameStrategy {
    private boolean finished = false;
    private boolean won = false;
    private boolean exitRequested = false;

    private Texture dimTexture;
    private Texture panelTex;
    private BitmapFont font;
    private BitmapFont dialogFont;

    private int selectedOption = 0; // 0: Đồng ý, 1: Từ chối

    @Override
    public void start() {
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        dimTexture = new Texture(pix);
        pix.dispose();

        panelTex = new Texture(Gdx.files.internal("images/HUD/ui/panel/panel.png"));
        
        font = ResourceManager.getInstance().hudFont;
        dialogFont = ResourceManager.getInstance().dialogFont;
    }

    @Override
    public void update(float dt) {
        if (finished) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            selectedOption = 0;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            selectedOption = 1;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (selectedOption == 0) {
                won = true;
            } else {
                won = false;
            }
            finished = true;
            exitRequested = true;
            StoryManager.getInstance().recordResult(MinigameID.PET_BEG, won);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            won = false;
            finished = true;
            exitRequested = true;
            StoryManager.getInstance().recordResult(MinigameID.PET_BEG, false);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        float screenW = Constants.VIRTUAL_WIDTH;
        float screenH = Constants.VIRTUAL_HEIGHT;

        batch.setColor(0f, 0f, 0f, 0.8f);
        batch.draw(dimTexture, 0, 0, screenW, screenH);
        batch.setColor(Color.WHITE);

        float panelW = 800f;
        float panelH = 400f;
        float panelX = (screenW - panelW) / 2f;
        float panelY = (screenH - panelH) / 2f;

        batch.draw(panelTex, panelX, panelY, panelW, panelH);

        String question = ResourceManager.getInstance().getBundle().get("pet_beg_question");
        String yesText = ResourceManager.getInstance().getBundle().get("pet_beg_yes");
        String noText = ResourceManager.getInstance().getBundle().get("pet_beg_no");

        dialogFont.setColor(Color.WHITE);
        dialogFont.draw(batch, question, panelX + 50, panelY + panelH - 80, panelW - 100, Align.center, true);

        float btnY = panelY + 120;
        
        font.getData().setScale(1.2f);
        
        font.setColor(selectedOption == 0 ? Color.YELLOW : Color.WHITE);
        font.draw(batch, selectedOption == 0 ? "> " + yesText + " <" : yesText, panelX + panelW * 0.25f, btnY, 0, Align.center, false);
        
        font.setColor(selectedOption == 1 ? Color.YELLOW : Color.WHITE);
        font.draw(batch, selectedOption == 1 ? "> " + noText + " <" : noText, panelX + panelW * 0.75f, btnY, 0, Align.center, false);

        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);
    }

    @Override
    public boolean isFinished() { return exitRequested; }

    @Override
    public boolean isWon() { return won; }

    @Override
    public void dispose() {
        if (dimTexture != null) dimTexture.dispose();
        if (panelTex != null) panelTex.dispose();
    }

    @Override
    public void forceEnd(boolean win) {
        this.won = win;
        this.finished = true;
        this.exitRequested = true;
        StoryManager.getInstance().recordResult(MinigameID.PET_BEG, win);
    }
}
