package hust.hedspi.oop.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import hust.hedspi.oop.game.managers.GameManager;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.managers.ScreenManager;
import hust.hedspi.oop.game.utils.Constants;

public class MainMenuScreen implements Screen {
    private enum State {
        MAIN_MENU,
        CREDITS,
        HOW_TO_PLAY,
        ACHIEVEMENTS
    }

    private State currentState = State.MAIN_MENU;
    private Screen nextScreen = null;

    private SpriteBatch batch;
    private Viewport viewport;

    // Textures
    private Texture bgMainMenu;
    private Texture bgSubScreen;
    private Texture timeframeTex;
    private NinePatch timeframePatch;

    // Menu Buttons
    private Texture newGameTex;
    private Texture continueTex;
    private Texture achievementTex;
    private Texture howToPlayTex;
    private Texture creditTex;

    // UI Buttons (Blue for Back button)
    private Texture btnTex;
    private Texture btnPressedTex;
    private NinePatch btnPatch;
    private NinePatch btnPressedPatch;

    // Fonts
    private BitmapFont font;
    private BitmapFont titleFont;

    // Brown color for tinting (darken by 30% on hover, 60% on press)
    private static final Color BROWN = new Color(0.45f, 0.25f, 0.12f, 1f);
    private Color hoverColor;
    private Color pressedColor;

    public MainMenuScreen() {
        batch = new SpriteBatch();
        viewport = new FitViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);

        // Load textures from assets/menu
        bgMainMenu = new Texture(Gdx.files.internal("menu/menu_background.png"));
        bgSubScreen = new Texture(Gdx.files.internal("menu/background.png"));
        timeframeTex = new Texture(Gdx.files.internal("menu/timeframe.png"));
        timeframePatch = new NinePatch(timeframeTex, 8, 8, 8, 8);

        newGameTex = new Texture(Gdx.files.internal("menu/new_game.png"));
        continueTex = new Texture(Gdx.files.internal("menu/continue.png"));
        achievementTex = new Texture(Gdx.files.internal("menu/archievement.png"));
        howToPlayTex = new Texture(Gdx.files.internal("menu/how_to_play.png"));
        creditTex = new Texture(Gdx.files.internal("menu/credit.png"));

        // Load UI button textures for NinePatch return buttons
        btnTex = new Texture(Gdx.files.internal("images/HUD/ui/button/button_blue.png"));
        btnPressedTex = new Texture(Gdx.files.internal("images/HUD/ui/button/button_blue_pressed.png"));
        btnPatch = new NinePatch(btnTex, 4, 4, 4, 4);
        btnPressedPatch = new NinePatch(btnPressedTex, 4, 4, 4, 4);

        font = ResourceManager.getInstance().dialogFont;
        titleFont = ResourceManager.getInstance().hudFont;

