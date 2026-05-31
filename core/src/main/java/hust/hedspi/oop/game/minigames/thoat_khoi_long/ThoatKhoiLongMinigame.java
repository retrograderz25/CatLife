package hust.hedspi.oop.game.minigames.thoat_khoi_long;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.I18NBundle;
import hust.hedspi.oop.game.managers.ResourceManager;
import hust.hedspi.oop.game.managers.StoryManager;
import hust.hedspi.oop.game.minigames.IMinigameStrategy;
import hust.hedspi.oop.game.utils.MinigameID;

import java.util.ArrayList;

/**
 * Minigame "Thoat Khoi Long" (THIEF_ESCAPE_CAGE).
 * 
 * Trò chơi trượt tranh giải đố (Sliding Puzzle) kích thước 4x3.
 * Người chơi có 1 phút (60 giây) để trượt các ô tranh hoàn thành bức ảnh gốc.
 * Điều khiển qua tương tác click chuột hoặc các phím WASD / Mũi tên.
 */
public class ThoatKhoiLongMinigame implements IMinigameStrategy {

    private static final String ASSET_BASE = "minigames/thoat_khoi_long/";
    private static final float GAME_DURATION = 60f; // 1 phút
    private static final int ROWS = 3;
    private static final int COLS = 4;
    private static final int TILE_SIZE = 64; // Kích thước gốc của mỗi mảnh tranh (64x64)

    // Textures
    private Texture bgTexture;
    private Texture decorCloveTexture;
    private Texture decorOmenTexture;
    private Texture puzzleTexture;
    private Texture puzzleFrameTexture;
    private Texture timeFrameTexture;
    private Texture dimTexture;

    // Slices
    private TextureRegion[][] originalRegions;

    // Sliding Puzzle grid state
    private int[][] grid = new int[ROWS][COLS];
    private int emptyRow;
    private int emptyCol;

    // Game states
    private int screenW, screenH;
    private float timer;
    private boolean gameOver;
    private boolean won;
    private boolean exitRequested;

    private BitmapFont hudFont;
    private BitmapFont dialogFont;
    private I18NBundle bundle;

    @Override
    public void start() {
        screenW = hust.hedspi.oop.game.utils.Constants.VIRTUAL_WIDTH;
        screenH = hust.hedspi.oop.game.utils.Constants.VIRTUAL_HEIGHT;

        loadAssets();

        hudFont = ResourceManager.getInstance().hudFont;
        dialogFont = ResourceManager.getInstance().dialogFont;
        bundle = ResourceManager.getInstance().getBundle();

        timer = GAME_DURATION;
        gameOver = false;
        won = false;
        exitRequested = false;

        // Slice texture into grid
        originalRegions = TextureRegion.split(puzzleTexture, TILE_SIZE, TILE_SIZE);

        shufflePuzzle();
    }

    private void loadAssets() {
        bgTexture = new Texture(Gdx.files.internal(ASSET_BASE + "background.png"));
        decorCloveTexture = new Texture(Gdx.files.internal(ASSET_BASE + "decor_clove.png"));
        decorOmenTexture = new Texture(Gdx.files.internal(ASSET_BASE + "decor_omen.png"));
        puzzleTexture = new Texture(Gdx.files.internal(ASSET_BASE + "puzzle.png"));
        puzzleFrameTexture = new Texture(Gdx.files.internal(ASSET_BASE + "puzzle_frame.png"));
        timeFrameTexture = new Texture(Gdx.files.internal(ASSET_BASE + "timeframe.png"));

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        dimTexture = new Texture(pix);
        pix.dispose();
    }

