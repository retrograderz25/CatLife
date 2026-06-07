package hust.hedspi.oop.game.skills;

import hust.hedspi.oop.game.entities.Cat;

public class ScratchSkill extends BaseSkill {
    public ScratchSkill() {
        
        super("Cào (Scratch)", 1.5f, 10);
    }

    @Override
    protected void performAction(Cat cat) {
        System.out.println("Mèo tung cú cào sắc lẹm! Gây sát thương vật lý lên kẻ địch phía trước.");
    }
}
