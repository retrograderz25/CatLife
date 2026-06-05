package hust.hedspi.oop.game.entities;

public class HouseCat extends Cat {
    
    private float energyRecoveryTimer = 0f;

    public HouseCat(float x, float y, float width, float height) {
        super(x, y, width, height, CatColor.WHITE); // Mặc định mèo nhà màu trắng
    }

    public HouseCat(float x, float y, float width, float height, CatColor color) {
        super(x, y, width, height, color);
    }

    @Override
    public void applyPassiveSkill(float dt) {
        // Tính Đa hình: Mèo nhà hồi phục thể lực nhanh khi ở trong nhà
        boolean isIndoors = true; // TODO: Lấy trạng thái map hiện tại từ GameManager/MapManager
        if (isIndoors) {
            energyRecoveryTimer += dt;
            if (energyRecoveryTimer >= 5.0f) { // Cứ 5 giây hồi 1 Energy
                increaseEnergy(1);
                energyRecoveryTimer -= 5.0f;
            }
        } else {
            energyRecoveryTimer = 0f;
        }
    }
}
