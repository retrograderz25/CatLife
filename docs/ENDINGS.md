Tài liệu này sẽ hướng dẫn cách chuyển đổi ma trận trên thành code OOP sạch sẽ, xử lý thời gian thực, lưu trữ tiến trình (meta-progression) và điều hướng màn hình sau khi đạt Ending.

---

# 📘 ĐẶC TẢ KỸ THUẬT: HỆ THỐNG ENDING & TIME-GATED TRIGGERS
**Dự án:** CatLife 
**Đối tượng đọc:** Development Team (Coders)

## PHẦN 1: CẬP NHẬT TỪ ĐIỂN DỮ LIỆU (ENUMS)
Dựa theo bảng mới, chúng ta loại bỏ vĩnh viễn các game bị gạch xám (Mát xa).

```java
// 1. Danh sách ID của các Minigame chính thức (Đã lược bỏ Mát xa)
public enum MinigameID {
    LOVE_HIPHOP, LOVE_DETECTIVE,
    DAILY_SCRATCH, DAILY_ESCAPE_SEWER, DAILY_FIGHT_STRAY,
    THIEF_HIDE, THIEF_ESCAPE_CAGE,
    GANG_FIGHT_1VN, GANG_FIGHT_BOSS,
    PET_BEG, PET_BATH, PET_ESCAPE_VET
}

// 2. Kết quả của một Minigame
public enum GameResult {
    WIN, LOSE, UNPLAYED
}
```

---

## PHẦN 2: XỬ LÝ ĐIỀU KIỆN THỜI GIAN TRÊN MAIN MAP
Cột "Điều kiện/Thời gian xuất hiện" quy định thời điểm NPC hoặc Trigger Zone cho phép bấm phím `[E]` để vào Minigame.

**Hướng dẫn triển khai cho Dev:**
Sử dụng `TimeManager` để chặn/mở khóa tương tác. Ở class quản lý va chạm hoặc NPC, thêm một hàm kiểm tra thời gian trước khi gọi `GameManager.getInstance().startMinigame(...)`.

```java
public class InteractionHandler {
    
    public void tryInteractWithEvent(String eventType) {
        int currentHour = TimeManager.getInstance().getInGameHour();
        
        switch (eventType) {
            case "GANG_EVENT":
                // Từ 18h trở đi
                if (currentHour >= 18) {
                    startGangMinigameFlow();
                } else {
                    showFloatText("Bọn giang hồ chưa ra mặt giờ này đâu (Cần 18h+)");
                }
                break;
                
            case "PET_EVENT":
                // Từ 17h trở đi
                if (currentHour >= 17) {
                    startPetMinigameFlow();
                } else {
                    showFloatText("Con người đi làm chưa về (Cần 17h+)");
                }
                break;
                
            case "THIEF_EVENT":
                // Từ 20h trở đi
                if (currentHour >= 20 || currentHour < 6) { // Chạy xuyên đêm
                    startThiefMinigameFlow();
                }
                break;
                
            default: // Các game DAILY và LOVE (Cả ngày)
                startDefaultMinigameFlow(eventType);
                break;
        }
    }
}
```

---

## PHẦN 3: TRIỂN KHAI LOGIC ENDINGS BẰNG BUILDER PATTERN
Luật *"Nếu fail game này"* được hiểu là: **Bắt buộc phải THẮNG các game trước đó theo nhánh dọc, và THUA chính game đang được đánh dấu.**

Chúng ta cấu hình `StoryManager` chính xác theo cột dọc của Excel:

```java
public class StoryManager {
    // ... (Các phần khởi tạo như đã thảo luận trước đây)

    private void buildEndingsConfig() {
        // 1. Thánh Đổ Vỏ (Nhánh Love)
        endingsDatabase.add(new EndingCondition.Builder("Thánh Đổ Vỏ", "ending_cuckold")
            .require(MinigameID.LOVE_HIPHOP, GameResult.WIN)
            .require(MinigameID.LOVE_DETECTIVE, GameResult.LOSE) // Cố tình fail để lấy ending này
            .build());

        // 2. Gia Đình Hạnh Phúc
        endingsDatabase.add(new EndingCondition.Builder("Gia Đình Hạnh Phúc", "ending_happy_family")
            .require(MinigameID.DAILY_ESCAPE_SEWER, GameResult.WIN)
            .require(MinigameID.DAILY_FIGHT_STRAY, GameResult.WIN)
            .require(MinigameID.LOVE_HIPHOP, GameResult.WIN)
            .require(MinigameID.LOVE_DETECTIVE, GameResult.WIN)
            .build());

        // 3. Làm Đại Ca Mèo (Nhánh Gang)
        endingsDatabase.add(new EndingCondition.Builder("Làm Đại Ca Mèo", "ending_gang_boss")
            .require(MinigameID.DAILY_ESCAPE_SEWER, GameResult.WIN)
            .require(MinigameID.DAILY_FIGHT_STRAY, GameResult.WIN)
            .require(MinigameID.GANG_FIGHT_1VN, GameResult.WIN)
            .require(MinigameID.GANG_FIGHT_BOSS, GameResult.WIN)
            .build());

        // 4. Mãi Mãi Kiếp Culi
        endingsDatabase.add(new EndingCondition.Builder("Mãi Mãi Kiếp Culi", "ending_minion")
            .require(MinigameID.DAILY_ESCAPE_SEWER, GameResult.WIN)
            .require(MinigameID.DAILY_FIGHT_STRAY, GameResult.WIN)
            .require(MinigameID.GANG_FIGHT_1VN, GameResult.WIN)
            .require(MinigameID.GANG_FIGHT_BOSS, GameResult.LOSE) // Đánh trùm thua sẽ làm culi
            .build());

        // 5. Hoàng Thượng Có Hoàng Hậu (Nhánh Pet + Love)
        endingsDatabase.add(new EndingCondition.Builder("Hoàng Thượng Có Hoàng Hậu", "ending_king_queen")
            .require(MinigameID.LOVE_HIPHOP, GameResult.WIN)
            .require(MinigameID.LOVE_DETECTIVE, GameResult.WIN)
            .require(MinigameID.PET_BEG, GameResult.WIN)
            .require(MinigameID.PET_BATH, GameResult.WIN)
            .require(MinigameID.PET_ESCAPE_VET, GameResult.WIN)
            .build());

        // 6. Hoàng Thượng Thái Giám
        endingsDatabase.add(new EndingCondition.Builder("Hoàng Thượng Thái Giám", "ending_eunuch")
            .require(MinigameID.DAILY_SCRATCH, GameResult.WIN) // Cào móng thắng
            .require(MinigameID.PET_BEG, GameResult.WIN)
            .require(MinigameID.PET_BATH, GameResult.WIN)
            .require(MinigameID.PET_ESCAPE_VET, GameResult.LOSE) // Bị bắt đem thiến
            .build());
    }
}
```

