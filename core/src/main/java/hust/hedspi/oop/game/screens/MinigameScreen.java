package hust.hedspi.oop.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import hust.hedspi.oop.game.minigames.IMinigameStrategy;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.managers.ScreenManager;
import hust.hedspi.oop.game.managers.GameManager;
import hust.hedspi.oop.game.managers.SoundManager;
import hust.hedspi.oop.game.utils.Constants;
import hust.hedspi.oop.game.debug.DebugMenu;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.Input;

public class MinigameScreen implements Screen {
    private IMinigameStrategy strategy;
    private SpriteBatch batch;
    private boolean shouldExit = false;

    public static Viewport viewport;
    private OrthographicCamera camera;

    // --- PAUSE MENU ---
    private boolean isPaused = false;
    private Texture dimTexture;
    private BitmapFont font;
    private int selectedOption = 0; // 0: Resume, 1: Exit
    
    // --- DEBUG MENU ---
    private Stage debugStage;
    private DebugMenu debugMenu;

    // --- TUTORIAL / INSTRUCTIONS POPUP ---
    private boolean showTutorial = true;
    private Texture tfTex;
    private Texture btnTex;
    private Texture btnPressedTex;
    private NinePatch panelPatch;
    private NinePatch btnPatch;
    private NinePatch btnPressedPatch;

    public static NinePatch staticPanelPatch;
    public static NinePatch staticBtnPatch;
    public static NinePatch staticBtnPressedPatch;

    public MinigameScreen(IMinigameStrategy strategy) {
        this.strategy = strategy;
        this.batch = new SpriteBatch();

        this.camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT, camera);
        viewport.apply();

        // Đổi trạng thái game tổng để tạm dừng Map chính
        GameManager.getInstance().pauseGame();

        // Khởi tạo tài nguyên cho Pause Menu
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        dimTexture = new Texture(pix);
        pix.dispose();

        font = ResourceManager.getInstance().dialogFont;
        
        // Khởi tạo tài nguyên cho Tutorial Popup
        tfTex = new Texture(Gdx.files.internal("images/HUD/ui/panel/timeframe.png"));
        btnTex = new Texture(Gdx.files.internal("images/HUD/ui/button/button_blue.png"));
        btnPressedTex = new Texture(Gdx.files.internal("images/HUD/ui/button/button_blue_pressed.png"));

        panelPatch = new NinePatch(tfTex, 8, 8, 8, 8);
        btnPatch = new NinePatch(btnTex, 4, 4, 4, 4);
        btnPressedPatch = new NinePatch(btnPressedTex, 4, 4, 4, 4);

        staticPanelPatch = panelPatch;
        staticBtnPatch = btnPatch;
        staticBtnPressedPatch = btnPressedPatch;

