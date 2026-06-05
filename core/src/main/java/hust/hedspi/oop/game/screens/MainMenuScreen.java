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
    private Texture quitBtnTex;

    // Achievement Screen specific textures
    private Texture boardTex;
    private NinePatch boardPatch;
    private Texture catDecorTex;
    private Texture tittleTex;

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
        quitBtnTex = new Texture(Gdx.files.internal("menu/quit_button.png"));

        // Load achievements specific assets
        boardTex = new Texture(Gdx.files.internal("menu/achievement/board.png"));
        // TODO: Chỉnh sửa các thông số chia NinePatch của board.png ở đây để điều chỉnh bo viền/giãn góc
        // Định dạng: new NinePatch(texture, left, right, top, bottom)
        boardPatch = new NinePatch(boardTex, 12, 12, 12, 12);
        catDecorTex = new Texture(Gdx.files.internal("menu/achievement/cat_decor.png"));
        tittleTex = new Texture(Gdx.files.internal("menu/achievement/tittle.png"));

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

        // Quit Button (Bottom-Right corner, aligned with Credit button y4)
        float qw = quitBtnTex.getWidth() * scale;
        float qh = quitBtnTex.getHeight() * scale;
        float qx = Constants.VIRTUAL_WIDTH - qw - 80f;
        float qy = startY;
        if (handleButton(batch, quitBtnTex, qx, qy, qw, qh, mouseX, mouseY, true)) {
            Gdx.app.exit();
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

        // Draw member names 
        font.getData().setScale(1.1f);
        String membersText = "\n" +
            "coming soon";
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
            "- Tạm dừng trò chơi: Nhấn phím ESC\n\n" +
            // "- Mở bảng điều khiển gỡ lỗi (Debug): Nhấn phím F12\n\n" +
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

        // Draw NinePatch timeframe panel (enlarged to 1000x550)
        float panelW = 1000f;
        float panelH = 550f;
        float panelX = (Constants.VIRTUAL_WIDTH - panelW) / 2f;
        float panelY = (Constants.VIRTUAL_HEIGHT - panelH) / 2f;
        timeframePatch.draw(batch, panelX, panelY, panelW, panelH);

        // Draw Title (tittle.png) at the top of the panel (scaled to fit nicely)
        float titleImgW = tittleTex.getWidth();
        float titleImgH = tittleTex.getHeight();
        float titleScale = Math.min(panelW * 0.6f / titleImgW, 75f / titleImgH);
        float drawTitleW = titleImgW * titleScale;
        float drawTitleH = titleImgH * titleScale;
        float drawTitleX = panelX + (panelW - drawTitleW) / 2f;
        float drawTitleY = panelY + panelH - drawTitleH - 22f;
        batch.draw(tittleTex, drawTitleX, drawTitleY, drawTitleW, drawTitleH);

        // Define content layout coordinates
        float padLeft = 50f;
        float padRight = 50f;
        float padBottom = 75f; // Space for the back button
        float contentW = panelW - padLeft - padRight; // 900f
        float contentH = 370f; // Heightened board & decor
        float gap = 25f;
        float usableW = contentW - gap; // 875f

        // Adjust layout: widen the board and narrow cat_decor (650:225)
        float boardW = 650f;
        float decorW = 225f;

        float boardX = panelX + padLeft;
        float boardY = panelY + padBottom;
        float decorX = boardX + boardW + gap;
        float decorY = panelY + padBottom;

        // Draw Board (board.png) on the left using NinePatch
        boardPatch.draw(batch, boardX, boardY, boardW, contentH);

        // Draw Cat Decor (cat_decor.png) on the right (preserving aspect ratio)
        float decorImgW = catDecorTex.getWidth();
        float decorImgH = catDecorTex.getHeight();
        float decorScale = Math.min(decorW / decorImgW, contentH / decorImgH);
        float drawDecorW = decorImgW * decorScale;
        float drawDecorH = decorImgH * decorScale;
        float drawDecorX = decorX + (decorW - drawDecorW) / 2f;
        float drawDecorY = decorY + (contentH - drawDecorH) / 2f;
        batch.draw(catDecorTex, drawDecorX, drawDecorY, drawDecorW, drawDecorH);

        // Draw Headers: STT, Tên Kết Cục, Mở Khóa inside the board
        float boardPadLeft = 40f;
        float boardPadRight = 40f;
        float boardPadTop = 25f;
        float headerY = boardY + contentH - boardPadTop - 20f;

        font.getData().setScale(0.85f); // Set to 0.85f for better legibility on a larger board
        font.setColor(Color.GOLD);
        drawBoldText(batch, "STT", boardX + boardPadLeft, headerY);
        drawBoldText(batch, "Tên Kết Cục", boardX + boardPadLeft + 60f, headerY);
        drawBoldText(batch, "Mở Khóa", boardX + boardW - boardPadRight - 85f, headerY);
        font.setColor(Color.WHITE);

        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("CatLife_Endings");

        // Render ending rows with closer vertical spacing (38f)
        for (int i = 0; i < 7; i++) {
            float y = boardY + contentH - boardPadTop - 55f - i * 38f;
            String endingName = hust.hedspi.oop.game.managers.SaveManager.OFFICIAL_ENDINGS[i];
            boolean unlocked = prefs.getBoolean(endingName, false);

            // STT
            drawBoldText(batch, String.valueOf(i + 1), boardX + boardPadLeft, y + 20f);

            // Tên Kết Cục
            if (unlocked) {
                font.setColor(Color.WHITE);
                drawBoldText(batch, endingName, boardX + boardPadLeft + 60f, y + 20f);
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
                drawBoldText(batch, sb.toString(), boardX + boardPadLeft + 60f, y + 20f);
            }
            font.setColor(Color.WHITE);

            // Checkbox: boxSize = 26
            float boxSize = 26f;
            float boxX = boardX + boardW - boardPadRight - 60f;
            float boxY = y + 2f;

            if (unlocked) {
                // Checked: active blue button with a yellow 'X' in the center
                btnPatch.draw(batch, boxX, boxY, boxSize, boxSize);
                font.setColor(Color.YELLOW);
                drawBoldText(batch, "X", boxX, boxY + boxSize / 2f + 7f, boxSize, Align.center, false);
                font.setColor(Color.WHITE);
            } else {
                // Unchecked: dark blue/pressed empty box
                btnPressedPatch.draw(batch, boxX, boxY, boxSize, boxSize);
            }
        }
        font.getData().setScale(1.0f);

        // Draw "Quay lại" button inside timeframe frame (re-centered and scaled for 1000x550)
        float btnW = 160f;
        float btnH = 45f;
        float btnX = (Constants.VIRTUAL_WIDTH - btnW) / 2f;
        float btnY = panelY + 18f;

        boolean hovered = (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH);
        boolean pressed = hovered && Gdx.input.isTouched();

        if (pressed) {
            btnPressedPatch.draw(batch, btnX, btnY, btnW, btnH);
        } else {
            btnPatch.draw(batch, btnX, btnY, btnW, btnH);
        }

        font.setColor(hovered ? Color.YELLOW : Color.WHITE);
        drawBoldText(batch, "Quay lại", btnX, btnY + btnH / 2f + 6f, btnW, Align.center, false);
        font.setColor(Color.WHITE);

        if (hovered && Gdx.input.justTouched()) {
            currentState = State.MAIN_MENU;
        }
    }

    private void drawBoldText(SpriteBatch batch, String text, float x, float y) {
        font.draw(batch, text, x, y);
        font.draw(batch, text, x + 0.6f, y);
    }

    private void drawBoldText(SpriteBatch batch, String text, float x, float y, float targetWidth, int align, boolean wrap) {
        font.draw(batch, text, x, y, targetWidth, align, wrap);
        font.draw(batch, text, x + 0.6f, y, targetWidth, align, wrap);
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
        quitBtnTex.dispose();

        boardTex.dispose();
        catDecorTex.dispose();
        tittleTex.dispose();

        btnTex.dispose();
        btnPressedTex.dispose();
    }
}
