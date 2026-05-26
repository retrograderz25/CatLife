# TIẾN TRÌNH DỰ ÁN (PROJECT PROGRESS)
*Tài liệu này được cập nhật liên tục sau mỗi thay đổi cấu trúc hoặc tính năng để theo dõi sát sao lộ trình dự án.*

## 🟩 PHASE 1: CORE ARCHITECTURE (Hoàn Thành 100%)
*Xây dựng bộ khung nền tảng bằng các Design Pattern tiêu chuẩn nhằm đảm bảo tính mở rộng, dễ bảo trì và loại bỏ Hardcode.*

- [x] Khởi tạo dự án LibGDX và dọn dẹp cấu trúc thư mục (packages).
- [x] **Core Managers (Singleton Pattern):** Hoàn thiện `GameManager`, `TimeManager`, `ScreenManager`, `StoryManager`, `ResourceManager`, `SoundManager` (placeholder).
- [x] **UI Observer System (Observer Pattern):** Áp dụng ISubject / IObserver để HUD chỉ cập nhật khi nhận tín hiệu từ Managers, loại bỏ lag khi đếm FPS.
- [x] **Player State Machine (State Pattern):** Xử lý mượt mà các trạng thái độc lập `Idle`, `Run`, `Sleep`, loại bỏ cấu trúc if-else rối rắm.
- [x] **Entity Hierarchy (Polymorphism/Inheritance):** Xây dựng cây kế thừa `Entity` -> `Cat`, phân tách logic nội tại đa hình giữa `StrayCat` và `HouseCat`.
- [x] **Minigame System (Strategy Pattern):** Tạo màn hình `MinigameScreen` dùng chung (Context) có thể cắm bất kỳ `IMinigameStrategy` (Ví dụ: `RhythmMinigame`) nào vào mà không cần viết lại logic chuyển cảnh.
- [x] **Time-Gated Events (Component-based):** Thiết kế class `TimeCondition` và `TriggerZone` để dễ dàng khóa/mở sự kiện theo khung giờ trong ngày in-game.
- [x] **Data-Driven Endings (Builder Pattern & Chain of Responsibility):** Chuyển dịch toàn bộ từ mảng cờ Boolean cũ sang hệ thống chấm điểm kết quả `MinigameID` & `GameResult`. Cấu hình toàn bộ Ending linh hoạt trong `StoryManager`.
- [x] **Pixel Art Fonts (Factory/ResourceManager):** Sinh tự động font Tiếng Việt qua `FreeTypeFontGenerator`, giữ nguyên độ sắc nét (Nearest Filter) với 3 cấp độ: HUD, Tên nhân vật, Thoại.
- [x] **Memory Management:** Vá triệt để lỗi Memory Leak / Access Violation (Crashed Native Code) khi giải phóng bộ nhớ `SpriteBatch` ở màn hình Minigame bằng cách dời logic sang Frame an toàn qua biến `shouldExit`.
- [x] **Hệ thống Kỹ năng cũ (Legacy Code Fix):** Nâng cấp gói `skills` (Dash, Hiss, Scratch) để đồng bộ với cấu trúc `Energy` mới của `Cat`.

## 🟨 PHASE 2: GAMEPLAY & TÍNH NĂNG (Đang Chuẩn Bị)
*Giai đoạn thiết kế Bản đồ, va chạm vật lý, giao diện chi tiết và hệ thống NPC.*

- [x] Tích hợp thư viện Map (TiledMap `*.tmx`). Cài đặt `PlayScreen` và `MapManager`.
- [ ] Khởi tạo Box2D hoặc hệ thống xử lý va chạm cơ bản.
- [x] Giao diện (Scene2D UI) cho Main Menu, Inventory và Hộp thoại. (Đã bổ sung tính năng F11 chuyển đổi Windowed/Fullscreen thông minh).
- [x] Hoàn thiện Map rendering: Tích hợp `ExtendViewport` xóa viền đen (Letterboxing), Fix lỗi lệch trục Y của TiledMap (`offsety`).
- [ ] Hệ thống hộp thoại RPG (Dialogue System với Typewriter Effect).
- [ ] Gắn kết AssetManager đầy đủ cho SoundManager.
- [ ] Phát triển công cụ F12 (Developer Debug Kit).

## 🟥 PHASE 3: CONTENT & ĐÁNH BÓNG (Tương Lai)
- [x] Vẽ / Tích hợp toàn bộ Sprite Animation (Đã tích hợp cơ chế load SpriteSheet và chuyển đổi trạng thái Animation cho Cat).
- [x] Sửa mượt animation và di chuyển (Fix UV mutation glitch bằng Scene2D draw flip, chuẩn hóa vector di chuyển chéo, và fix flickering state).
- [ ] Code logic cho các Minigame cụ thể.
- [ ] Tích hợp âm thanh, BGM.
- [ ] Cân bằng game (Chỉ số hồi/tiêu hao năng lượng, độ khó minigame).