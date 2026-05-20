package hust.hedspi.oop.game.skills;

import hust.hedspi.oop.game.entities.StrayCat;

public interface Skill {
    String getName();
    float getCooldown();
    boolean canUse(StrayCat cat);
    void use(StrayCat cat);
    void update(float delta);
}
