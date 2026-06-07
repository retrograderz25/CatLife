package hust.hedspi.oop.game.utils;

import hust.hedspi.oop.game.managers.TimeManager;
import java.util.List;
import java.util.Arrays;

public class TimeCondition {
    private int startHour;
    private int endHour;
    private List<DayOfWeek> validDays;

    





    public TimeCondition(int startHour, int endHour, DayOfWeek... validDays) {
        this.startHour = startHour;
        this.endHour = endHour;
        if (validDays != null && validDays.length > 0) {
            this.validDays = Arrays.asList(validDays);
        } else {
            this.validDays = Arrays.asList(DayOfWeek.values()); 
        }
    }

    public boolean isCurrentlyValid() {
        TimeManager timeManager = TimeManager.getInstance();
        int currentHour = timeManager.getInGameHour();
        DayOfWeek currentDay = timeManager.getCurrentDayOfWeek();

        
        if (!validDays.contains(currentDay)) {
            return false;
        }

        
        if (startHour <= endHour) {
            
            return currentHour >= startHour && currentHour < endHour;
        } else {
            
            return currentHour >= startHour || currentHour < endHour;
        }
    }
}
