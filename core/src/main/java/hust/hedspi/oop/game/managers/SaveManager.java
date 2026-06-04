package hust.hedspi.oop.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SaveManager {
    private static final String SAVE_FILE = "CatLife_Endings";
    public static final int TOTAL_ENDINGS = 7; // Tổng số ending trong game
    
    // Lưu một ending vừa đạt được
    public static void unlockEnding(String endingKey) {
        Preferences prefs = Gdx.app.getPreferences(SAVE_FILE);
        prefs.putBoolean(endingKey, true);
        prefs.flush(); // Ghi xuống ổ cứng
    }
    
    // Đếm số lượng ending đã mở khóa
    public static int getUnlockedEndingsCount() {
        Preferences prefs = Gdx.app.getPreferences(SAVE_FILE);
        int count = 0;
        // Kiểm tra tất cả các key ending
        for (String key : prefs.get().keySet()) {
            if (prefs.getBoolean(key, false)) {
                count++;
            }
        }
        return count;
    }
}
