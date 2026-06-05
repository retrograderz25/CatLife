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

        // Thưởng / Phạt máu và thể lực khi có kết quả
        Cat player = GameManager.getInstance().getPlayer();
        if (player != null) {
            if (isWin) {
                player.increaseHp(hust.hedspi.oop.game.utils.Constants.MINIGAME_WIN_HP_REWARD);
                player.increaseEnergy(hust.hedspi.oop.game.utils.Constants.MINIGAME_WIN_ENERGY_REWARD);
                player.decreaseHunger(hust.hedspi.oop.game.utils.Constants.MINIGAME_WIN_HUNGER_PENALTY);
            } else {
                player.decreaseHp(hust.hedspi.oop.game.utils.Constants.MINIGAME_LOSE_HP_PENALTY);
                player.decreaseEnergy(hust.hedspi.oop.game.utils.Constants.MINIGAME_LOSE_ENERGY_PENALTY);
                player.decreaseHunger(hust.hedspi.oop.game.utils.Constants.MINIGAME_LOSE_HUNGER_PENALTY);
            }
        }

        // Bắt lỗi Instant Death (Quán thịt hổ)
        if (id == MinigameID.THIEF_ESCAPE_CAGE && playerHistory.get(MinigameID.THIEF_ESCAPE_CAGE) == GameResult.LOSE) {
            triggerInstantGameOver("Quán Thịt Hổ");
        }
    }

    private void triggerInstantGameOver(String reason) {
        System.out.println("INSTANT GAME OVER: " + reason);
        GameManager.getInstance().pauseGame();
        ScreenManager.getInstance().pushScreen(new hust.hedspi.oop.game.screens.EndingScreen(reason));
    }

    public boolean isZoneUnlocked(String zoneName) {
        switch (zoneName) {
            case "Hotel Gate": // LOVE_DETECTIVE
                // Phải thắng HipHop mới mở game Thám tử
                return playerHistory.getOrDefault(MinigameID.LOVE_HIPHOP, GameResult.UNPLAYED) == GameResult.WIN;
            case "Warzone2": // GANG_FIGHT_BOSS
                // Phải thắng Đánh hội đồng 1vn mới được đập Đại ca
                return playerHistory.getOrDefault(MinigameID.GANG_FIGHT_1VN, GameResult.UNPLAYED) == GameResult.WIN;
            case "Bubble": // PET_BATH
            case "Office Gate": // PET_ESCAPE_VET
                // Phải làm nũng thành công (Adopt) mới đi tắm và đi thú y
                return playerHistory.getOrDefault(MinigameID.PET_BEG, GameResult.UNPLAYED) == GameResult.WIN;
            default:
                // Các game mặc định (thoát cống, võ mèo lang thang, adopt, le-nin, caomong, exciter) luôn mở về mặt cốt truyện
                return true; 
        }
    }

    // Chữ ký hàm cũ để tương thích với GameManager hiện tại
    public EndingCondition evaluateFinalEnding(Cat player) {
        for (EndingCondition ending : endingsDatabase) {
            if (ending.isSatisfied(playerHistory)) {
                ScreenManager.getInstance().pushScreen(new hust.hedspi.oop.game.screens.EndingScreen(ending));
                return ending;
            }
        }
        ScreenManager.getInstance().pushScreen(new hust.hedspi.oop.game.screens.EndingScreen((EndingCondition) null));
        return null; 
    }
}
