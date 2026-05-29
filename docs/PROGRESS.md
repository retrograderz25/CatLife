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

- [ ] Tích hợp thư viện Map (TiledMap `*.tmx`).
- [ ] Khởi tạo Box2D hoặc hệ thống xử lý va chạm cơ bản.
- [ ] Giao diện (Scene2D UI) cho Main Menu, Inventory và Hộp thoại.
- [ ] Hệ thống hộp thoại RPG (Dialogue System với Typewriter Effect).
- [ ] Gắn kết AssetManager đầy đủ cho SoundManager.
- [ ] Phát triển công cụ F12 (Developer Debug Kit).

## 🟥 PHASE 3: CONTENT & ĐÁNH BÓNG (Tương Lai)
- [ ] Vẽ / Tích hợp toàn bộ Sprite Animation.
- [x] **Minigame Cào Móng – `CaoMongMinigame` (Strategy + I18N + StoryManager):** Minigame đầu tiên hoàn chỉnh: background, board, 4 zone mũi tên xoay đúng hướng bằng `TextureRegion` rotation, bàn tay mèo flash khi bấm đúng, đếm ngược 30 giây, game over overlay. Tuân thủ: font lấy từ `ResourceManager`, text từ `I18NBundle` (`assets/i18n/ui_vi.properties`), hằng số trong `Constants`, kết quả ghi vào `StoryManager.recordResult(DAILY_SCRATCH, won)`.
- [x] **Minigame Thoát Khỏi Cống – `ThoatKhoiCongMinigame` (Strategy + Pixmap Collision + Camera Scroll + I18N):** Mèo điều hướng qua mê cung 1024×1024 trong 45 giây. Va chạm pixel-perfect bằng `Pixmap` đọc alpha channel của `road.png`. Camera cuộn dọc theo mèo (ngang cố định vì maze < screen). Sprite animation 3 trạng thái (IDLE/WALK/RUN) từ sprite sheet. Hiệu ứng bóng tối (`dark_frame.png` scale 2× màn hình) fade-in khi mèo rời spawn, tâm trong suốt tạo tầm nhìn hạn chế. Kết quả ghi vào `StoryManager.recordResult(DAILY_ESCAPE_SEWER, won)`.
- [x] **Minigame Trốn Kim Tiêm – `TronKimTiemMinigame` (Strategy + Spawning + Particle Trails + I18N):** Tránh các đạn độc (`poisonball.png`) bắn ra từ Boss (`boss.png`) ở trung tâm màn hình trong 45 giây. Boss tự động xoay và phập phồng (pulse), giật sáng lóe đỏ khi bắn đạn. Đạn độc có vệt mờ ghost trail ảo diệu bám đuôi, tốc độ và tần số đạn tăng dần theo thời gian. Mèo di chuyển bằng WASD/Mũi tên, giữ Shift để chạy nhanh (hỗ trợ đầy đủ hoạt ảnh WALK, RUN, IDLE). Khung thời gian rung lắc kịch tính khi dưới 10 giây. Kết quả ghi nhận vào `StoryManager.recordResult(PET_ESCAPE_VET, won)`.
- [x] **Minigame Trộm Mèo – `TromMeoMinigame` (Strategy + Spawning + Particle Trails + I18N):** Bản sao tùy chỉnh của Trốn Kim Tiêm sử dụng bẫy thú (`trap.png`) thay vì kim tiêm, tuân thủ kiến trúc Strategy và I18N đầy đủ. Kết quả ghi nhận vào `StoryManager.recordResult(THIEF_HIDE, won)`.
- [x] **Minigame Võ Mèo Lang Thang / Quyền Anh Mèo – `CombatMinigame` (Strategy + Rythmic Lane Blocking + I18N):** Trò chơi đối kháng nhịp điệu với 3 đối thủ mèo ở trên, thả cú đấm (hand) xuống 3 làn đường tương ứng (A/S/D). Người chơi phải đỡ đòn khi cú đấm trúng đích tại `frame.png`. Có hiệu ứng bàn tay bảo vệ `cat_hand.png` hiển thị khi tương tác, giới hạn hụt tối đa 4 đòn trong 45 giây chơi. Kết quả ghi nhận vào `StoryManager.recordResult(DAILY_FIGHT_STRAY, won)`.
- [x] **Minigame Võ Mèo Lang Thang Đơn – `CombatDonMinigame` (Strategy + Rythmic Lane Blocking + I18N):** Bản thể võ mèo đối kháng nhịp điệu nhưng đơn giản hóa: chỉ sử dụng duy nhất đối thủ trắng (`opponent_white.png`) và một loại đòn đấm hai màu (`cat_punch/hnad_bicolor.png`) rơi dọc 3 làn. Kết quả ghi nhận vào `StoryManager.recordResult(GANG_FIGHT_1VN, won)`.
- [x] **Minigame Nhảy Hip Hop – `NhayHipHopMinigame` (Strategy + Input Sequence + Rotated Rendering + I18N):** Minigame nhảy hiphop đối kháng nhịp điệu. Game tạo ngẫu nhiên chuỗi 10 mũi tên thuộc 4 màu sắc (`yellow.png`, `blue.png`, `green.png`, `red.png`) phân bổ thành 2 hàng trong khung `frame.png`. Người chơi phải bấm đúng thứ tự trong 10 giây (tổng 5 lượt chơi độc lập, thắng >= 3 lượt để hoàn thành). Mỗi lần bấm đúng, mũi tên biến mất và chạy hoạt ảnh nhân vật xoay vòng 3 ảnh (`spin1` -> `spin2` -> `spin3`). Bấm sai sẽ hiển thị chữ "MISS!", reset lượt chơi hiện tại và nhân vật chuyển về `idle.png`. Hỗ trợ thanh đo thời gian đổi màu sinh động. Kết quả ghi nhận vào `StoryManager.recordResult(MinigameID.LOVE_HIPHOP, won)`.
- [x] **Minigame Thoát Khỏi Lồng – `ThoatKhoiLongMinigame` (Strategy + Texture Slicing + Sliding Puzzle Parity + I18N):** Trò chơi trượt tranh giải đố kích thước 4x3 (12 ô). Game tự động chia cắt ảnh `puzzle.png` thành 12 phần 64x64 và bỏ ô cuối góc phải để tráo tranh bằng 150 bước trượt hợp lệ ngẫu nhiên. Người chơi sử dụng tương tác Chuột click hoặc phím WASD/Mũi tên để di chuyển mảnh ghép. Khung chứa `puzzle_frame.png` nằm ở trung tâm và đồ trang trí Clove/Omen bố trí tinh tế ở hai góc dưới màn hình. Khung thời gian `timeframe.png` đếm ngược 60 giây ở góc trên bên phải. Kết quả ghi nhận vào `StoryManager.recordResult(MinigameID.THIEF_ESCAPE_CAGE, won)`.
- [ ] Tích hợp âm thanh, BGM.
- [ ] Cân bằng game (Chỉ số hồi/tiêu hao năng lượng, độ khó minigame).