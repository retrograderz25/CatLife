package hust.hedspi.oop.game.utils;

import hust.hedspi.oop.game.managers.TimeManager;
import java.util.List;
import java.util.Arrays;

public class TimeCondition {
    private int startHour;
    private int endHour;
    private List<DayOfWeek> validDays;

    /**
     * Khởi tạo điều kiện thời gian.
     * @param startHour Giờ bắt đầu (0-23)
     * @param endHour Giờ kết thúc (0-23). Nếu chạy qua nửa đêm (vd: 22h -> 2h sáng), logic sẽ xử lý riêng.
     * @param validDays Các ngày trong tuần sự kiện này được phép chạy. Nếu null, chạy mọi ngày.
     */
    public TimeCondition(int startHour, int endHour, DayOfWeek... validDays) {
        this.startHour = startHour;
        this.endHour = endHour;
        if (validDays != null && validDays.length > 0) {
            this.validDays = Arrays.asList(validDays);
        } else {
            this.validDays = Arrays.asList(DayOfWeek.values()); // Mặc định mọi ngày
        }
    }

    public boolean isCurrentlyValid() {
        TimeManager timeManager = TimeManager.getInstance();
        int currentHour = timeManager.getInGameHour();
        DayOfWeek currentDay = timeManager.getCurrentDayOfWeek();

        // 1. Kiểm tra Ngày hợp lệ
        if (!validDays.contains(currentDay)) {
            return false;
        }

        // 2. Kiểm tra Giờ hợp lệ
        if (startHour <= endHour) {
            // Sự kiện trong cùng một ngày (VD: 8h sáng -> 17h chiều)
            return currentHour >= startHour && currentHour < endHour;
        } else {
            // Sự kiện xuyên đêm (VD: 22h đêm -> 4h sáng hôm sau)
            return currentHour >= startHour || currentHour < endHour;
        }
    }
}
