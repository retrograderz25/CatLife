package hust.hedspi.oop.game.entities;

import hust.hedspi.oop.game.utils.Constants;

public class HouseCat extends Cat {
    
    private float energyRecoveryTimer = 0f;

    public HouseCat(float x, float y, float width, float height) {
        super(x, y, width, height, CatColor.WHITE); 
    }

    public HouseCat(float x, float y, float width, float height, CatColor color) {
        super(x, y, width, height, color);
    }

    @Override
    public void applyPassiveSkill(float dt) {
        
    }
}
