# 📜 HIẾN PHÁP PHÁT TRIỂN DỰ ÁN CATLIFE (AGENT_DEV_GUIDELINES)

> ⚠️ **THÔNG BÁO QUAN TRỌNG TỚI TẤT CẢ AI AGENTS / DEVELOPERS:**
> Bất kỳ ai khi nhận yêu cầu tham gia chỉnh sửa, thêm tính năng, hay bảo trì dự án `CatLife` **BẮT BUỘC** phải đọc và tuân thủ nghiêm ngặt các quy tắc trong tài liệu này trước khi viết bất kỳ dòng code nào.

Dự án này được thiết kế theo tư duy của một hệ thống có khả năng mở rộng (Scalable Architecture) và chống Feature Creep. Sự vô kỷ luật trong việc đẻ thêm file hoặc viết code cứng (Hardcode) là không được phép.

---

## 1. QUY TẮC QUẢN LÝ VĂN BẢN (TEXT, DIALOGUE & QUESTS)
**Kỹ thuật bắt buộc: I18N (Internationalization) / File `.properties`**
Để đáp ứng yêu cầu "tất cả text chỉ chỉnh trong 1 file duy nhất", tuyệt đối **KHÔNG ĐƯỢC** gõ trực tiếp chuỗi String tiếng Việt vào trong file `.java` (ngoại trừ file `Constants.java` chứa bảng mã font).

- **Cách làm:**
  1. Toàn bộ text (Thoại NPC, Tên nhiệm vụ, Mô tả vật phẩm, Nút UI) phải được đặt trong một (hoặc nhiều) file `.properties` (Ví dụ: `assets/i18n/dialogues_vi.properties`).
  2. Sử dụng thư viện `I18NBundle` của libGDX để load file này thông qua `ResourceManager`.
  3. Trong code Java, chỉ được phép gọi theo Key.
- **Ví dụ minh họa:**
  *File `dialogues_vi.properties`:*
  ```properties
  npc_boss_intro=Mày nghĩ mày đủ trình đụng vào tao sao, ranh con?
  quest_sewer_title=Trốn Khỏi Cống Ngầm
  ui_btn_play=Chơi Ngay
  ```
  *File Code `.java`:*
  ```java
  // TUYỆT ĐỐI KHÔNG: label.setText("Chơi Ngay");
  // BẮT BUỘC DÙNG:
  label.setText(ResourceManager.getInstance().getBundle().get("ui_btn_play"));
  ```

---

## 2. QUY TẮC PHÁT TRIỂN MINIGAME
**Kỹ thuật bắt buộc: Strategy Pattern**
Dự án **không** chấp nhận việc mỗi minigame tạo ra một màn hình `Screen` (Ví dụ: `FishingScreen.java`, `RhythmScreen.java` là sai).

- **Cách làm:**
  1. Mọi minigame mới phải tạo class trong package `minigames/` và bắt buộc `implements IMinigameStrategy`.
  2. Triển khai đầy đủ các hàm vòng đời: `start()`, `update()`, `render()`, `isFinished()`, `isWon()`.
  3. Khi muốn gọi minigame ra màn hình, phải bọc nó vào `MinigameScreen` (Context).
  ```java
  ScreenManager.getInstance().pushScreen(new MinigameScreen(new YourNewMinigame()));
  ```

---

## 3. QUY TẮC PHÁT TRIỂN GIAO DIỆN (UI & HUD)
**Kỹ thuật bắt buộc: Scene2D UI & Observer Pattern**

- **Về Vẽ UI:** KHÔNG dùng `SpriteBatch` để tự tính tọa độ vẽ từng nút bấm hay dòng chữ. Bắt buộc dùng `Stage`, `Table`, `Label`, `TextButton` của gói `com.badlogic.gdx.scenes.scene2d.ui` để giao diện có thể co giãn tự động (Responsive).
- **Về Font chữ:** Cấm dùng tool bên ngoài xuất file `.fnt`. Mọi font phải lấy từ `ResourceManager.getInstance()` (`dialogFont`, `nameFont`, `hudFont`).
- **Về Cập nhật dữ liệu (Observer Pattern):** 
  - Các thanh HUD (Máu, Thời gian) tuyệt đối không được gọi truy vấn dữ liệu (`getHp()`, `getHour()`) trong vòng lặp `render()`.
  - UI class phải `implements IObserver`. Dữ liệu chỉ được vẽ lại (cập nhật text) bên trong hàm `onNotify()` khi có tín hiệu gửi tới.

---

## 4. QUY TẮC LƯU TRỮ VÀ TẠO KẾT CỤC (ENDINGS)
**Kỹ thuật bắt buộc: Data-Driven Builder Pattern**

- Khi người chơi kết thúc một sự kiện, hãy gọi: `StoryManager.getInstance().recordResult(MinigameID..., isWin)`.
- Không dùng `if-else` lồng nhau để xử lý kết cục game. Nếu muốn thêm/sửa/xóa một Ending, hãy vào hàm `buildEndingsConfig()` trong `StoryManager.java` và chỉnh sửa chuỗi `.require(...)` theo Builder Pattern.

---

## 5. QUY TẮC LUÂN CHUYỂN TRẠNG THÁI (STATES)
**Kỹ thuật bắt buộc: State Pattern**

- Nhân vật `Cat` không chứa các biến dạng `boolean isRunning, isSleeping`. 
- Hành vi được quản lý qua `ICatState`. Nếu muốn thêm hành vi mới (Ví dụ: Ăn, Câu cá), hãy tạo State mới (Vd: `EatState`) và gọi `player.changeState(new EatState())`.

---

## 6. QUY ĐỊNH LÀM VIỆC CỦA AI AGENTS (MANDATORY WORKFLOW)
Đây là quy trình bắt buộc mọi AI Agent phải tuân theo khi hoàn thành một task:

1. **Hiểu rõ Kiến trúc:** Đọc file này và các file trong `docs/` để không làm phá vỡ kiến trúc OOP lõi.
2. **Tuân thủ Clean Code:** PascalCase cho Class, camelCase cho biến/hàm, UPPER_SNAKE_CASE cho hằng số. Mọi "Magic Number" phải đẩy vào `Constants.java`.
3. **Cập nhật Tiến trình (UPDATE PROGRESS):** 
   - Sau MỖI LẦN code xong một tính năng mới và compile thành công, Agent **BẮT BUỘC** phải mở file `docs/PROGRESS.md` ra.
   - Thêm tính năng vừa làm vào danh sách (đánh dấu `[x]`).
   - Ghi chú ngắn gọn tính năng đó dùng kỹ thuật gì.
4. **Cập nhật UML (UPDATE UML):**
   - Nếu có tạo Class/Interface mới, hoặc đổi tên hàm quan trọng, Agent **BẮT BUỘC** phải cập nhật lại sơ đồ trong file `docs/Full_Project_UML.md` để sơ đồ luôn phản ánh đúng 100% source code hiện tại.

*Dự án CatLife ưu tiên tính Kỷ Luật và Cấu trúc hệ thống vững vàng lên hàng đầu. Chúc các Agent làm việc hiệu quả!*