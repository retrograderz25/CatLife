package hust.hedspi.oop.game.managers;

import hust.hedspi.oop.game.utils.EventFlag;

import java.util.HashMap;
import java.util.Map;

public class StoryManager {
    private static StoryManager instance;
    private Map<EventFlag, Boolean> eventFlags;

    private StoryManager() {
        eventFlags = new HashMap<>();
        reset();
    }

    public static StoryManager getInstance() {
        if (instance == null) {
            instance = new StoryManager();
        }
        return instance;
    }

    public void reset() {
        eventFlags.clear();
        for (EventFlag flag : EventFlag.values()) {
            eventFlags.put(flag, false);
        }
    }

    public void setFlag(EventFlag flag, boolean value) {
        eventFlags.put(flag, value);
    }

    public boolean getFlag(EventFlag flag) {
        return eventFlags.getOrDefault(flag, false);
    }
}
