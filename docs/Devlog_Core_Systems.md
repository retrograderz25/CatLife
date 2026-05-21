# 🚀 DEVLOG: TỔNG HỢP CORE SYSTEMS ĐÃ HOÀN THÀNH

Tài liệu này ghi chú lại những hệ thống lõi (Core Systems) đã được xây dựng thành công trong Phase 1 của dự án CatLife. Đồng thời, nó đóng vai trò là "Sách Hướng Dẫn" (How-to-use) để Team Dev dựa vào đó phát triển các tính năng tiếp theo (UI, Map, Quest) một cách chuẩn mực.

---

## 1. Hệ Thống Quản Lý Lõi (Core Managers)
**🛠 Kỹ thuật áp dụng:** Singleton Pattern.

**✅ Đã hoàn thành:**
- `GameManager`: Quản lý người chơi (`Cat`) và trạng thái game (PLAYING, PAUSED...).
- `ScreenManager`: Chuyển cảnh an toàn bằng Stack (`pushScreen`, `clearAndSetScreen`).
- `TimeManager`: Đếm giờ in-game (1s = 2m), quản lý Ngày và Giai đoạn lớn lên (Phase).
- `StoryManager`: Quản lý lịch sử Minigame và chốt Ending.
- `SoundManager`: Khung quản lý âm thanh cơ bản (chờ gắn AssetManager).
- `ResourceManager`: Sinh tự động font Tiếng Việt chuẩn Pixel Art.

**💡 Cách dùng khi dev tính năng khác:**
- Cần trừ máu nhân vật từ một cạm bẫy? 
  👉 `GameManager.getInstance().getPlayer().decreaseHp(10);`
- Cần mở giao diện túi đồ đè lên màn hình hiện tại? 
  👉 `ScreenManager.getInstance().pushScreen(new InventoryScreen());`
- Cần lấy giờ hiện tại để quyết định NPC có ngủ hay không? 
  👉 `TimeManager.getInstance().getInGameHour();`

---

## 2. Hệ Thống Tối Ưu UI (UI Observer)
**🛠 Kỹ thuật áp dụng:** Observer Pattern (Subject/Observer).

**✅ Đã hoàn thành:**
- Interface `ISubject` (Kẻ phát tín hiệu) và `IObserver` (Kẻ lắng nghe).
- `TimeManager` đã được cài đặt làm Subject. Mỗi khi qua 1 phút in-game, nó sẽ tự động `notifyObservers()`.

**💡 Cách dùng khi dev tính năng khác (Làm HUD):**
- **TUYỆT ĐỐI KHÔNG** dùng vòng lặp `render(dt)` để cập nhật Text/Label liên tục.
- Khi tạo file `TimeHUD`, hãy cho class đó `implements IObserver`. Ở hàm khởi tạo, gọi `TimeManager.getInstance().addObserver(this);`. Sau đó, chỉ cập nhật chữ ở hàm `onNotify()`.

---

## 3. Hệ Thống Hành Vi Nhân Vật (Player State Machine)
**🛠 Kỹ thuật áp dụng:** State Pattern.

**✅ Đã hoàn thành:**
- Chuyển toàn bộ logic if-else di chuyển khổng lồ thành các Class rời rạc: `IdleState`, `RunState`, `SleepState`.
- Lớp `Cat` chỉ làm nhiệm vụ ủy quyền (delegate) việc update/render cho State hiện tại.

**💡 Cách dùng khi dev tính năng khác:**
- Khi bạn muốn Mèo rơi vào trạng thái "Bị Choáng" (Stunned) do dẫm phải bẫy:
  1. Tạo class `StunState implements ICatState`.
  2. Viết logic không cho bấm phím WASD trong hàm `update()`.
  3. Ở chỗ cạm bẫy, gọi: `player.changeState(new StunState());`.

---

## 4. Hệ Thống Thực Thể Tương Tác (Entity Hierarchy)
**🛠 Kỹ thuật áp dụng:** Kế thừa (Inheritance), Trừu tượng (Abstraction), Interface.

**✅ Đã hoàn thành:**
- Lớp gốc `Entity` quản lý tọa độ x,y và Hitbox. Lớp `Cat` quản lý chỉ số sinh tồn (HP, Energy, Speed, AttackPower).
- Các class cụ thể `StrayCat`, `HouseCat` tự định nghĩa nội tại qua `applyPassiveSkill()`.
- Lớp `TriggerZone` kế thừa `Entity` đóng vai trò vật thể ẩn có thể tương tác.
- Interface `IInteractable` và `IDamageable`.

