package hust.hedspi.oop.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SaveManager {
    private static final String SAVE_FILE = "CatLife_Endings";
    public static final int TOTAL_ENDINGS = 7; 

    public static final String[] OFFICIAL_ENDINGS = {
        "Thánh Đổ Vỏ",
        "Gia Đình Hạnh Phúc",
        "Mãi Mãi Kiếp Culi",
        "Làm Đại Ca Mèo",
        "Hoàng Thượng Có Hoàng Hậu",
        "Hoàng Thượng Thái Giám",
        "Quán Thịt Hổ"
    };
    
    
    public static void unlockEnding(String endingKey) {
        Preferences prefs = Gdx.app.getPreferences(SAVE_FILE);
        prefs.putBoolean(endingKey, true);
        prefs.flush(); 
    }
    
    public static boolean isEndingUnlocked(String endingKey) {
        Preferences prefs = Gdx.app.getPreferences(SAVE_FILE);
        return prefs.getBoolean(endingKey, false);
    }

    
    public static int getUnlockedEndingsCount() {
        Preferences prefs = Gdx.app.getPreferences(SAVE_FILE);
        int count = 0;
        
        for (String key : OFFICIAL_ENDINGS) {
            if (prefs.getBoolean(key, false)) {
                count++;
            }
        }
        return count;
    }

    public static void clearAllSaves() {
        Preferences prefs = Gdx.app.getPreferences(SAVE_FILE);
        prefs.clear();
        prefs.flush();
    }
}