        // Calculate tint colors by lerping Color.WHITE towards BROWN
        hoverColor = Color.WHITE.cpy().lerp(BROWN, 0.3f);
        pressedColor = Color.WHITE.cpy().lerp(BROWN, 0.6f);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null); // Direct mouse testing inside render
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.getCamera().update();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        // Get mouse position unprojected to virtual screen coordinates
        float mx = Gdx.input.getX();
        float my = Gdx.input.getY();
        Vector3 unprojected = viewport.getCamera().unproject(new Vector3(mx, my, 0));
        float mouseX = unprojected.x;
        float mouseY = unprojected.y;

        batch.begin();

        switch (currentState) {
            case MAIN_MENU:
                renderMainMenu(batch, mouseX, mouseY);
                break;
            case CREDITS:
                renderCredits(batch, mouseX, mouseY);
                break;
            case HOW_TO_PLAY:
                renderHowToPlay(batch, mouseX, mouseY);
                break;
            case ACHIEVEMENTS:
                renderAchievements(batch, mouseX, mouseY);
                break;
        }

        batch.end();

        // Defer screen transition to prevent disposing assets while batch is rendering/flushing
        if (nextScreen != null) {
            ScreenManager.getInstance().clearAndSetScreen(nextScreen);
        }
    }

    private void renderMainMenu(SpriteBatch batch, float mouseX, float mouseY) {
        // Draw background
        batch.draw(bgMainMenu, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);

        // 5 buttons on the left
        float btnX = 80f;
        float spacing = 20f;

        float scale = 0.6f;
        float h0 = newGameTex.getHeight() * scale;
        float h1 = continueTex.getHeight() * scale;
        float h2 = achievementTex.getHeight() * scale;
        float h3 = howToPlayTex.getHeight() * scale;
        float h4 = creditTex.getHeight() * scale;

        float w0 = newGameTex.getWidth() * scale;
        float w1 = continueTex.getWidth() * scale;
        float w2 = achievementTex.getWidth() * scale;
        float w3 = howToPlayTex.getWidth() * scale;
        float w4 = creditTex.getWidth() * scale;

        // Calculate total vertical height to center the buttons vertically and shift down slightly
        float totalH = h0 + h1 + h2 + h3 + h4 + 4 * spacing;
        float startY = ((Constants.VIRTUAL_HEIGHT - totalH) / 2f) - 90f;

        // Draw buttons bottom-up (Credit at the bottom, New Game at the top)
        
        // 5. Credit
        float y4 = startY;
        if (handleButton(batch, creditTex, btnX, y4, w4, h4, mouseX, mouseY, true)) {
            currentState = State.CREDITS;
        }

        // 4. How To Play
        float y3 = y4 + h4 + spacing;
        if (handleButton(batch, howToPlayTex, btnX, y3, w3, h3, mouseX, mouseY, true)) {
            currentState = State.HOW_TO_PLAY;
        }

        // 3. Achievement
        float y2 = y3 + h3 + spacing;
        if (handleButton(batch, achievementTex, btnX, y2, w2, h2, mouseX, mouseY, true)) {
            currentState = State.ACHIEVEMENTS;
        }

        // 2. Continue (Enabled only if active session exists in memory)
        boolean hasSave = GameManager.getInstance().getPlayer() != null;
        float y1 = y2 + h2 + spacing;
        if (handleButton(batch, continueTex, btnX, y1, w1, h1, mouseX, mouseY, hasSave)) {
            nextScreen = new PlayScreen(true);
        }

        // 1. New Game
        float y0 = y1 + h1 + spacing;
        if (handleButton(batch, newGameTex, btnX, y0, w0, h0, mouseX, mouseY, true)) {
            nextScreen = new PlayScreen(false);
        }
    }

    private void renderCredits(SpriteBatch batch, float mouseX, float mouseY) {
        // Draw background
        batch.draw(bgSubScreen, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);

        // Draw NinePatch timeframe panel
        float panelW = 750f;
        float panelH = 450f;
        float panelX = (Constants.VIRTUAL_WIDTH - panelW) / 2f;
        float panelY = (Constants.VIRTUAL_HEIGHT - panelH) / 2f;
        timeframePatch.draw(batch, panelX, panelY, panelW, panelH);

        // Draw Title
        titleFont.setColor(Color.YELLOW);
        titleFont.draw(batch, "THÀNH VIÊN THỰC HIỆN", panelX, panelY + panelH - 50f, panelW, Align.center, false);
        titleFont.setColor(Color.WHITE);

        // Draw member names placeholder (centered text)
        font.getData().setScale(1.1f);
        // GHI CHÚ TRONG CODE: Thêm thông tin danh sách các thành viên thực hiện tại đây
        String membersText = "\n" +
            "THÀNH VIÊN DỰ ÁN\n\n" +
            "1. [Thành viên 1]\n" +
            "2. [Thành viên 2]\n" +
            "3. [Thành viên 3]\n\n" +
            "(Hãy chỉnh sửa MainMenuScreen.java để cập nhật danh sách này)";
        font.draw(batch, membersText, panelX + 50f, panelY + panelH - 120f, panelW - 100f, Align.center, true);
        font.getData().setScale(1.0f);

        // Draw "Quay lại" button inside timeframe frame
        float btnW = 180f;
        float btnH = 60f;
        float btnX = (Constants.VIRTUAL_WIDTH - btnW) / 2f;
        float btnY = panelY + 40f;

        boolean hovered = (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH);
        boolean pressed = hovered && Gdx.input.isTouched();

        if (pressed) {
            btnPressedPatch.draw(batch, btnX, btnY, btnW, btnH);
        } else {
            btnPatch.draw(batch, btnX, btnY, btnW, btnH);
        }

        font.setColor(hovered ? Color.YELLOW : Color.WHITE);
        font.draw(batch, "Quay lại", btnX, btnY + btnH / 2f + 8f, btnW, Align.center, false);
        font.setColor(Color.WHITE);

        if (hovered && Gdx.input.justTouched()) {
            currentState = State.MAIN_MENU;
        }
    }

    private void renderHowToPlay(SpriteBatch batch, float mouseX, float mouseY) {
        // Draw background
        batch.draw(bgSubScreen, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);

        // Draw NinePatch timeframe panel
        float panelW = 750f;
        float panelH = 450f;
        float panelX = (Constants.VIRTUAL_WIDTH - panelW) / 2f;
        float panelY = (Constants.VIRTUAL_HEIGHT - panelH) / 2f;
        timeframePatch.draw(batch, panelX, panelY, panelW, panelH);

        // Draw Title
        titleFont.setColor(Color.YELLOW);
        titleFont.draw(batch, "HƯỚNG DẪN CHƠI", panelX, panelY + panelH - 50f, panelW, Align.center, false);
        titleFont.setColor(Color.WHITE);

        // Draw guide content (left aligned with padding)
        font.getData().setScale(1.1f);
        String guideText = "\n" +
            "- Di chuyển: Sử dụng các phím WASD hoặc Mũi Tên\n" +
            "- Tương tác (Nhiệm vụ/Minigame): Nhấn phím SPACE\n" +
            "- Tạm dừng trò chơi: Nhấn phím ESC\n" +
            "- Mở bảng điều khiển gỡ lỗi (Debug): Nhấn phím F12\n\n" +
            "Hãy hoàn thành các minigame và thử thách sinh tồn để mở khóa đầy đủ 7 kết cục (Ending) của trò chơi!";
        font.draw(batch, guideText, panelX + 50f, panelY + panelH - 120f, panelW - 100f, Align.left, true);
        font.getData().setScale(1.0f);

        // Draw "Quay lại" button inside timeframe frame
        float btnW = 180f;
        float btnH = 60f;
        float btnX = (Constants.VIRTUAL_WIDTH - btnW) / 2f;
        float btnY = panelY + 40f;

        boolean hovered = (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH);
        boolean pressed = hovered && Gdx.input.isTouched();

        if (pressed) {
            btnPressedPatch.draw(batch, btnX, btnY, btnW, btnH);
        } else {
            btnPatch.draw(batch, btnX, btnY, btnW, btnH);
        }

        font.setColor(hovered ? Color.YELLOW : Color.WHITE);
        font.draw(batch, "Quay lại", btnX, btnY + btnH / 2f + 8f, btnW, Align.center, false);
        font.setColor(Color.WHITE);

        if (hovered && Gdx.input.justTouched()) {
            currentState = State.MAIN_MENU;
        }
    }

    private void renderAchievements(SpriteBatch batch, float mouseX, float mouseY) {
        // Draw background
        batch.draw(bgSubScreen, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);

        // Draw NinePatch timeframe panel
        float panelW = 850f;
        float panelH = 480f;
        float panelX = (Constants.VIRTUAL_WIDTH - panelW) / 2f;
        float panelY = (Constants.VIRTUAL_HEIGHT - panelH) / 2f;
        timeframePatch.draw(batch, panelX, panelY, panelW, panelH);

        // Draw Title
        titleFont.setColor(Color.YELLOW);
        titleFont.draw(batch, "DANH SÁCH THÀNH TỰU", panelX, panelY + panelH - 45f, panelW, Align.center, false);
        titleFont.setColor(Color.WHITE);

        // Draw Headers: STT, Tên Kết Cục, Checkbox
        font.getData().setScale(1.1f);
        font.setColor(Color.GOLD);
        font.draw(batch, "STT", panelX + 80f, panelY + panelH - 95f);
        font.draw(batch, "Tên Kết Cục", panelX + 220f, panelY + panelH - 95f);
        font.draw(batch, "Mở Khóa", panelX + 620f, panelY + panelH - 95f);
        font.setColor(Color.WHITE);

        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("CatLife_Endings");

        // Render each ending row
        for (int i = 0; i < 7; i++) {
            float y = panelY + panelH - 145f - i * 40f;
            String endingName = hust.hedspi.oop.game.managers.SaveManager.OFFICIAL_ENDINGS[i];
            boolean unlocked = prefs.getBoolean(endingName, false);

            // STT
            font.draw(batch, String.valueOf(i + 1), panelX + 80f, y + 20f);

            // Tên Kết Cục
            if (unlocked) {
                font.setColor(Color.WHITE);
                font.draw(batch, endingName, panelX + 220f, y + 20f);
            } else {
                // Build red question marks string preserving spaces
                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < endingName.length(); k++) {
                    if (endingName.charAt(k) == ' ') {
                        sb.append(' ');
                    } else {
                        sb.append('?');
                    }
                }
                font.setColor(Color.RED);
                font.draw(batch, sb.toString(), panelX + 220f, y + 20f);
            }
            font.setColor(Color.WHITE);

            // Checkbox: boxSize = 24
            float boxSize = 24f;
            float boxX = panelX + 650f;
            float boxY = y + 2f;

            if (unlocked) {
                // Checked: active blue button with a yellow 'X' in the center
                btnPatch.draw(batch, boxX, boxY, boxSize, boxSize);
                font.setColor(Color.YELLOW);
                font.draw(batch, "X", boxX, boxY + boxSize / 2f + 7f, boxSize, Align.center, false);
                font.setColor(Color.WHITE);
            } else {
                // Unchecked: dark blue/pressed empty box
                btnPressedPatch.draw(batch, boxX, boxY, boxSize, boxSize);
            }
        }
        font.getData().setScale(1.0f);

        // Draw "Quay lại" button inside timeframe frame
        float btnW = 180f;
        float btnH = 60f;
        float btnX = (Constants.VIRTUAL_WIDTH - btnW) / 2f;
        float btnY = panelY + 25f;

        boolean hovered = (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH);
        boolean pressed = hovered && Gdx.input.isTouched();

        if (pressed) {
            btnPressedPatch.draw(batch, btnX, btnY, btnW, btnH);
        } else {
            btnPatch.draw(batch, btnX, btnY, btnW, btnH);
        }

        font.setColor(hovered ? Color.YELLOW : Color.WHITE);
        font.draw(batch, "Quay lại", btnX, btnY + btnH / 2f + 8f, btnW, Align.center, false);
        font.setColor(Color.WHITE);

        if (hovered && Gdx.input.justTouched()) {
            currentState = State.MAIN_MENU;
        }
    }

    /**
     * Draws a button texture, checks for mouse interaction, and tints it with a brown tone.
     * 30% brown for hover, 60% brown for pressed. 50% opacity gray for disabled.
     */
    private boolean handleButton(SpriteBatch batch, Texture tex, float x, float y, float w, float h, float mouseX, float mouseY, boolean enabled) {
        boolean hovered = enabled && (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h);
        boolean pressed = hovered && Gdx.input.isTouched();

        if (!enabled) {
            batch.setColor(0.5f, 0.5f, 0.5f, 0.5f); // Gray out and 50% opacity
        } else if (pressed) {
            batch.setColor(pressedColor); // 60% brown tint
        } else if (hovered) {
            batch.setColor(hoverColor);   // 30% brown tint
        } else {
            batch.setColor(Color.WHITE);  // Normal
        }

        batch.draw(tex, x, y, w, h);
        batch.setColor(Color.WHITE); // Reset batch color

        return hovered && Gdx.input.justTouched();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        bgMainMenu.dispose();
        bgSubScreen.dispose();
        timeframeTex.dispose();

        newGameTex.dispose();
        continueTex.dispose();
        achievementTex.dispose();
        howToPlayTex.dispose();
        creditTex.dispose();

        btnTex.dispose();
        btnPressedTex.dispose();
    }
}
