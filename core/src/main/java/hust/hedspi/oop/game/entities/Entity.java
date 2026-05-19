package hust.hedspi.oop.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Entity {
    protected float x, y;
    protected float width, height;
    protected Rectangle hitbox;

    public Entity(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hitbox = new Rectangle(x, y, width, height);
    }

    public abstract void update(float dt);
    public abstract void render(SpriteBatch batch);

    public float getX() { return x; }
    public float getY() { return y; }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.hitbox.setPosition(x, y);
    }

    public Rectangle getHitbox() { return hitbox; }
}
