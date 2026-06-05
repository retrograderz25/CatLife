package hust.hedspi.oop.game.entities;

public class StrayCat extends Cat {
    
    private final int BASE_ATTACK_POWER = 10;
    private float energyRecoveryTimer = 0f;
    
    public StrayCat(float x, float y, float width, float height) {
        super(x, y, width, height, CatColor.ORANGE); // Mặc định là mèo hoang màu cam
        setAttackPower(BASE_ATTACK_POWER);
    }

    public StrayCat(float x, float y, float width, float height, CatColor color) {
        super(x, y, width, height, color);
        setAttackPower(BASE_ATTACK_POWER);
    }

    @Override
    public void applyPassiveSkill(float dt) {
        // Tính Đa hình: Mèo hoang tăng sức mạnh khi ít máu
        if (getHp() < 30) {
            setAttackPower(BASE_ATTACK_POWER * 2);
        } else {
            setAttackPower(BASE_ATTACK_POWER);
        }
        
        // Hồi phục thể lực theo thời gian
        if (getEnergy() < 100) {
            energyRecoveryTimer += dt;
            if (energyRecoveryTimer >= 0.2f) { // Hồi 5 energy mỗi giây -> 1 energy mỗi 0.2 giây
                increaseEnergy(1);
                energyRecoveryTimer -= 0.2f;
            }
        } else {
            energyRecoveryTimer = 0f;
        }
    }
}
