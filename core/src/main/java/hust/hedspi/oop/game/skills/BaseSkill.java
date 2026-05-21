package hust.hedspi.oop.game.skills;

import hust.hedspi.oop.game.entities.StrayCat;

public abstract class BaseSkill implements Skill {
    protected String name;
    protected float cooldown;
    protected float currentCooldown;
    protected int staminaCost;

    public BaseSkill(String name, float cooldown, int staminaCost) {
        this.name = name;
        this.cooldown = cooldown;
        this.staminaCost = staminaCost;
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
    public boolean canUse(StrayCat cat) {
        if (currentCooldown > 0) {
            System.out.println("[" + name + "] Kỹ năng đang hồi chiêu! (Còn " + String.format("%.1f", currentCooldown) + "s)");
            return false;
        }
        if (cat.getStamina() < staminaCost) {
            System.out.println("[" + name + "] Không đủ thể lực! (Cần: " + staminaCost + ", Hiện có: " + cat.getStamina() + ")");
            return false;
        }
        return true;
    }

    @Override
    public void use(StrayCat cat) {
        if (canUse(cat)) {
            cat.reduceStamina(staminaCost);
            performAction(cat);
            currentCooldown = cooldown;
        }
    }

    protected abstract void performAction(StrayCat cat);
}
