package hust.hedspi.oop.game.skills;

import hust.hedspi.oop.game.entities.Cat;

public class HissSkill extends BaseSkill {
    public HissSkill() {
        
        super("Khè (Hiss)", 5.0f, 15);
    }

    @Override
    protected void performAction(Cat cat) {
        System.out.println("Mèo khè dữ dội! Kẻ địch xung quanh bị hoảng sợ và giảm tốc độ.");
    }
}
