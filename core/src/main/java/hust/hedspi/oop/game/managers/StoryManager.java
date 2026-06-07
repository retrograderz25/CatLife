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

        
        
        endingsDatabase.add(new EndingCondition.Builder("Thánh Đổ Vỏ", 50)
            .require(MinigameID.LOVE_HIPHOP, GameResult.WIN)
            
            .require(MinigameID.LOVE_DETECTIVE, GameResult.LOSE)
            .build());

        
        endingsDatabase.add(new EndingCondition.Builder("Gia Đình Hạnh Phúc", 60)
            .require(MinigameID.DAILY_ESCAPE_SEWER, GameResult.WIN)
            .require(MinigameID.LOVE_HIPHOP, GameResult.WIN)
            
            .require(MinigameID.LOVE_DETECTIVE, GameResult.WIN)
            .build());

        
        
        endingsDatabase.add(new EndingCondition.Builder("Mãi Mãi Kiếp Culi", 40)
            .require(MinigameID.DAILY_ESCAPE_SEWER, GameResult.WIN)
            .require(MinigameID.GANG_FIGHT_1VN, GameResult.WIN)
            
            .require(MinigameID.GANG_FIGHT_BOSS, GameResult.LOSE)
            .build());

        
        endingsDatabase.add(new EndingCondition.Builder("Làm Đại Ca Mèo", 70)
            .require(MinigameID.DAILY_ESCAPE_SEWER, GameResult.WIN)
            .require(MinigameID.GANG_FIGHT_1VN, GameResult.WIN)
            
            .require(MinigameID.GANG_FIGHT_BOSS, GameResult.WIN)
            .build());

        
        
        endingsDatabase.add(new EndingCondition.Builder("Hoàng Thượng Có Hoàng Hậu", 80)
            .require(MinigameID.LOVE_HIPHOP, GameResult.WIN)
            .require(MinigameID.LOVE_DETECTIVE, GameResult.WIN)
            .require(MinigameID.PET_BEG, GameResult.WIN)
            .require(MinigameID.PET_BATH, GameResult.WIN)
            .require(MinigameID.PET_ESCAPE_VET, GameResult.WIN)
            .build());

        
        endingsDatabase.add(new EndingCondition.Builder("Hoàng Thượng Thái Giám", 30)
            .require(MinigameID.DAILY_SCRATCH, GameResult.WIN)
            .require(MinigameID.PET_BEG, GameResult.WIN)
            .require(MinigameID.PET_BATH, GameResult.WIN)
            .require(MinigameID.PET_ESCAPE_VET, GameResult.LOSE)
            .build());

        
        endingsDatabase.sort((e1, e2) -> Integer.compare(e2.getPriority(), e1.getPriority()));
    }

    
    public void recordResult(MinigameID id, boolean isWin) {
        playerHistory.put(id, isWin ? GameResult.WIN : GameResult.LOSE);

        
        Cat player = GameManager.getInstance().getPlayer();
        if (player != null) {
            if (isWin) {
                player.increaseHp(hust.hedspi.oop.game.utils.Constants.MINIGAME_WIN_HP_REWARD);
            } else {
                player.decreaseHp(hust.hedspi.oop.game.utils.Constants.MINIGAME_LOSE_HP_PENALTY);
            }
        }

        
        if (id == MinigameID.THIEF_ESCAPE_CAGE && playerHistory.get(MinigameID.THIEF_ESCAPE_CAGE) == GameResult.LOSE) {
            triggerInstantGameOver("Quán Thịt Hổ");
            return; 
        }

        
        evaluateFinalEnding(player);
    }

    private void triggerInstantGameOver(String reason) {
        System.out.println("INSTANT GAME OVER: " + reason);
        GameManager.getInstance().pauseGame();
        ScreenManager.getInstance().pushScreen(new hust.hedspi.oop.game.screens.EndingScreen(reason));
    }

    public boolean isZoneUnlocked(String zoneName) {
        switch (zoneName) {
            case "Hotel Gate": 
                
                return playerHistory.getOrDefault(MinigameID.LOVE_HIPHOP, GameResult.UNPLAYED) == GameResult.WIN;
            case "Warzone2": 
                
                return playerHistory.getOrDefault(MinigameID.GANG_FIGHT_1VN, GameResult.UNPLAYED) == GameResult.WIN;
            case "Bubble": 
            case "Office Gate": 
                
                return playerHistory.getOrDefault(MinigameID.PET_BEG, GameResult.UNPLAYED) == GameResult.WIN;
            default:
                
                return true; 
        }
    }

    
    public EndingCondition evaluateFinalEnding(Cat player) {
        // duyệt mấy cái ending từ độ ưu tiên cao xuống thấp, cái nào thoả mãn đầu tiên thì lấy luôn
        for (EndingCondition ending : endingsDatabase) {
            if (ending.isSatisfied(playerHistory)) {
                
                if (SaveManager.isEndingUnlocked(ending.getEndingName())) {
                    continue; 
                }

                GameManager.getInstance().pauseGame();
                ScreenManager.getInstance().pushScreen(new hust.hedspi.oop.game.screens.EndingScreen(ending));
                return ending;
            }
        }
        return null; 
    }
}
