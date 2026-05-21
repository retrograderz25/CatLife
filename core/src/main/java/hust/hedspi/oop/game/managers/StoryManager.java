package hust.hedspi.oop.game.managers;

import hust.hedspi.oop.game.utils.EndingCondition;
import hust.hedspi.oop.game.utils.GameResult;
import hust.hedspi.oop.game.utils.MinigameID;
import hust.hedspi.oop.game.entities.Cat;
import hust.hedspi.oop.game.utils.GameState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class StoryManager {
    private static StoryManager instance;
    
    private Map<MinigameID, GameResult> playerHistory;
    private List<EndingCondition> endingsDatabase;

    private StoryManager() {
        playerHistory = new HashMap<>();
        endingsDatabase = new ArrayList<>();
        initHistory();
        buildEndingsConfig();
    }

    public static StoryManager getInstance() {
        if (instance == null) {
            instance = new StoryManager();
        }
        return instance;
    }

    public void initHistory() {
        playerHistory.clear();
        for (MinigameID id : MinigameID.values()) {
            playerHistory.put(id, GameResult.UNPLAYED);
        }
    }

    public void resetStoryFlags() {
        initHistory();
    }

    private void buildEndingsConfig() {
        endingsDatabase.clear();

        // 1. Nhánh Tình Yêu & Lang Thang
        // Thánh Đổ Vỏ
        endingsDatabase.add(new EndingCondition.Builder("Thánh Đổ Vỏ", 50)
            .require(MinigameID.LOVE_HIPHOP, GameResult.WIN)
            // .require(MinigameID.LOVE_MASSAGE, GameResult.WIN) <-- (o) Đang tạm khóa
            .require(MinigameID.LOVE_DETECTIVE, GameResult.LOSE)
            .build());

        // Gia Đình Hạnh Phúc
        endingsDatabase.add(new EndingCondition.Builder("Gia Đình Hạnh Phúc", 60)
            .require(MinigameID.DAILY_ESCAPE_SEWER, GameResult.WIN)
            .require(MinigameID.DAILY_FIGHT_STRAY, GameResult.WIN)
            .require(MinigameID.LOVE_HIPHOP, GameResult.WIN)
            // .require(MinigameID.LOVE_MASSAGE, GameResult.WIN) <-- (o) Đang tạm khóa
            .require(MinigameID.LOVE_DETECTIVE, GameResult.WIN)
            .build());

        // 2. Nhánh Giang Hồ (Gang)
        // Kiếp Culi
        endingsDatabase.add(new EndingCondition.Builder("Mãi Mãi Kiếp Culi", 40)
            .require(MinigameID.DAILY_ESCAPE_SEWER, GameResult.WIN)
            .require(MinigameID.DAILY_FIGHT_STRAY, GameResult.WIN)
            .require(MinigameID.GANG_FIGHT_1VN, GameResult.WIN)
            // .require(MinigameID.GANG_MASSAGE_BOSS, GameResult.WIN) <-- (o) Đang tạm khóa
            .require(MinigameID.GANG_FIGHT_BOSS, GameResult.LOSE)
            .build());

        // Làm Đại Ca Mèo
        endingsDatabase.add(new EndingCondition.Builder("Làm Đại Ca Mèo", 70)
            .require(MinigameID.DAILY_ESCAPE_SEWER, GameResult.WIN)
            .require(MinigameID.DAILY_FIGHT_STRAY, GameResult.WIN)
            .require(MinigameID.GANG_FIGHT_1VN, GameResult.WIN)
            // .require(MinigameID.GANG_MASSAGE_BOSS, GameResult.WIN) <-- (o) Đang tạm khóa
            .require(MinigameID.GANG_FIGHT_BOSS, GameResult.WIN)
            .build());

        // 3. Nhánh Nuôi Nhốt (Pet)
        // Hoàng Thượng Có Hoàng Hậu
        endingsDatabase.add(new EndingCondition.Builder("Hoàng Thượng Có Hoàng Hậu", 80)
            .require(MinigameID.LOVE_HIPHOP, GameResult.WIN)
            .require(MinigameID.LOVE_DETECTIVE, GameResult.WIN)
            .require(MinigameID.PET_BEG, GameResult.WIN)
            .require(MinigameID.PET_BATH, GameResult.WIN)
            .require(MinigameID.PET_ESCAPE_VET, GameResult.WIN)
            .build());

        // Hoàng Thượng Thái Giám
        endingsDatabase.add(new EndingCondition.Builder("Hoàng Thượng Thái Giám", 30)
            .require(MinigameID.DAILY_SCRATCH, GameResult.WIN)
            .require(MinigameID.PET_BEG, GameResult.WIN)
            .require(MinigameID.PET_BATH, GameResult.WIN)
            .require(MinigameID.PET_ESCAPE_VET, GameResult.LOSE)
            .build());

        // Sắp xếp database theo Priority (Độ ưu tiên cao xét trước)
        endingsDatabase.sort((e1, e2) -> Integer.compare(e2.getPriority(), e1.getPriority()));
    }

    // Hàm gọi khi hoàn thành 1 Minigame
    public void recordResult(MinigameID id, boolean isWin) {
        playerHistory.put(id, isWin ? GameResult.WIN : GameResult.LOSE);

        // Bắt lỗi Instant Death (Quán thịt hổ)
        if (id == MinigameID.THIEF_ESCAPE_CAGE && playerHistory.get(MinigameID.THIEF_ESCAPE_CAGE) == GameResult.LOSE) {
            triggerInstantGameOver("Quán Thịt Hổ");
        }
    }

    private void triggerInstantGameOver(String reason) {
        System.out.println("INSTANT GAME OVER: " + reason);
        GameManager.getInstance().pauseGame();
        // Set state directly or handle it
    }

    public boolean isMinigameUnlocked(MinigameID requestedGame) {
        switch (requestedGame) {
            case LOVE_DETECTIVE:
                // Phải thắng HipHop mới mở game Thám tử
                return playerHistory.get(MinigameID.LOVE_HIPHOP) == GameResult.WIN;
            case GANG_FIGHT_BOSS:
                // Phải thắng Đánh hội đồng 1vn mới được đập Đại ca
                return playerHistory.get(MinigameID.GANG_FIGHT_1VN) == GameResult.WIN;
            case PET_BATH:
            case PET_ESCAPE_VET:
                // Phải làm nũng thành công mới đi tắm và đi thú y
                return playerHistory.get(MinigameID.PET_BEG) == GameResult.WIN;
            default:
                return true; // Các game mặc định (thoát cống, võ mèo lang thang...) luôn mở
        }
    }

    // Chữ ký hàm cũ để tương thích với GameManager hiện tại
    public EndingCondition evaluateFinalEnding(Cat player) {
        for (EndingCondition ending : endingsDatabase) {
            if (ending.isSatisfied(playerHistory)) {
                System.out.println("ACHIEVED ENDING: " + ending.getEndingName());
                return ending;
            }
        }
        System.out.println("ACHIEVED ENDING: Sống sót ngoài đường (Default)");
        return null; 
    }
}
