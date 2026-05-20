package hust.hedspi.oop.game.skills;

import hust.hedspi.oop.game.entities.StrayCat;

public class ScratchSkill extends BaseSkill {
    public ScratchSkill() {
        // Tên skill, Thời gian hồi chiêu (1.5 giây), Tiêu hao thể lực (10)
        super("Cào (Scratch)", 1.5f, 10);
    }

    @Override
    protected void performAction(StrayCat cat) {
        System.out.println(cat.getName() + " tung cú cào sắc lẹm! Gây sát thương vật lý lên kẻ địch phía trước.");
    }
}
