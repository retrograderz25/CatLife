package hust.hedspi.oop.game.entities;

import hust.hedspi.oop.game.skills.Skill;
import java.util.ArrayList;
import java.util.List;

public class StrayCat {
    private String name;
    private float stamina;
    private float maxStamina;
    private List<Skill> skills;

    public StrayCat(String name) {
        this.name = name;
        this.maxStamina = 100f;
        this.stamina = this.maxStamina;
        this.skills = new ArrayList<>();
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public void useSkill(int index) {
        if (index >= 0 && index < skills.size()) {
            skills.get(index).use(this);
        } else {
            System.out.println("Kỹ năng ở vị trí " + index + " không tồn tại!");
        }
    }

    public void update(float delta) {
        for (Skill skill : skills) {
            skill.update(delta);
        }
        
        // Hồi phục thể lực theo thời gian
        if (stamina < maxStamina) {
            stamina += 5.0f * delta; // Hồi 5 stamina mỗi giây
            if (stamina > maxStamina) {
                stamina = maxStamina;
            }
        }
    }

    public int getStamina() {
        return (int) stamina;
    }

    public void reduceStamina(int amount) {
        this.stamina -= amount;
        if (this.stamina < 0) this.stamina = 0;
    }

    public String getName() {
        return name;
    }
}