    /**
     * Tráo mảnh tranh bằng cách trượt ngẫu nhiên các mảnh hợp lệ từ trạng thái chiến thắng.
     * Cách này đảm bảo 100% câu đố tạo ra có thể giải được.
     */
    private void shufflePuzzle() {
        // Khởi tạo trạng thái ban đầu hoàn hảo
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = r * COLS + c;
            }
        }
        emptyRow = ROWS - 1; // 2
        emptyCol = COLS - 1; // 3

        // Thực hiện 150 bước trượt ngẫu nhiên
        int moves = 0;
        while (moves < 150) {
            ArrayList<int[]> validNeighbors = new ArrayList<>();
            if (emptyRow > 0) validNeighbors.add(new int[]{emptyRow - 1, emptyCol}); // Trên
            if (emptyRow < ROWS - 1) validNeighbors.add(new int[]{emptyRow + 1, emptyCol}); // Dưới
            if (emptyCol > 0) validNeighbors.add(new int[]{emptyRow, emptyCol - 1}); // Trái
            if (emptyCol < COLS - 1) validNeighbors.add(new int[]{emptyRow, emptyCol + 1}); // Phải

            // Chọn lân cận ngẫu nhiên
            int[] chosen = validNeighbors.get(MathUtils.random(0, validNeighbors.size() - 1));
            int nr = chosen[0];
            int nc = chosen[1];

            // Tráo
            grid[emptyRow][emptyCol] = grid[nr][nc];
            grid[nr][nc] = ROWS * COLS - 1; // Gán ô trống chỉ số 11

            emptyRow = nr;
            emptyCol = nc;
            moves++;
        }
    }

    @Override
    public void update(float dt) {
        if (gameOver) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                exitRequested = true;
            }
            return;
        }

        // Đếm ngược thời gian
        timer -= dt;
        if (timer <= 0f) {
            timer = 0f;
            gameOver = true;
            won = false;
            StoryManager.getInstance().recordResult(MinigameID.THIEF_ESCAPE_CAGE, won);
            return;
        }

        // Tương tác Bàn phím
        int targetRow = emptyRow;
        int targetCol = emptyCol;
        boolean keyInput = false;

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            targetCol = emptyCol + 1; // Trượt mảnh bên phải sang trái
            keyInput = true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            targetCol = emptyCol - 1; // Trượt mảnh bên trái sang phải
            keyInput = true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            targetRow = emptyRow + 1; // Trượt mảnh bên dưới lên trên
            keyInput = true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            targetRow = emptyRow - 1; // Trượt mảnh bên trên xuống dưới
            keyInput = true;
        }

        if (keyInput) {
            slideTile(targetRow, targetCol);
        }

        // Tương tác Chuột / Click
        if (Gdx.input.justTouched()) {
            float mx = hust.hedspi.oop.game.screens.MinigameScreen.unproject(Gdx.input.getX(), Gdx.input.getY()).x;
            float my = hust.hedspi.oop.game.screens.MinigameScreen.unproject(Gdx.input.getX(), Gdx.input.getY()).y;

            // Khung đố Puzzle nằm ở: X từ 448 đến 832, Y từ 216 đến 504
            if (mx >= 448f && mx < 832f && my >= 216f && my < 504f) {
                int clickedCol = (int) ((mx - 448f) / 96f);
                int clickedRow = 2 - (int) ((my - 216f) / 96f);

                // Kiểm tra xem có lân cận với ô trống không
                if (Math.abs(clickedRow - emptyRow) + Math.abs(clickedCol - emptyCol) == 1) {
                    slideTile(clickedRow, clickedCol);
                }
            }
        }
    }

    private void slideTile(int nr, int nc) {
        if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS) {
            grid[emptyRow][emptyCol] = grid[nr][nc];
            grid[nr][nc] = ROWS * COLS - 1; // 11
            emptyRow = nr;
            emptyCol = nc;

            checkWinCondition();
        }
    }

    private void checkWinCondition() {
        boolean allCorrect = true;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (grid[r][c] != r * COLS + c) {
                    allCorrect = false;
                    break;
                }
            }
        }

        if (allCorrect) {
            won = true;
            gameOver = true;
            StoryManager.getInstance().recordResult(MinigameID.THIEF_ESCAPE_CAGE, won);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // 1. Vẽ Background
        batch.draw(bgTexture, 0, 0, screenW, screenH);

        // 2. Vẽ hai nhân vật trang trí (Omen & Clove) ở hai góc dưới màn hình (đã đổi vị trí và thu nhỏ thêm)
        // Scale 0.15x
        float omenW = 121f;
        float omenH = 130f;
        batch.draw(decorOmenTexture, 10f, 0f, omenW, omenH);

        float cloveW = 103f;
        float cloveH = 131f;
        batch.draw(decorCloveTexture, screenW - cloveW - 10f, 0f, cloveW, cloveH);

        // 3. Vẽ Khung giải đố puzzle_frame.png ở trung tâm màn hình (Scale 1.5x)
        float frameW = 396f;
        float frameH = 300f;
        float frameX = (screenW - frameW) / 2f; // 442
        float frameY = (screenH - frameH) / 2f; // 210
        batch.draw(puzzleFrameTexture, frameX, frameY, frameW, frameH);

        // 4. Vẽ các mảnh tranh xếp lưới 4x3 (Scale mảnh ghép 1.5x -> 96x96)
        float puzzleX = frameX + 6f; // 448
        float puzzleY = frameY + 6f; // 216
        float displayTileSize = 96f;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int val = grid[r][c];
                if (val == ROWS * COLS - 1) {
                    continue; // Bỏ qua không vẽ ô trống
                }

                int origRow = val / COLS;
                int origCol = val % COLS;
                TextureRegion region = originalRegions[origRow][origCol];

                float drawX = puzzleX + c * displayTileSize;
                float drawY = puzzleY + (2 - r) * displayTileSize;

                // Vẽ mảnh tranh với một viền mờ tối nhỏ (2px gap) để nhìn rõ ranh giới các mảnh
                batch.draw(region, drawX + 1f, drawY + 1f, displayTileSize - 2f, displayTileSize - 2f);
            }
        }

        // 5. Vẽ ô chứa thời gian ở góc trên bên phải (Scale 2.5x)
        float tfScale = 2.5f;
        float tfW = timeFrameTexture.getWidth() * tfScale;
        float tfH = timeFrameTexture.getHeight() * tfScale;
        float tfX = screenW - tfW - 30f;
        float tfY = screenH - tfH - 30f;
        batch.draw(timeFrameTexture, tfX, tfY, tfW, tfH);

        hudFont.setColor(timer < 10f ? Color.RED : Color.WHITE);
        hudFont.getData().setScale(0.8f); // Thu nhỏ chữ thời gian để vừa khung nhỏ
        String timeStr = String.format("%.1f", timer) + bundle.get("cage_time_unit");
        hudFont.draw(batch, timeStr, tfX + 10f, tfY + tfH * 0.65f);
        hudFont.getData().setScale(1.0f); // Reset scale
        hudFont.setColor(Color.WHITE);

        // 6. Vẽ thông tin HUD góc trên bên trái
        hudFont.setColor(Color.YELLOW);
        hudFont.draw(batch, "THOÁT KHỎI LỒNG", 30f, screenH - 30f);

        // Hướng dẫn ở góc dưới chính giữa (thu nhỏ chữ và đã được tách 2 dòng từ file I18N)
        dialogFont.setColor(Color.LIGHT_GRAY);
        dialogFont.getData().setScale(0.72f); // Thu nhỏ font chữ hướng dẫn
        dialogFont.draw(batch, bundle.get("cage_ctrl_hint"), 0f, 65f, (float)screenW, com.badlogic.gdx.utils.Align.center, false);
        dialogFont.getData().setScale(1.0f); // Reset scale
        dialogFont.setColor(Color.WHITE);

        // 7. Vẽ Màn hình Kết thúc Game Over
        if (gameOver) {
            renderGameOver(batch);
        }
    }

    private void renderGameOver(SpriteBatch batch) {
        // Làm tối màn hình
        batch.setColor(0f, 0f, 0f, 0.72f);
        batch.draw(dimTexture, 0, 0, screenW, screenH);
        batch.setColor(Color.WHITE);

        float panelW = screenW * 0.45f;
        float panelH = screenH * 0.45f;
        float panelX = (screenW - panelW) / 2f;
        float panelY = (screenH - panelH) / 2f;

        batch.draw(timeFrameTexture, panelX, panelY, panelW, panelH);

        hudFont.setColor(won ? Color.GREEN : Color.RED);
        String resultText = bundle.get(won ? "cage_win" : "cage_lose");
        hudFont.draw(batch, resultText, panelX + 30f, panelY + panelH * 0.8f);

        hudFont.setColor(Color.WHITE);
        dialogFont.draw(batch, bundle.get("cage_exit_hint"), panelX + 30f, panelY + panelH * 0.45f);
    }

    @Override
    public boolean isFinished() {
        return exitRequested;
    }

    @Override
    public boolean isWon() {
        return won;
    }

    @Override
    public void dispose() {
        safeDispose(bgTexture);
        safeDispose(decorCloveTexture);
        safeDispose(decorOmenTexture);
        safeDispose(puzzleTexture);
        safeDispose(puzzleFrameTexture);
        safeDispose(timeFrameTexture);
        safeDispose(dimTexture);
    }

    private void safeDispose(Texture t) {
        if (t != null) {
            t.dispose();
        }
    }
}