*Đặc biệt: Nhánh "Quán Thịt Hổ" là Instant Death, sẽ được check ngay lập tức ở hàm `recordResult()` chứ không đợi đến cuối game.*
```java
// Gọi khi vừa chơi xong minigame thoát lồng
if (id == MinigameID.THIEF_ESCAPE_CAGE && result == GameResult.LOSE) {
    if (playerHistory.get(MinigameID.THIEF_HIDE) == GameResult.LOSE) {
        GameManager.getInstance().triggerEnding("ending_tiger_meat"); // Quán thịt hổ!
    }
}
```

---

## PHẦN 4: HỆ THỐNG GLOBAL SAVE & HIỂN THỊ TIẾN ĐỘ ENDING (META-PROGRESSION)

Vì người chơi có thể chơi lại nhiều lần để sưu tập Ending, ta KHÔNG THỂ lưu dữ liệu này vào bộ nhớ tạm (RAM) của `GameManager`. Chúng ta phải dùng thư viện **`Preferences`** của libGDX để ghi file vào ổ cứng (Lưu vĩnh viễn).

**1. Class quản lý Save Game (Global Save):**
```java
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
        // Kiểm tra tất cả các key ending (ví dụ: ending_cuckold, ending_minion...)
        for (String key : prefs.get().keySet()) {
            if (prefs.getBoolean(key, false)) {
                count++;
            }
        }
        return count;
    }
}
```

---

## PHẦN 5: LUỒNG ĐIỀU HƯỚNG TẠI MÀN HÌNH ENDING (UI SCREEN)

Khi `GameManager` gọi hàm `triggerEnding(EndingCondition ending)`, chúng ta đẩy (push) `EndingScreen` lên hiển thị.

**Logic trên `EndingScreen`:**
1.  **Ghi nhận Save Game:** Ngay ở hàm khởi tạo screen, gọi `SaveManager.unlockEnding(ending.getKey())`.
2.  **Vẽ UI (Scene2D):**
    *   Hiển thị Tên Ending + Cốt truyện/Hình ảnh tương ứng.
    *   Hiển thị Label ở góc dưới: `"Bạn đã mở khóa: " + SaveManager.getUnlockedEndingsCount() + " / 7 Endings"`.
3.  **Xử lý 2 nút bấm:**

*   **Nút [Bắt Đầu Cuộc Sống Mới]:** Xóa hoàn toàn tiến trình ván này và reset game.
    ```java
    btnNewLife.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            // Reset toàn bộ thông số, máu, lịch sử minigame, đồng hồ
            GameManager.getInstance().startNewGame(); 
            // Chuyển thẳng về Main Map
            ScreenManager.getInstance().clearAndSetScreen(new PlayScreen());
        }
    });
    ```

*   **Nút [Tiếp Tục Khám Phá] (Freeplay/Continue):** 
    Đây là tính năng rất hay. Nó cho phép người chơi quay lại Map với thân phận hiện tại (các Minigame đã chơi sẽ KHÔNG bị reset), để họ có thể đi tìm các NPC khác và bẻ lái sang Ending khác (ví dụ: đang làm Đại ca mèo nhưng tự nhiên đi làm nũng để được nhận nuôi).
    ```java
    btnContinue.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            // KHÔNG reset playerHistory, KHÔNG reset thời gian.
            // Chỉ đóng Màn hình Ending lại, ném mèo trở về bản đồ chính
            ScreenManager.getInstance().popScreen(); 
            GameManager.getInstance().resumeGame();
        }
    });
    ```

**Tóm tắt giao task cho Dev:**
1. Cập nhật `MinigameID` và `EndingCondition` theo đúng code ở Phần 3.
2. Xây dựng class `SaveManager` dùng `Preferences` của libGDX.
3. Code layout cho `EndingScreen` gồm 2 nút bấm và đoạn text đếm `(Unlocked X/7)`.