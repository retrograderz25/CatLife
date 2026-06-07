package hust.hedspi.oop.game.entities;

import hust.hedspi.oop.game.utils.Constants;

public class StrayCat extends Cat {
    
    public StrayCat(float x, float y, float width, float height) {
        super(x, y, width, height, CatColor.ORANGE); // Mặc định là mèo hoang màu cam
        setAttackPower(Constants.BASE_ATTACK_POWER);
    }

    public StrayCat(float x, float y, float width, float height, CatColor color) {
        super(x, y, width, height, color);
        setAttackPower(Constants.BASE_ATTACK_POWER);
    }

    @Override
    public void applyPassiveSkill(float dt) {
        // Tính Đa hình: Mèo hoang tăng sức mạnh khi ít máu
        if (getHp() < Constants.LOW_HP_THRESHOLD) {
            setAttackPower(Constants.BASE_ATTACK_POWER * 2);
        } else {
            setAttackPower(Constants.BASE_ATTACK_POWER);
        }
    }
}
