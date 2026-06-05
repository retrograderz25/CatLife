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

    private static final String[] CREDIT_LINES = {
        "CAT LIFE",
        "",
        "--- ĐỘI NGŨ PHÁT TRIỂN ---",
        "// TODO: Nhập danh sách thành viên phát triển tại đây",
        "coming soon",
        "coming soon",
        "coming soon",
        "",
        "--- HÌNH ẢNH & ĐỒ HỌA ---",
        "// TODO: Nhập danh sách người thiết kế đồ họa tại đây",
        "coming soon",
        "coming soon",
        "",
        "--- ÂM NHẠC & ÂM THANH ---",
        "// TODO: Nhập danh sách người phụ trách âm nhạc tại đây",
        "coming soon",
        "coming soon",
        "",
        "--- CÔNG NGHỆ SỬ DỤNG ---",
        "LibGDX - Java Game Framework",
        "Gradle Build System",
        "",
        "--- BẢN QUYỀN ---",
        "Copyright 2026 Hedspi OOP Group",
        "All Rights Reserved",
        ""
    };

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

    // Credit Screen specific assets and states
    private Texture creEndTex;
    private float creditScrollY = 0f;
    private boolean isAutoScrolling = true;
    private boolean isDraggingScrollbar = false;

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

        // Load credit specific assets
        creEndTex = new Texture(Gdx.files.internal("menu/cre_end.png"));

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
            creditScrollY = 0f;
            isAutoScrolling = true;
            isDraggingScrollbar = false;
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

        // Draw Title (Changed to "Credit")
        titleFont.setColor(Color.YELLOW);
        titleFont.draw(batch, "Credit", panelX, panelY + panelH - 45f, panelW, Align.center, false);
        titleFont.setColor(Color.WHITE);

        // Define bounds for clipping box inside timeframe panel
        float clipX = panelX + 40f;
        float clipY = panelY + 110f;
        float clipW = panelW - 80f;
        float clipH = panelH - 170f; // Leaves room for header and back button

        // Calculate scroll bounds
        float topSpace = clipH; // Start below the bottom of the clipping area
        float lineSpacing = 35f;
        float textH = CREDIT_LINES.length * lineSpacing;
        float gap = 50f;
        float creEndW = 200f;
        float creEndH = creEndW * creEndTex.getHeight() / creEndTex.getWidth();

        // We want the scroll to stop when cre_end.png is perfectly centered in the clipping box
        float maxScrollY = (topSpace + textH + gap) - (clipH - creEndH) / 2f;
        if (maxScrollY < 0f) maxScrollY = 0f;

        // Drag handle dimensions
        float trackX = panelX + panelW - 35f;
        float trackY = clipY;
        float trackW = 8f;
        float trackH = clipH;

        float handleH = 40f;
        float handleRange = trackH - handleH;

        // Update auto-scroll
        float delta = Gdx.graphics.getDeltaTime();
        if (isAutoScrolling) {
            creditScrollY += delta * 45f; // scroll speed: 45 pixels per second
            if (creditScrollY >= maxScrollY) {
                creditScrollY = maxScrollY;
                isAutoScrolling = false;
            }
        }

        // Handle scrollbar dragging
        if (Gdx.input.isTouched()) {
            if (Gdx.input.justTouched()) {
                // Check if user clicked on or near the scrollbar
                if (mouseX >= trackX - 15f && mouseX <= trackX + trackW + 15f && mouseY >= trackY && mouseY <= trackY + trackH) {
                    isDraggingScrollbar = true;
                    isAutoScrolling = false;
                }
            }

            if (isDraggingScrollbar) {
                float relativeMouseY = mouseY - trackY - handleH / 2f;
                float scrollRatio = 1f - (relativeMouseY / handleRange);
                scrollRatio = Math.max(0f, Math.min(1f, scrollRatio));
                creditScrollY = scrollRatio * maxScrollY;
            }
        } else {
            isDraggingScrollbar = false;
        }

        // Clip drawing to the interior of the timeframe panel using ScissorStack
        batch.flush();
        com.badlogic.gdx.math.Rectangle clipBounds = new com.badlogic.gdx.math.Rectangle(clipX, clipY, clipW, clipH);
        com.badlogic.gdx.math.Rectangle scissors = new com.badlogic.gdx.math.Rectangle();
        com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.calculateScissors(viewport.getCamera(), batch.getTransformMatrix(), clipBounds, scissors);
        boolean scissorPushed = com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.pushScissors(scissors);

        if (scissorPushed) {
            // Draw credit lines
            font.getData().setScale(1.0f);
            for (int i = 0; i < CREDIT_LINES.length; i++) {
                float docY = topSpace + i * lineSpacing;
                float drawY = (clipY + clipH) - (docY - creditScrollY);
                // Centered inside clipW
                font.draw(batch, CREDIT_LINES[i], clipX, drawY + 12f, clipW, Align.center, false);
            }

            // Draw cre_end.png at the bottom of the credits scroll
            float imgDocY = topSpace + textH + gap;
            float imgDrawY = (clipY + clipH) - (imgDocY - creditScrollY) - creEndH;
            float imgDrawX = clipX + (clipW - creEndW) / 2f;
            batch.draw(creEndTex, imgDrawX, imgDrawY, creEndW, creEndH);

            batch.flush();
            com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.popScissors();
        }

        // Draw scrollbar background track (thin pressed button style)
        btnPressedPatch.draw(batch, trackX, trackY, trackW, trackH);

        // Draw scrollbar handle (blue button style)
        float scrollRatio = maxScrollY > 0f ? creditScrollY / maxScrollY : 0f;
        float handleY = (trackY + trackH - handleH) - scrollRatio * handleRange;
        btnPatch.draw(batch, trackX - 2f, handleY, trackW + 4f, handleH);

        // Draw "Quay lại" button inside timeframe frame
        float btnW = 160f;
        float btnH = 45f;
        float btnX = (Constants.VIRTUAL_WIDTH - btnW) / 2f;
        float btnY = panelY + 30f;

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

        // Draw Headers: STT, Tên Kết Cục, Trạng Thái inside the board
        float boardPadLeft = 40f;
        float boardPadRight = 40f;
        float boardPadTop = 25f;
        float headerY = boardY + contentH - boardPadTop - 15f; // Raised to prevent overlap

        font.getData().setScale(0.85f); // Set to 0.85f for better legibility on a larger board
        font.setColor(new Color(0.85f, 0.65f, 0f, 1f)); // Dark yellow color
        drawBoldText(batch, "STT", boardX + boardPadLeft, headerY);
        drawBoldText(batch, "Tên Kết Cục", boardX + boardPadLeft + 60f, headerY);
        drawBoldText(batch, "Trạng Thái", boardX + boardW - boardPadRight - 90f, headerY); // Centered over checkbox column
        font.setColor(Color.WHITE);

        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("CatLife_Endings");

        // Render ending rows with closer vertical spacing (38f) starting lower to prevent overlap
        for (int i = 0; i < 7; i++) {
            float y = boardY + contentH - boardPadTop - 62f - i * 38f;
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
        creEndTex.dispose();

        btnTex.dispose();
        btnPressedTex.dispose();
    }
}
