# Kiến Trúc Các Manager (Core Managers)

Tài liệu này giải thích cấu trúc và mối quan hệ giữa các Core Manager trong hệ thống của dự án CatLife. Các thành phần này đã được tinh chỉnh nghiêm ngặt theo mô hình Sơ đồ UML.

## 1. Mục Tiêu Thiết Kế
Đảm bảo luồng điều phối của game hoạt động mượt mà, phân tách rõ ràng trách nhiệm của từng Manager. Chúng tuân theo **Singleton Pattern** để đảm bảo luôn chỉ có 1 phiên bản duy nhất xuyên suốt vòng đời của game.

## 2. Chi Tiết Các Manager

### A. GameManager (Bộ Điều Phối Trung Tâm)
- Chịu trách nhiệm quản lý vòng đời nhân vật (Cat) và Máy trạng thái của game (`GameState`: `MENU`, `PLAYING`, `PAUSED`, `MINIGAME`, `GAME_OVER`).
- Cung cấp các hàm điều hướng chính như `startNewGame(isStrayCat: boolean)`, `pauseGame()`, `resumeGame()`.
- Là nơi chứa con trỏ `player` duy nhất, cho phép tất cả các hệ thống khác lấy được nhân vật hiện tại thông qua `GameManager.getInstance().getPlayer()`.

### B. TimeManager (Bộ Đếm Thời Gian - Observer/Subject Pattern)
- Không chỉ đếm giờ/phút (`inGameHour`, `inGameMinute`), `TimeManager` giờ đây quản lý cả Ngày trong tuần (`currentDayOfWeek`) và Giai đoạn phát triển của Mèo (`currentPhase`: `CHILDHOOD`, `ADULT`, `SENIOR`).
- Đóng vai trò là **Subject**, nó sẽ thông báo (`notifyObservers`) tới các màn hình HUD (như `TimeHUD`) mỗi khi thời gian thay đổi, tránh việc UI gọi truy vấn liên tục mỗi frame gây sụt giảm hiệu năng.
- Bổ sung hàm `skipToNextMorning()` giúp tua nhanh thời gian khi Mèo đi ngủ.

### C. StoryManager (Bộ Xử Lý Cốt Truyện)
- Lịch sử game được theo dõi qua `playerHistory` dựa trên MinigameID thay vì EventFlags cũ.
- Áp dụng **Chain of Responsibility Pattern** cùng **Builder Pattern** cho danh sách `possibleEndings`. Khi kết thúc game, hàm `evaluateFinalEnding(player)` sẽ duyệt qua các `EndingCondition` theo thứ tự ưu tiên giảm dần để chọn ra kết cục phù hợp nhất.
- Hỗ trợ hàm `isMinigameUnlocked` để quản lý mở khóa tiến trình (Dependency logic).

### D. ScreenManager (Quản Lý Màn Hình)
- Hoạt động như một cấu trúc `Stack<Screen>`. Giúp dễ dàng chuyển qua lại giữa các màn hình, ví dụ: Đang ở `PlayScreen` -> Đẩy (Push) `MinigameScreen` lên trên cùng -> Chơi xong thì Pop ra để quay lại `PlayScreen` ban đầu.
- Sở hữu hàm `clearAndSetScreen()` để dọn dẹp các màn hình cũ khi đổi cảnh (tránh rò rỉ bộ nhớ).

### E. ResourceManager (Quản Lý Tài Nguyên & Font Chữ)
- Quản lý việc sinh ra (generate) các font chữ Tiếng Việt trực tiếp từ file `.ttf` bằng thư viện `gdx-freetype`.
- Khởi tạo 3 cấp độ hiển thị: `hudFont` (To, viền/đổ bóng), `nameFont` (Vừa, viền nổi bật), `dialogFont` (Chuẩn, dễ đọc).
- Cài đặt cấu hình Pixel Art (`TextureFilter.Nearest`) để không bị nhòe mờ.
- Xử lý gom nhóm `dispose()` tài nguyên an toàn khi game tắt.

### F. SoundManager (Quản Lý Âm Thanh)
- Tích hợp chặt chẽ với `AssetManager` của libGDX để tải và giải phóng file âm thanh an toàn, tránh văng game do hết RAM.
- Tách biệt quản lý âm lượng giữa Nhạc nền (BGM) và Hiệu ứng (SFX) thông qua các biến `bgmVolume` và `sfxVolume`.

## 3. Tổng Kết
Sự phân rã này giúp dự án trở nên cực kỳ linh hoạt (Decoupled). Bất kỳ một Class tính năng nào cũng có thể truy cập `GameManager` để xin thông tin người chơi, hay yêu cầu `SoundManager` phát nhạc, mà không cần phải truyền liên kết rườm rà qua tham số khởi tạo.