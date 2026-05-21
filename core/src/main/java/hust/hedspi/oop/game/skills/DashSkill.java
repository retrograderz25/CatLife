package hust.hedspi.oop.game.skills;

import hust.hedspi.oop.game.entities.Cat;

public class DashSkill extends BaseSkill {
    public DashSkill() {
        // Tên skill, Thời gian hồi chiêu (3.0 giây), Tiêu hao thể lực (25)
        super("Chạy trốn (Dash)", 3.0f, 25);
    }

    @Override
    protected void performAction(Cat cat) {
        System.out.println("Mèo lẩn trốn nhanh như chớp! Tăng mạnh tốc độ di chuyển trong thời gian ngắn.");
    }
}
