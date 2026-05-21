package hust.hedspi.oop.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.hedspi.oop.game.components.ICatState;
import hust.hedspi.oop.game.components.IdleState;

public abstract class Cat extends Entity {
    // Encapsulation: Dữ liệu được đóng gói private, chỉ có thể sửa đổi qua hàm
    private int hp;
    private int hunger;
    private int energy;

    // Các thuộc tính cơ bản thêm vào
    private float speed;
    private int attackPower;
    
    // Hình ảnh hiển thị tạm thời (Placeholder)
    protected Texture texture;

    // State Pattern: Quản lý hành vi của Mèo
    private ICatState currentState;

    public Cat(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.hp = 100;
        this.hunger = 100;
        this.energy = 100;
        
        this.speed = 150f;
        this.attackPower = 10;
        
        // Trạng thái khởi điểm
        changeState(new IdleState());
    }

    protected void createPlaceholderTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        this.texture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void changeState(ICatState newState) {
        if (currentState != null) {
            currentState.exit(this);
        }
        currentState = newState;
        if (currentState != null) {
            currentState.enter(this);
        }
    }

    public ICatState getCurrentState() {
        return currentState;
    }

    // Abstract method để ép buộc các class con phải định nghĩa nội tại riêng
    public abstract void applyPassiveSkill(float dt);

    @Override
    public void update(float dt) {
        applyPassiveSkill(dt); // Luôn áp dụng nội tại mỗi frame
        
        // Ủy quyền (Delegate) logic update cho State hiện tại
        if (currentState != null) {
            currentState.update(this, dt);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // Ủy quyền (Delegate) logic vẽ cho State hiện tại
        if (currentState != null) {
            currentState.render(this, batch);
        }
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    // --- Getters & Setters an toàn ---
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = Math.max(0, hp); }
    public void decreaseHp(int amount) { this.hp = Math.max(0, this.hp - amount); }
    public void increaseHp(int amount) { this.hp = Math.min(100, this.hp + amount); }

    public int getHunger() { return hunger; }
    public void decreaseHunger(int amount) { this.hunger = Math.max(0, this.hunger - amount); }
    public void increaseHunger(int amount) { this.hunger = Math.min(100, this.hunger + amount); }

    public int getEnergy() { return energy; }
    public void decreaseEnergy(int amount) { this.energy = Math.max(0, this.energy - amount); }
    public void increaseEnergy(int amount) { this.energy = Math.min(100, this.energy + amount); }

    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }

    public int getAttackPower() { return attackPower; }
    public void setAttackPower(int attackPower) { this.attackPower = attackPower; }

    public Texture getTexture() { return texture; }
}
