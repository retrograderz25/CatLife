package hust.hedspi.oop.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SaveManager {
    private static final String SAVE_FILE = "CatLife_Endings";
    public static final int TOTAL_ENDINGS = 7; // Tổng số ending trong game

    public static final String[] OFFICIAL_ENDINGS = {
        "Thánh Đổ Vỏ",
        "Gia Đình Hạnh Phúc",
        "Mãi Mãi Kiếp Culi",
        "Làm Đại Ca Mèo",
        "Hoàng Thượng Có Hoàng Hậu",
        "Hoàng Thượng Thái Giám",
        "Quán Thịt Hổ"
    };
    
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
        // Chỉ đếm các ending nằm trong danh sách chính thức
        for (String key : OFFICIAL_ENDINGS) {
            if (prefs.getBoolean(key, false)) {
                count++;
            }
        }
        return count;
    }
}
