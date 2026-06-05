package hust.hedspi.oop.game.entities;

import hust.hedspi.oop.game.utils.Constants;

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
        // Tính Đa hình: Mèo nhà hồi phục thể lực chậm
        boolean isIndoors = true; // TODO: Lấy trạng thái map hiện tại từ GameManager/MapManager
        if (isIndoors) {
            energyRecoveryTimer += dt;
            if (energyRecoveryTimer >= Constants.HOUSE_CAT_ENERGY_RECOVERY_RATE) { 
                increaseEnergy(1);
                energyRecoveryTimer -= Constants.HOUSE_CAT_ENERGY_RECOVERY_RATE;
            }
        } else {
            energyRecoveryTimer = 0f;
        }
    }
}
