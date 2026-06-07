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
import hust.hedspi.oop.game.managers.SoundManager;
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
            "",
            "",
            "",
            "",
            "--- ĐỘI NGŨ PHÁT TRIỂN ---",
            "Nguyễn Bình",
            "Nguyễn Quang Anh aka tuyenthuchuyennghiep",
            "Bùi Ngọc Trung",
            "Vũ Lan Anh",
            "Hoàng Bình Phương",
            "",
            "--- HÌNH ẢNH & ĐỒ HỌA ---",
            "Vũ Lan Anh",
            "Nguyễn Quang Anh",
            "Nguyễn Bình",
            "Nguyễn Gemini",
            "",
            "--- ÂM NHẠC & ÂM THANH ---",
            "Bùi Ngọc Trung",
            "Hoàng Bình Phương",
            "",
            "--- CÔNG NGHỆ SỬ DỤNG ---",
            "LibGDX - Java Game Framework",
            "Gradle Build System",
            "",
            
            
            
            
    };

    private SpriteBatch batch;
    private Viewport viewport;

    
    private Texture bgMainMenu;
    private Texture bgSubScreen;
    private Texture timeframeTex;
    private NinePatch timeframePatch;

    
    private Texture newGameTex;
    private Texture continueTex;
    private Texture achievementTex;
    private Texture howToPlayTex;
    private Texture creditTex;
    private Texture quitBtnTex;

    
    private Texture boardTex;
    private NinePatch boardPatch;
    private Texture catDecorTex;
    private Texture tittleTex;

    
    private Texture creEndTex;
    private Texture titleVnTex;
    private float creditScrollY = 0f;
    private boolean isAutoScrolling = true;
    private boolean isDraggingScrollbar = false;

    
    private Texture btnTex;
    private Texture btnPressedTex;
    private NinePatch btnPatch;
    private NinePatch btnPressedPatch;

    
    private BitmapFont font;
    private BitmapFont titleFont;

    
    private static final Color BROWN = new Color(0.45f, 0.25f, 0.12f, 1f);
    private Color hoverColor;
    private Color pressedColor;

    public MainMenuScreen() {
        batch = new SpriteBatch();
        viewport = new FitViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);

        
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

        
        boardTex = new Texture(Gdx.files.internal("menu/achievement/board.png"));
        
        
        
        boardPatch = new NinePatch(boardTex, 12, 12, 12, 12);
        catDecorTex = new Texture(Gdx.files.internal("menu/achievement/cat_decor.png"));
        tittleTex = new Texture(Gdx.files.internal("menu/achievement/tittle.png"));

        
        creEndTex = new Texture(Gdx.files.internal("menu/cre_end.png"));
        titleVnTex = new Texture(Gdx.files.internal("menu/giat_tit_vn.png"));

        
        btnTex = new Texture(Gdx.files.internal("images/HUD/ui/button/button_blue.png"));
        btnPressedTex = new Texture(Gdx.files.internal("images/HUD/ui/button/button_blue_pressed.png"));
        btnPatch = new NinePatch(btnTex, 4, 4, 4, 4);
        btnPressedPatch = new NinePatch(btnPressedTex, 4, 4, 4, 4);

        font = ResourceManager.getInstance().dialogFont;
        titleFont = ResourceManager.getInstance().hudFont;

        
        hoverColor = Color.WHITE.cpy().lerp(BROWN, 0.3f);
        pressedColor = Color.WHITE.cpy().lerp(BROWN, 0.6f);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null); 
        SoundManager.getInstance().playBGM(SoundManager.BGM_MENU);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.getCamera().update();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        
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

        
        
        if (nextScreen != null) {
            ScreenManager.getInstance().clearAndSetScreen(nextScreen);
        }
    }

    private void renderMainMenu(SpriteBatch batch, float mouseX, float mouseY) {
        
        batch.draw(bgMainMenu, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);

        
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

        
        
        float totalH = h0 + h1 + h2 + h3 + h4 + 4 * spacing;
        float startY = ((Constants.VIRTUAL_HEIGHT - totalH) / 2f) - 90f;

        

        
        float y4 = startY;
        if (handleButton(batch, creditTex, btnX, y4, w4, h4, mouseX, mouseY, true)) {
            currentState = State.CREDITS;
            creditScrollY = 0f;
            isAutoScrolling = true;
            isDraggingScrollbar = false;
        }

        
        float y3 = y4 + h4 + spacing;
        if (handleButton(batch, howToPlayTex, btnX, y3, w3, h3, mouseX, mouseY, true)) {
            currentState = State.HOW_TO_PLAY;
        }

        
        float y2 = y3 + h3 + spacing;
        if (handleButton(batch, achievementTex, btnX, y2, w2, h2, mouseX, mouseY, true)) {
            SoundManager.getInstance().playSFX(SoundManager.SFX_UI_CONFIRM);
            currentState = State.ACHIEVEMENTS;
        }

        
        boolean hasSave = GameManager.getInstance().getPlayer() != null;
        float y1 = y2 + h2 + spacing;
        if (handleButton(batch, continueTex, btnX, y1, w1, h1, mouseX, mouseY, hasSave)) {
            SoundManager.getInstance().playSFX(SoundManager.SFX_UI_CONFIRM);
            nextScreen = new PlayScreen(true);
        }

        
        float y0 = y1 + h1 + spacing;
        if (handleButton(batch, newGameTex, btnX, y0, w0, h0, mouseX, mouseY, true)) {
            SoundManager.getInstance().playSFX(SoundManager.SFX_UI_CONFIRM);
            nextScreen = new PlayScreen(false);
        }

        
        float qw = quitBtnTex.getWidth() * scale;
        float qh = quitBtnTex.getHeight() * scale;
        float qx = Constants.VIRTUAL_WIDTH - qw - 80f;
        float qy = startY;
        if (handleButton(batch, quitBtnTex, qx, qy, qw, qh, mouseX, mouseY, true)) {
            SoundManager.getInstance().playSFX(SoundManager.SFX_UI_CANCEL);
            Gdx.app.exit();
        }
    }

    private void renderCredits(SpriteBatch batch, float mouseX, float mouseY) {
        
        batch.draw(bgSubScreen, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);

        
        float panelW = 750f;
        float panelH = 450f;
        float panelX = (Constants.VIRTUAL_WIDTH - panelW) / 2f;
        float panelY = (Constants.VIRTUAL_HEIGHT - panelH) / 2f;
        timeframePatch.draw(batch, panelX, panelY, panelW, panelH);

        
        titleFont.setColor(Color.YELLOW);
        titleFont.draw(batch, "Credit", panelX, panelY + panelH - 45f, panelW, Align.center, false);
        titleFont.setColor(Color.WHITE);

        
        float clipX = panelX + 40f;
        float clipY = panelY + 110f;
        float clipW = panelW - 80f;
        float clipH = panelH - 195f; 
                                     

        
        float topSpace = clipH; 
        float lineSpacing = 35f;
        float textH = CREDIT_LINES.length * lineSpacing;
        float gap = 50f;

        
        float creEndW = 380f;
        float creEndH = creEndW * creEndTex.getHeight() / creEndTex.getWidth();
        if (creEndH > clipH - 40f) {
            creEndH = clipH - 40f;
            creEndW = creEndH * creEndTex.getWidth() / creEndTex.getHeight();
        }

        
        
        float maxScrollY = (topSpace + textH + gap) - (clipH - creEndH) / 2f;
        if (maxScrollY < 0f)
            maxScrollY = 0f;

        
        float trackX = panelX + panelW - 35f;
        float trackY = clipY;
        float trackW = 8f;
        float trackH = clipH;

        float handleH = 40f;
        float handleRange = trackH - handleH;

        
        float delta = Gdx.graphics.getDeltaTime();
        if (isAutoScrolling) {
            creditScrollY += delta * 45f; 
            if (creditScrollY >= maxScrollY) {
                creditScrollY = maxScrollY;
                isAutoScrolling = false;
            }
        }

        
        if (Gdx.input.isTouched()) {
            if (Gdx.input.justTouched()) {
                
                if (mouseX >= trackX - 15f && mouseX <= trackX + trackW + 15f && mouseY >= trackY
                        && mouseY <= trackY + trackH) {
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
        // cái này dùng để giới hạn vùng vẽ credit, không cho tràn ra ngoài khung gỗ
        batch.flush();
        com.badlogic.gdx.math.Rectangle clipBounds = new com.badlogic.gdx.math.Rectangle(clipX, clipY, clipW, clipH);
        com.badlogic.gdx.math.Rectangle scissors = new com.badlogic.gdx.math.Rectangle();
        com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.calculateScissors(viewport.getCamera(),
                batch.getTransformMatrix(), clipBounds, scissors);
        boolean scissorPushed = com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.pushScissors(scissors);

        if (scissorPushed) {
            
            font.getData().setScale(1.0f);
            for (int i = 0; i < CREDIT_LINES.length; i++) {
                float docY = topSpace + i * lineSpacing;
                float drawY = (clipY + clipH) - (docY - creditScrollY);

                if (i == 0) {
                    
                    float titleImgW = 220f;
                    float titleImgH = titleImgW * titleVnTex.getHeight() / titleVnTex.getWidth();
                    float imgX = clipX + (clipW - titleImgW) / 2f;
                    float imgY = drawY - titleImgH + 20f; 
                    batch.draw(titleVnTex, imgX, imgY, titleImgW, titleImgH);
                } else if (!CREDIT_LINES[i].isEmpty()) {
                    
                    font.draw(batch, CREDIT_LINES[i], clipX, drawY + 12f, clipW, Align.center, false);
                }
            }

            
            float imgDocY = topSpace + textH + gap;
            float imgDrawY = (clipY + clipH) - (imgDocY - creditScrollY) - creEndH;
            float imgDrawX = clipX + (clipW - creEndW) / 2f;
            batch.draw(creEndTex, imgDrawX, imgDrawY, creEndW, creEndH);

            batch.flush();
            com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.popScissors();
        }

        
        btnPressedPatch.draw(batch, trackX, trackY, trackW, trackH);

        
        float scrollRatio = maxScrollY > 0f ? creditScrollY / maxScrollY : 0f;
        float handleY = (trackY + trackH - handleH) - scrollRatio * handleRange;
        btnPatch.draw(batch, trackX - 2f, handleY, trackW + 4f, handleH);

        
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
        
        batch.draw(bgSubScreen, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);

        
        float panelW = 750f;
        float panelH = 450f;
        float panelX = (Constants.VIRTUAL_WIDTH - panelW) / 2f;
        float panelY = (Constants.VIRTUAL_HEIGHT - panelH) / 2f;
        timeframePatch.draw(batch, panelX, panelY, panelW, panelH);

        
        titleFont.setColor(Color.YELLOW);
        titleFont.draw(batch, "HƯỚNG DẪN CHƠI", panelX, panelY + panelH - 50f, panelW, Align.center, false);
        titleFont.setColor(Color.WHITE);

        
        font.getData().setScale(1.1f);
        String guideText = "\n" +
                "- Di chuyển: Sử dụng các phím WASD hoặc Mũi Tên\n" +
                "- Tương tác (Nhiệm vụ/Minigame): Nhấn phím SPACE\n" +
                "- Tạm dừng trò chơi: Nhấn phím ESC\n\n" +
                
                "Hãy hoàn thành các minigame và thử thách sinh tồn để mở khóa đầy đủ 7 kết cục (Ending) của trò chơi!";
        font.draw(batch, guideText, panelX + 50f, panelY + panelH - 120f, panelW - 100f, Align.left, true);
        font.getData().setScale(1.0f);

        
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
        
        batch.draw(bgSubScreen, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);

        
        float panelW = 1000f;
        float panelH = 550f;
        float panelX = (Constants.VIRTUAL_WIDTH - panelW) / 2f;
        float panelY = (Constants.VIRTUAL_HEIGHT - panelH) / 2f;
        timeframePatch.draw(batch, panelX, panelY, panelW, panelH);

        
        float titleImgW = tittleTex.getWidth();
        float titleImgH = tittleTex.getHeight();
        float titleScale = Math.min(panelW * 0.6f / titleImgW, 75f / titleImgH);
        float drawTitleW = titleImgW * titleScale;
        float drawTitleH = titleImgH * titleScale;
        float drawTitleX = panelX + (panelW - drawTitleW) / 2f;
        float drawTitleY = panelY + panelH - drawTitleH - 22f;
        batch.draw(tittleTex, drawTitleX, drawTitleY, drawTitleW, drawTitleH);

        
        float padLeft = 50f;
        float padRight = 50f;
        float padBottom = 75f; 
        float contentW = panelW - padLeft - padRight; 
        float contentH = 370f; 
        float gap = 25f;
        float usableW = contentW - gap; 

        
        float boardW = 650f;
        float decorW = 225f;

        float boardX = panelX + padLeft;
        float boardY = panelY + padBottom;
        float decorX = boardX + boardW + gap;
        float decorY = panelY + padBottom;

        
        boardPatch.draw(batch, boardX, boardY, boardW, contentH);

        
        float decorImgW = catDecorTex.getWidth();
        float decorImgH = catDecorTex.getHeight();
        float decorScale = Math.min(decorW / decorImgW, contentH / decorImgH);
        float drawDecorW = decorImgW * decorScale;
        float drawDecorH = decorImgH * decorScale;
        float drawDecorX = decorX + (decorW - drawDecorW) / 2f;
        float drawDecorY = decorY + (contentH - drawDecorH) / 2f;
        batch.draw(catDecorTex, drawDecorX, drawDecorY, drawDecorW, drawDecorH);

        
        float boardPadLeft = 40f;
        float boardPadRight = 40f;
        float boardPadTop = 25f;
        float headerY = boardY + contentH - boardPadTop - 15f; 

        font.getData().setScale(0.85f); 
        font.setColor(new Color(0.85f, 0.65f, 0f, 1f)); 
        drawBoldText(batch, "STT", boardX + boardPadLeft, headerY);
        drawBoldText(batch, "Tên Kết Cục", boardX + boardPadLeft + 60f, headerY);
        drawBoldText(batch, "Trạng Thái", boardX + boardW - boardPadRight - 90f, headerY); 
                                                                                           
        font.setColor(Color.WHITE);

        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("CatLife_Endings");

        
        
        for (int i = 0; i < 7; i++) {
            float y = boardY + contentH - boardPadTop - 62f - i * 38f;
            String endingName = hust.hedspi.oop.game.managers.SaveManager.OFFICIAL_ENDINGS[i];
            boolean unlocked = prefs.getBoolean(endingName, false);

            
            drawBoldText(batch, String.valueOf(i + 1), boardX + boardPadLeft, y + 20f);

            
            if (unlocked) {
                font.setColor(Color.WHITE);
                drawBoldText(batch, endingName, boardX + boardPadLeft + 60f, y + 20f);
            } else {
                
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

            
            float boxSize = 26f;
            float boxX = boardX + boardW - boardPadRight - 60f;
            float boxY = y + 2f;

            if (unlocked) {
                
                btnPatch.draw(batch, boxX, boxY, boxSize, boxSize);
                font.setColor(Color.YELLOW);
                drawBoldText(batch, "X", boxX, boxY + boxSize / 2f + 7f, boxSize, Align.center, false);
                font.setColor(Color.WHITE);
            } else {
                
                btnPressedPatch.draw(batch, boxX, boxY, boxSize, boxSize);
            }
        }
        font.getData().setScale(1.0f);

        
        
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

    private void drawBoldText(SpriteBatch batch, String text, float x, float y, float targetWidth, int align,
            boolean wrap) {
        font.draw(batch, text, x, y, targetWidth, align, wrap);
        font.draw(batch, text, x + 0.6f, y, targetWidth, align, wrap);
    }

    




    private boolean handleButton(SpriteBatch batch, Texture tex, float x, float y, float w, float h, float mouseX,
            float mouseY, boolean enabled) {
        boolean hovered = enabled && (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h);
        boolean pressed = hovered && Gdx.input.isTouched();

        if (!enabled) {
            batch.setColor(0.5f, 0.5f, 0.5f, 0.5f); 
        } else if (pressed) {
            batch.setColor(pressedColor); 
        } else if (hovered) {
            batch.setColor(hoverColor); 
        } else {
            batch.setColor(Color.WHITE); 
        }

        batch.draw(tex, x, y, w, h);
        batch.setColor(Color.WHITE); 

        return hovered && Gdx.input.justTouched();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

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
        titleVnTex.dispose();

        btnTex.dispose();
        btnPressedTex.dispose();
    }
}
