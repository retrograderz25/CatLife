package hust.hedspi.oop.game.skills;

import hust.hedspi.oop.game.entities.Cat;

public class DashSkill extends BaseSkill {
    public DashSkill() {
        
        super("Chạy trốn (Dash)", 3.0f, 25);
    }

    @Override
    protected void performAction(Cat cat) {
        System.out.println("Mèo lẩn trốn nhanh như chớp! Tăng mạnh tốc độ di chuyển trong thời gian ngắn.");
    }
}