**💡 Cách dùng khi dev tính năng khác:**
- Khi bạn muốn tạo một NPC Thùng Rác để bới:
  👉 Tạo `class TrashCan extends Entity implements IInteractable`. Viết logic rớt đồ vào trong hàm `onInteract(Cat player)`.

---

## 5. Hệ Thống Quản Lý Minigame (Minigame Strategy)
**🛠 Kỹ thuật áp dụng:** Strategy Pattern.

**✅ Đã hoàn thành:**
- Interface `IMinigameStrategy` và màn hình dùng chung `MinigameScreen`.
- Đã test thành công với `RhythmMinigame`. Khắc phục triệt để lỗi rò rỉ bộ nhớ (Access Violation) khi tắt màn hình bằng kỹ thuật trì hoãn thoát `shouldExit`.

**💡 Cách dùng khi dev tính năng khác:**
- Khi team Design giao cho làm minigame "Bắt Chuột":
  👉 Bạn KHÔNG cần tạo `CatchMouseScreen`. Bạn chỉ cần tạo `class CatchMouseGame implements IMinigameStrategy`. Sau đó gọi `ScreenManager.getInstance().pushScreen(new MinigameScreen(new CatchMouseGame()));`

---

## 6. Hệ Thống Điều Kiện Thời Gian & Không Gian (Time-Gated Trigger)
**🛠 Kỹ thuật áp dụng:** Component-based.

**✅ Đã hoàn thành:**
- Lớp `TimeCondition`: Bộ lọc thời gian (Từ giờ A -> giờ B, thuộc các thứ X, Y).
- Lớp `TriggerZone`: Hitbox tàng hình gắn kết với `TimeCondition`.

**💡 Cách dùng khi dev tính năng khác:**
- Khi làm Map (TiledMap), bạn đọc ra một ô màu đỏ (Khu vực chợ đêm). Bạn viết code Java:
  ```java
  TriggerZone nightMarket = new TriggerZone(x, y, w, h, "Chợ Đêm");
  nightMarket.setTimeCondition(new TimeCondition(20, 4)); // Mở từ 8h tối đến 4h sáng
  nightMarket.setLinkedMinigame(new FishingGame());
  ```

---

## 7. Hệ Thống Kết Cục Đa Nhánh (Data-Driven Endings)
**🛠 Kỹ thuật áp dụng:** Builder Pattern, Chain of Responsibility.

**✅ Đã hoàn thành:**
- Lập bảng History (lưu lịch sử Thắng/Thua của từng Minigame qua `MinigameID`).
- Áp dụng Builder Pattern để "lắp ráp" các Endings bằng mã lệnh cực kỳ dễ đọc. Đã nạp đầy đủ cấu hình Endings từ file Excel của Team Game Design.
- Xử lý mượt mà việc "Khóa/Mở" (Dependency) minigame và "Chết đột ngột" (Instant Game Over).

**💡 Cách dùng khi dev tính năng khác:**
- Khi người chơi chơi xong một minigame:
  👉 `StoryManager.getInstance().recordResult(MinigameID.LOVE_HIPHOP, true);`
- Khi NPC Đại Ca Mèo kiểm tra xem thằng nhãi này đủ tuổi đập mình chưa:
  👉 `if (StoryManager.getInstance().isMinigameUnlocked(MinigameID.GANG_FIGHT_BOSS)) { ... }`
- Chốt Ending cuối game:
  👉 `EndingCondition finalEnding = StoryManager.getInstance().evaluateFinalEnding(player);`

---

## 8. Hệ Thống Quản Lý Tài Nguyên & Font Chữ (ResourceManager)
**🛠 Kỹ thuật áp dụng:** Factory, Singleton, Freetype Font.

**✅ Đã hoàn thành:**
- Sử dụng `FreeTypeFontGenerator` để chuyển đổi file `zpix.ttf` thành `BitmapFont`.
- Hỗ trợ full chuỗi ký tự có dấu tiếng Việt trong `Constants`.
- Cấu hình Anti-aliasing (`TextureFilter.Nearest`) giữ nguyên chất lượng Pixel Art cổ điển.
- Phân tách làm 3 cấp độ: `dialogFont`, `nameFont`, `hudFont`.

**💡 Cách dùng khi dev tính năng khác:**
- Khi vẽ trên màn hình HUD hoặc Minigame:
  ```java
  BitmapFont font = ResourceManager.getInstance().dialogFont;
  font.draw(batch, "Chào con sen!", x, y);
  ```