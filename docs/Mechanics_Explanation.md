# Kiến Trúc Các Cơ Chế Cốt Lõi (Mechanics)

Tài liệu này giải thích chi tiết về 3 cơ chế vật lý / logic nền tảng đang chạy ngầm trong dự án `CatLife`: Hệ thống quy đổi thời gian, Cơ chế tiêu hao/phục hồi Thể lực (Energy), và Cơ chế Máu (HP).

## 1. Hệ Thống Quy Đổi Thời Gian (Time System)

Toàn bộ thời gian trong game được quản lý bởi `TimeManager` và được cấu hình trong `utils/Constants.java`.

- **Tỷ lệ quy đổi (Time Scale):**
  - Hằng số `REAL_SECONDS_PER_IN_GAME_MINUTE = 0.5f`.
  - Nghĩa là: **0.5 giây ngoài đời thực = 1 phút trong game**.
  - Tương đương: 1 giây đời thực = 2 phút game.
  - Tương đương: **30 giây đời thực = 1 giờ trong game**.
  - Một ngày trong game (24 giờ) sẽ trôi qua sau vỏn vẹn **12 phút** đồng hồ đời thực.

- **Cơ chế cập nhật (Observer):**
  - Biến `timer` trong `TimeManager` liên tục cộng dồn `deltaTime`. Cứ mỗi khi `timer >= 0.5f`, game sẽ tự động nhảy lên 1 phút (`inGameMinute++`), đồng thời gọi `notifyObservers()` để làm mới đồng hồ (TimeHUD) góc phải màn hình. Việc này đảm bảo UI hiển thị số phút làm tròn một cách chuẩn xác mà không bị lẹm số thập phân.

## 2. Cơ Chế Thể Lực (Energy) và Tính Đa Hình

Mèo có 3 chỉ số sinh tồn chính: HP (Máu), Hunger (Độ đói), và Energy (Thể lực). Mức tối đa của mỗi chỉ số đều là 100. Năng lượng được tính toán và phục hồi liên tục mỗi khung hình thông qua hàm trừu tượng `applyPassiveSkill(float dt)` nằm trong `Cat.java`.

- **Đối với Mèo Hoang (StrayCat):**
  - Mèo hoang quen với sương gió nên có khả năng **hồi phục thể lực cực nhanh**.
  - Công thức: `increaseEnergy((int)(5.0f * dt))`.
  - Nghĩa là: Cứ **1 giây ngoài đời thực, Mèo Hoang hồi 5 Energy**.
  - Thời gian hồi đầy bình (0 -> 100): 20 giây thực (tương đương 40 phút ingame).

- **Đối với Mèo Nhà (HouseCat):**
  - Mèo nhà lười biếng hơn, thể lực **hồi phục rất chậm** và bị ràng buộc bởi điều kiện `isIndoors` (chỉ khi ở trong nhà mới được hồi).
  - Công thức: `if (energyRecoveryTimer >= 5.0f) { increaseEnergy(1); }`.
  - Nghĩa là: Phải mất **5 giây ngoài đời thực mới hồi được 1 Energy**.
  - Thời gian hồi đầy bình (0 -> 100): 500 giây thực (gần 8.5 phút đời thực).
  
> ⚠️ **Lưu ý cho Dev:** Mọi hành vi làm tiêu hao Energy (như chạy, cào, nhảy) sẽ được viết trực tiếp bên trong `RunState` hoặc gói `skills/`. Luôn dùng hàm `cat.decreaseEnergy(amount)` đã được đóng gói an toàn (Encapsulation) để Energy không bao giờ bị rớt xuống dưới số 0.

## 3. Cơ Chế Máu và Sức Mạnh (HP & Attack Power)

- Máu (HP) mặc định khởi điểm là 100.
- Sức mạnh tấn công (`attackPower`) mặc định là 10 (`BASE_ATTACK_POWER`).
- **Nội tại Điên Cuồng của Mèo Hoang:**
  - Nếu `StrayCat` bị trừ máu xuống dưới 30 (Sắp chết), nội tại sinh tồn sẽ được kích hoạt tại `applyPassiveSkill`.
  - `attackPower` tự động nhân đôi lên mức 20 (`BASE_ATTACK_POWER * 2`). Khi máu hồi lại trên 30, sức mạnh sẽ trả về 10.
- Tương tự như Energy, mọi thay đổi về HP (trừ máu từ cạm bẫy, ăn cá hồi máu) phải đi qua `increaseHp()` và `decreaseHp()`. Bất kỳ sự thay đổi nào cũng sẽ kích hoạt tín hiệu `notifyObservers()` để thanh PlayerHUD góc trái màn hình cập nhật ngay lập tức lập tức (Máu: 90/100).
- Nếu `hp <= 0`, `GameManager` sẽ bắt tín hiệu và đẩy GameState về `GAME_OVER`, đồng thời gọi `StoryManager` để chốt màn hình Ending.