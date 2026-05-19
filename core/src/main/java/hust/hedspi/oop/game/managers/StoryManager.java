package hust.hedspi.oop.game.managers;

import hust.hedspi.oop.game.utils.EventFlag;
import hust.hedspi.oop.game.utils.EndingCondition;
import hust.hedspi.oop.game.entities.Cat;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class StoryManager {
    private static StoryManager instance;
    private HashMap<EventFlag, Boolean> storyFlags;
    private List<EndingCondition> possibleEndings;

    private StoryManager() {
        storyFlags = new HashMap<>();
        possibleEndings = new ArrayList<>();
        resetStoryFlags();
    }

    public static StoryManager getInstance() {
        if (instance == null) {
            instance = new StoryManager();
        }
        return instance;
    }

    public void resetStoryFlags() {
        storyFlags.clear();
        for (EventFlag flag : EventFlag.values()) {
            storyFlags.put(flag, false);
        }
    }

    public void setFlag(EventFlag flag, boolean value) {
        storyFlags.put(flag, value);
    }

    public boolean getFlag(EventFlag flag) {
        return storyFlags.getOrDefault(flag, false);
    }

    public EndingCondition evaluateFinalEnding(Cat player) {
        // Sắp xếp các kết cục theo thứ tự ưu tiên giảm dần
        possibleEndings.sort((e1, e2) -> Integer.compare(e2.getPriority(), e1.getPriority()));
        
        for (EndingCondition ending : possibleEndings) {
            if (ending.isSatisfied(player)) {
                return ending;
            }
        }
        return null; // Return default ending or null if none satisfied
    }
}
