package hust.hedspi.oop.game.skills;

import hust.hedspi.oop.game.entities.Cat;

public interface Skill {
    String getName();
    float getCooldown();
    boolean canUse(Cat cat);
    void use(Cat cat);
    void update(float delta);
}
