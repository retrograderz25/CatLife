package hust.hedspi.oop.game.entities;

public class HouseCat extends Cat {
    public HouseCat(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    @Override
    public void applyPassiveSkill() {
        // Tính Đa hình: Mèo nhà hồi phục thể lực nhanh khi ở trong nhà
        boolean isIndoors = true; // TODO: Lấy trạng thái map hiện tại từ GameManager/MapManager
        if (isIndoors) {
            // System.out.println("HouseCat Passive: Fast energy recovery indoors!");
            increaseEnergy(1); // Tự động hồi phục
        }
    }
}