        // Khởi tạo Debug Menu
        debugStage = new Stage(viewport, batch);
        debugMenu = new DebugMenu();
        debugStage.addActor(debugMenu.getTable());
    }

    public void forceEnd(boolean win) {
        if (strategy != null) {
            strategy.forceEnd(win);
        }
    }

    public static Vector2 unproject(int screenX, int screenY) {
        if (viewport == null) return new Vector2(screenX, screenY);
        Vector3 unprojected = viewport.unproject(new Vector3(screenX, screenY, 0));
        return new Vector2(unprojected.x, unprojected.y);
    }

    @Override
    public void show() {
        strategy.start();
        Gdx.input.setInputProcessor(debugStage);
        SoundManager.getInstance().playBGM(getBGMForStrategy(strategy));
    }

    private String getBGMForStrategy(IMinigameStrategy s) {
        if (s == null) return SoundManager.BGM_MG_FUNNY1;
        switch (s.getClass().getSimpleName()) {
            // Hài hước / Lãng mạn
            case "CaoMongMinigame":      return SoundManager.BGM_MG_FUNNY1;
            case "PetBegMinigame":       return SoundManager.BGM_MG_FUNNY1;
            case "BathGameMinigame":     return SoundManager.BGM_MG_FUNNY2;
            case "NhayHipHopMinigame":   return SoundManager.BGM_MG_HIPHOP;
            case "TimTieuTamMinigame":   return SoundManager.BGM_MG_FUNNY3;
            case "RhythmMinigame":       return SoundManager.BGM_MG_FUNNY3;
            // Sinh tồn / Trốn chạy
            case "ThoatKhoiCongMinigame": return SoundManager.BGM_MG_ESCAPE1;
            case "TromMeoMinigame":       return SoundManager.BGM_MG_ESCAPE2;
            case "ThoatKhoiLongMinigame": return SoundManager.BGM_MG_ESCAPE3;
            case "TronKimTiemMinigame":   return SoundManager.BGM_MG_ESCAPE3;
            // Đánh nhau / Băng đảng
            case "CombatDonMinigame":    return SoundManager.BGM_MG_FIGHT2;
            case "CombatMinigame":       return SoundManager.BGM_MG_BOSS;
            default:                     return SoundManager.BGM_MG_FUNNY1;
        }
    }

    @Override
    public void render(float delta) {
        if (shouldExit || strategy.isFinished()) {
            ScreenManager.getInstance().popScreen();
            
            // Xử lý chuỗi sự kiện (Chain Minigames)
            if (strategy instanceof hust.hedspi.oop.game.minigames.trom_meo.TromMeoMinigame && !strategy.isWon()) {
                // Thua Trốn Trộm Mèo -> Bị bắt nhốt -> Tự động chuyển sang game Thoát Khỏi Lồng
                ScreenManager.getInstance().pushScreen(new MinigameScreen(new hust.hedspi.oop.game.minigames.thoat_khoi_long.ThoatKhoiLongMinigame()));
            } else {
                GameManager.getInstance().resumeGame();
            }
            return;
        }

        // Bật tắt DebugMenu
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.F12)) {
            debugMenu.toggle();
        }

        // Bắt phím ESC để bật/tắt Pause Menu (chỉ khi game chưa kết thúc và debug menu đang đóng)
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE) && !strategy.isFinished() && !debugMenu.isVisible()) {
            isPaused = !isPaused;
            selectedOption = 0; // Mặc định trỏ vào "Tiếp tục"
        }

        if (isPaused) {
            // Điều khiển Menu Pause
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.UP) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.W)) {
                selectedOption = 0;
            } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S)) {
                selectedOption = 1;
            } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
                if (selectedOption == 0) {
                    isPaused = false;
                } else {
                    shouldExit = true;
                }
            }
        } else if (showTutorial) {
            // Handle tutorial input
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
                showTutorial = false;
            } else if (Gdx.input.justTouched()) {
                Vector2 mousePos = unproject(Gdx.input.getX(), Gdx.input.getY());
                float btnW = 180f;
                float btnH = 60f;
                float btnX = (Constants.VIRTUAL_WIDTH - btnW) / 2f;
                float btnY = ((Constants.VIRTUAL_HEIGHT - 450f) / 2f) + 40f;
                if (mousePos.x >= btnX && mousePos.x <= btnX + btnW && mousePos.y >= btnY && mousePos.y <= btnY + btnH) {
                    showTutorial = false;
                }
            }
        } else if (!debugMenu.isVisible()) {
            // Cập nhật logic game nếu không Pause và không mở Debug
            strategy.update(delta);
        }

        ScreenUtils.clear(0f, 0f, 0f, 1);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        
        // Luôn vẽ minigame bên dưới
        strategy.render(batch);
        
        // Vẽ lớp Tutorial đè lên trên minigame
        if (showTutorial) {
            // Dim background
            batch.setColor(0f, 0f, 0f, 0.7f);
            batch.draw(dimTexture, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
            batch.setColor(Color.WHITE);
            
            // Draw timeframe panel (NinePatch)
            float panelW = 750f;
            float panelH = 420f;
            float panelX = (Constants.VIRTUAL_WIDTH - panelW) / 2f;
            float panelY = (Constants.VIRTUAL_HEIGHT - panelH) / 2f;
            panelPatch.draw(batch, panelX, panelY, panelW, panelH);
            
            // Draw title
            String titleText = getMinigameTitle();
            font.getData().setScale(1.8f);
            font.setColor(Color.YELLOW);
            font.draw(batch, titleText, panelX, panelY + panelH - 50f, panelW, Align.center, false);
            
            // Draw instructions text (wrapped)
            String instrText = getMinigameInstruction();
            font.getData().setScale(1.2f);
            font.setColor(Color.WHITE);
            font.draw(batch, instrText, panelX + 50f, panelY + panelH - 120f, panelW - 100f, Align.center, true);
            
            // Draw "Đã hiểu" button
            float btnW = 180f;
            float btnH = 60f;
            float btnX = (Constants.VIRTUAL_WIDTH - btnW) / 2f;
            float btnY = panelY + 40f;
            
            // Check if mouse is hovering/pressing inside button bounds to draw pressed texture
            boolean isHoveredOrPressed = false;
            Vector2 mousePos = unproject(Gdx.input.getX(), Gdx.input.getY());
            if (mousePos.x >= btnX && mousePos.x <= btnX + btnW && mousePos.y >= btnY && mousePos.y <= btnY + btnH) {
                isHoveredOrPressed = true;
            }
            
            if (isHoveredOrPressed && Gdx.input.isTouched()) {
                btnPressedPatch.draw(batch, btnX, btnY, btnW, btnH);
            } else {
                btnPatch.draw(batch, btnX, btnY, btnW, btnH);
            }
            
            // Draw button text
            font.getData().setScale(1.1f);
            font.setColor(isHoveredOrPressed ? Color.YELLOW : Color.WHITE);
            font.draw(batch, "Đã hiểu", btnX, btnY + btnH / 2f + 8f, btnW, Align.center, false);
            
            // Reset scale and color
            font.getData().setScale(1.0f);
            font.setColor(Color.WHITE);
        }
        
        // Vẽ lớp Pause Menu đè lên trên cùng
        if (isPaused) {
            batch.setColor(0f, 0f, 0f, 0.7f);
            batch.draw(dimTexture, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
            batch.setColor(Color.WHITE);
            
            font.getData().setScale(2.0f);
            font.setColor(Color.YELLOW);
            font.draw(batch, "TẠM DỪNG", 0, Constants.VIRTUAL_HEIGHT / 2f + 120, Constants.VIRTUAL_WIDTH, Align.center, false);
            
            font.getData().setScale(1.2f);
            font.setColor(selectedOption == 0 ? Color.YELLOW : Color.WHITE);
            font.draw(batch, selectedOption == 0 ? "> Tiếp Tục <" : "Tiếp Tục", 0, Constants.VIRTUAL_HEIGHT / 2f + 20, Constants.VIRTUAL_WIDTH, Align.center, false);
            
            font.setColor(selectedOption == 1 ? Color.YELLOW : Color.WHITE);
            font.draw(batch, selectedOption == 1 ? "> Thoát về Đường Phố <" : "Thoát về Đường Phố", 0, Constants.VIRTUAL_HEIGHT / 2f - 40, Constants.VIRTUAL_WIDTH, Align.center, false);
            
            font.getData().setScale(1.0f); // Reset scale
            font.setColor(Color.WHITE);
        }

        batch.end();
        
        if (debugMenu.isVisible()) {
            debugStage.act(delta);
            debugStage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
        }
        if (debugStage != null) {
            debugStage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    private String getMinigameTitle() {
        if (strategy == null) return "TRÒ CHƠI PHỤ";
        String className = strategy.getClass().getSimpleName();
        switch (className) {
            case "ThoatKhoiLongMinigame": return "THOÁT KHỎI LỒNG";
            case "ThoatKhoiCongMinigame": return "TRỐN THOÁT KHỎI CỐNG NGẦM";
            case "BathGameMinigame": return "TẮM RỬA SẠCH SẼ";
            case "CaoMongMinigame": return "THỬ THÁCH CÀO MÓNG";
            case "CombatMinigame": return "ĐÁNH HỘI ĐỒNG";
            case "CombatDonMinigame": return "THÁCH ĐẤU 1VS1";
            case "NhayHipHopMinigame": return "BATTLE DANCE HIPHOP";
            case "TromMeoMinigame": return "NÉ TRÁNH KẺ TRỘM MÈO";
            case "TronKimTiemMinigame": return "TRỐN KHỎI KIM TIÊM";
            case "TimTieuTamMinigame": return "BẮT GIAN TIỂU TAM";
            case "RhythmMinigame": return "THỬ THÁCH NHỊP ĐIỆU";
            case "PetBegMinigame": return "NHẬN NUÔI";
            default: return "HƯỚNG DẪN CHƠI";
        }
    }

    private String getMinigameInstruction() {
        if (strategy == null) return "";
        String className = strategy.getClass().getSimpleName();
        String key;
        switch (className) {
            case "ThoatKhoiLongMinigame": key = "cage_ctrl_hint"; break;
            case "ThoatKhoiCongMinigame": key = "thoat_cong_ctrl_hint"; break;
            case "BathGameMinigame": key = "bath_ctrl_hint"; break;
            case "CaoMongMinigame": key = "cao_mong_ctrl_hint"; break;
            case "CombatMinigame": key = "combat_ctrl_hint"; break;
            case "CombatDonMinigame": key = "combat_don_ctrl_hint"; break;
            case "NhayHipHopMinigame": key = "hiphop_ctrl_hint"; break;
            case "TromMeoMinigame": key = "trom_meo_ctrl_hint"; break;
            case "TronKimTiemMinigame": key = "tron_kim_tiem_ctrl_hint"; break;
            case "TimTieuTamMinigame": key = "ttt_ctrl_hint"; break;
            case "RhythmMinigame": key = "rhythm_ctrl_hint"; break;
            case "PetBegMinigame": key = "pet_beg_ctrl_hint"; break;
            default: return "Nhấn nút để bắt đầu trò chơi!";
        }
        try {
            return ResourceManager.getInstance().getBundle().get(key);
        } catch (Exception e) {
            return "Nhấn nút để bắt đầu trò chơi!";
        }
    }

    @Override
    public void dispose() {
        SoundManager.getInstance().stopBGM();
        batch.dispose();
        if (dimTexture != null) {
            dimTexture.dispose();
        }
        if (tfTex != null) tfTex.dispose();
        if (btnTex != null) btnTex.dispose();
        if (btnPressedTex != null) btnPressedTex.dispose();
        if (debugStage != null) {
            debugStage.dispose();
        }
        if (debugMenu != null) {
            debugMenu.dispose();
        }
        strategy.dispose();
        viewport = null;
    }
}
