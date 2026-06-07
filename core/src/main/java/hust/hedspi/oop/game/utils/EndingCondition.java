package hust.hedspi.oop.game.utils;

import hust.hedspi.oop.game.entities.Cat;
import java.util.HashMap;
import java.util.Map;

public class EndingCondition {
    private String endingName;
    private int priority;
    // Lưu trữ danh sách các điều kiện bắt buộc
    private Map<MinigameID, GameResult> requiredResults = new HashMap<>();

    // Private constructor, chỉ khởi tạo qua Builder
    private EndingCondition(String name, int priority) {
        this.endingName = name;
        this.priority = priority;
    }

    public String getEndingName() {
        return endingName;
    }

    public int getPriority() { 
        return this.priority; 
    }

    // Hàm kiểm tra lịch sử người chơi
    public boolean isSatisfied(Map<MinigameID, GameResult> playerHistory) {
        for (Map.Entry<MinigameID, GameResult> condition : requiredResults.entrySet()) {
            if (playerHistory.getOrDefault(condition.getKey(), GameResult.UNPLAYED) != condition.getValue()) {
                return false; // Chỉ cần lệch 1 điều kiện là trượt Ending này
            }
        }
        return true;
    }

    // Builder Class lồng bên trong
    public static class Builder {
        private EndingCondition ending;

        public Builder(String name, int priority) {
            ending = new EndingCondition(name, priority);
        }

        // Hàm thêm điều kiện. Trả về chính Builder để chain method
        public Builder require(MinigameID id, GameResult result) {
            ending.requiredResults.put(id, result);
            return this;
        }

        public EndingCondition build() {
            return ending;
        }
    }
}