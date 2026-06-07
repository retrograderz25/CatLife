package hust.hedspi.oop.game.skills;

import hust.hedspi.oop.game.entities.Cat;

public abstract class BaseSkill implements Skill {
    protected String name;
    protected float cooldown;
    protected float currentCooldown;

    public BaseSkill(String name, float cooldown, int staminaCost) {
        this.name = name;
        this.cooldown = cooldown;
        this.currentCooldown = 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }

    @Override
    public void update(float delta) {
        if (currentCooldown > 0) {
            currentCooldown -= delta;
        }
    }

    @Override
    public boolean canUse(Cat cat) {
        if (currentCooldown > 0) {
            System.out.println("[" + name + "] Kỹ năng đang hồi chiêu! (Còn " + String.format("%.1f", currentCooldown) + "s)");
            return false;
        }
        return true;
    }

    @Override
    public void use(Cat cat) {
        if (canUse(cat)) {
            performAction(cat);
            currentCooldown = cooldown;
        }
    }

    protected abstract void performAction(Cat cat);
}
