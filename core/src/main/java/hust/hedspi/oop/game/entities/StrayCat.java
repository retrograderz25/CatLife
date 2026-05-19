package hust.hedspi.oop.game.entities;

public class StrayCat extends Cat {
    public StrayCat(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    @Override
    public void applyPassiveSkill() {
        // Tính Đa hình: Mèo hoang tăng sức mạnh khi ít máu
        if (getHp() < 30) {
            // System.out.println("StrayCat Passive: Attack power increased due to low HP!");
            // TODO: Áp dụng buff tăng sát thương / tốc chạy
        }
    }
}
