# Hướng Dẫn Tích Hợp Minigame An Toàn (Merge Guidelines)

Tài liệu này quy định quy trình 5 bước bắt buộc phải tuân thủ khi thực hiện merge các Minigame từ nhánh khác (branch) vào nhánh chính (main/master) của dự án `CatLife`. Quy trình này nhằm bảo vệ bộ khung kiến trúc (Core Architecture) vững chắc đã được xây dựng trong Phase 1.

Tuyệt đối **KHÔNG** sử dụng lệnh `git merge` để đè code trực tiếp mà chưa qua bước Review và Refactor theo chuẩn **Hiến Pháp Dev (AGENT_DEV_GUIDELINES)**.

---

## Bước 1: Ép khuôn theo Strategy Pattern (Quan trọng nhất)
Kiến trúc dự án không cho phép mỗi minigame tạo ra một màn hình (`Screen`) độc lập. Việc này gây rườm rà và lặp lại code chuyển cảnh.

- Yêu cầu người code minigame sửa đổi class của họ: Thay vì `extends Screen`, bắt buộc phải `implements IMinigameStrategy`.
- Yêu cầu họ xóa bỏ các đoạn code khởi tạo `Camera`, `Viewport`, `SpriteBatch` thừa thãi bên trong class minigame.
- Bắt buộc nhồi toàn bộ logic của minigame vào đúng 5 hàm vòng đời của Interface:
  - `start()`: Khởi tạo dữ liệu.
  - `update(float dt)`: Cập nhật logic (di chuyển, tính điểm...).
  - `render(SpriteBatch batch)`: Vẽ đồ họa.
  - `isFinished()`: Trả về `true` khi trò chơi kết thúc.
  - `isWon()`: Trả về `true` nếu người chơi thắng, `false` nếu thua.

## Bước 2: Tích hợp vào hệ thống Quản lý cốt truyện (StoryManager)
Minigame không được tự ý gọi hàm chuyển cảnh (ví dụ: `setScreen(new PlayScreen())`) hay tự ý tính điểm cốt truyện khi kết thúc.

- Mở file `MinigameID.java` (hoặc cấu hình tương đương), khai báo thêm ID hằng số cho minigame mới (VD: `MinigameID.CATCH_MOUSE`).
- Trong hàm `update()` của minigame, khi có kết quả thắng/thua, lập trình viên chỉ cần gán cờ nội bộ `isFinished = true` và `isWon = true/false`.
- Class bao bọc (Context) là `MinigameScreen` của hệ thống lõi sẽ tự động theo dõi. Khi `isFinished()` trả về true, nó sẽ lấy kết quả từ `isWon()` và gọi `StoryManager.getInstance().recordResult(...)` để lưu vào lịch sử người chơi, sau đó tự động gọi `popScreen()` để trả người chơi về lại đúng vị trí đang đứng trên bản đồ đường phố.

## Bước 3: Tiêu diệt Hardcode & Đồng bộ I18N
Dự án áp dụng I18N (Internationalization) triệt để.

- **Về Text:** Dò tìm toàn bộ các đoạn code trong minigame có chứa chuỗi String hiển thị trực tiếp (Ví dụ: `font.draw(batch, "Chúc mừng bạn đã thắng!", ...)`). Bắt buộc cắt các chuỗi đó và ném vào file cấu hình ngôn ngữ `assets/i18n/dialogues_vi.properties`. Sau đó, gọi ra bằng lệnh: 
  `ResourceManager.getInstance().getBundle().get("key_tuong_ung")`.
- **Về UI:** Nếu minigame sử dụng các nút bấm hoặc bảng thông báo, yêu cầu sử dụng `Stage`, `Table` của Scene2D UI và dùng chung bộ UI `NinePatch` (như `panel.png`, `button_blue.png`) đã được chuẩn hóa để giữ tính nhất quán về mặt thị giác cho toàn game.

## Bước 4: Vá rò rỉ bộ nhớ (Memory Leak Check)
Giải phóng tài nguyên sai cách là nguyên nhân gây crash game (Access Violation) thường gặp nhất.

- Phải soi kỹ hàm `dispose()` trong class minigame mới. Mọi đối tượng như `Texture`, `Sound`, `Music`, hoặc `BitmapFont` (nếu minigame tự tạo mới độc lập) đều **bắt buộc** phải được gọi `.dispose()` tại đây.
- **Cảnh báo:** Nếu minigame sử dụng tài nguyên dùng chung lấy từ `ResourceManager` (ví dụ: `ResourceManager.getInstance().dialogFont`), nghiêm cấm việc tự ý gọi `.dispose()` lên các tài nguyên này. Nếu giải phóng nhầm, khi quay trở lại màn hình chính (`PlayScreen`), game sẽ bị lỗi hiển thị khối đen hoặc crash.

## Bước 5: Cắm Minigame vào Map
Sau khi minigame đã được chuẩn hóa và an toàn, để nhân vật Mèo có thể chơi được minigame đó trong thế giới game, tiến hành cắm nó vào bản đồ:

- Mở file `MapManager.java` hoặc `PlayScreen.java`.
- Tìm đến `TriggerZone` tương ứng trên bản đồ (Ví dụ: Khu vực cống ngầm - `Sewage`).
- Cắm class minigame vào TriggerZone bằng lệnh:
  ```java
  // Ví dụ cắm minigame Câu cá vào Trigger "Sewage"
  zone.setLinkedMinigame(new FishingGame());
  ```
- Khi Mèo bước vào vùng Trigger và bấm phím tương tác (Enter/Space), hệ thống `InteractionUI` sẽ tự động kích hoạt và đẩy Minigame này lên màn hình thông qua cơ chế Strategy mà không cần phải viết thêm bất kỳ hàm `if-else` phân luồng nào.

---
**Kết luận:** Việc áp dụng đúng bộ quy tắc lọc 5 bước này đảm bảo rằng dù dự án có thêm hàng chục minigame mới, Codebase vẫn sẽ sạch sẽ, kiến trúc luôn gọn gàng, chạy mượt mà và file `PlayScreen` sẽ không bao giờ bị phình to (Bloated) hay trở thành Spaghetti Code.