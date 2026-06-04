# Cấu Hình Điều Kiện Xuất Hiện Minigame (Time & Dependencies)

Tài liệu này quy định chi tiết các điều kiện để một Trigger Zone (Khu vực nhiệm vụ) xuất hiện trên bản đồ và cho phép người chơi tương tác. Nếu không thỏa mãn các điều kiện này, icon nhiệm vụ sẽ không hiện lên và người chơi không thể mở hộp thoại.

## 1. Nhóm Hoạt Động Hằng Ngày (Daily)
- **Cào móng (Caomong)** -> *Game: Cào Móng*
  - **Thời gian:** Cả ngày (Luôn mở)
  - **Tiên quyết:** Không có.
- **Cống ngầm (Sewage)** -> *Game: Thoát khỏi cống*
  - **Thời gian:** Cả ngày
  - **Tiên quyết:** Không có.
- **Hẻm nhỏ (Warzone 1)** -> *Game: Võ mèo 1v1*
  - **Thời gian:** Từ 18h trở đi.
  - **Tiên quyết:** Không có.

## 2. Nhóm Tình Yêu (Love)
- **Tượng đài Lenin (Le-nin)** -> *Game: Nhảy Hiphop*
  - **Thời gian:** Cả ngày
  - **Tiên quyết:** Không có.
- **Khách sạn (Hotel Gate)** -> *Game: Thám tử tìm Tuesday*
  - **Thời gian:** Cả ngày
  - **Tiên quyết:** Bắt buộc **THẮNG** game Nhảy Hiphop (Tượng đài Lenin).

## 3. Nhóm Giang Hồ (Gang)
- **Góc phố (Warzone 2)** -> *Game: Võ mèo đánh Đại ca*
  - **Thời gian:** Từ 18h trở đi.
  - **Tiên quyết:** Bắt buộc **THẮNG** game Võ mèo 1v1 (Warzone 1).

## 4. Nhóm Nuôi Nhốt (Pet)
- **Nhà dân (Adopt)** -> *Game: Làm nũng để nhận nuôi* (Hiện đang là Coming Soon)
  - **Thời gian:** Từ 17h trở đi (Giờ con người đi làm về).
  - **Tiên quyết:** Không có.
- **Cửa hàng (Bubble)** -> *Game: Đại chiến xà phòng / Tắm*
  - **Thời gian:** Từ 17h trở đi.
  - **Tiên quyết:** Bắt buộc **THẮNG** game Làm nũng (Adopt).
- **Phòng khám (Office Gate)** -> *Game: Trốn kim tiêm*
  - **Thời gian:** Từ 17h trở đi.
  - **Tiên quyết:** Bắt buộc **THẮNG** game Làm nũng (Adopt).

## 5. Nhóm Trộm Mèo (Thief)
- **Bãi đỗ xe (Exciter)** -> *Game: Trốn trộm mèo*
  - **Thời gian:** Từ 20h trở đi.
  - **Tiên quyết:** Không có.
- **(Bị nhốt lồng)** -> *Game: Thoát khỏi lồng*
  - *Đây là game tự động chuyển cảnh (Auto-chained) nếu Thua game Trốn trộm mèo, không có trigger trực tiếp trên map.*