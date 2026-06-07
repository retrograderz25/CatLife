package hust.hedspi.oop.game.entities;

import hust.hedspi.oop.game.utils.Constants;

public class StrayCat extends Cat {
    
    public StrayCat(float x, float y, float width, float height) {
        super(x, y, width, height, CatColor.ORANGE); 
        setAttackPower(Constants.BASE_ATTACK_POWER);
    }

    public StrayCat(float x, float y, float width, float height, CatColor color) {
        super(x, y, width, height, color);
        setAttackPower(Constants.BASE_ATTACK_POWER);
    }

    @Override
    public void applyPassiveSkill(float dt) {
        
        if (getHp() < Constants.LOW_HP_THRESHOLD) {
            setAttackPower(Constants.BASE_ATTACK_POWER * 2);
        } else {
            setAttackPower(Constants.BASE_ATTACK_POWER);
        }
    }
}
