package hust.hedspi.oop.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import hust.hedspi.oop.game.components.ICatState;
import hust.hedspi.oop.game.components.IdleState;
import hust.hedspi.oop.game.utils.IObserver;
import hust.hedspi.oop.game.utils.ISubject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Cat extends Entity implements ISubject {

    public enum CatColor {
        WHITE("white"), BLACK("black"), BLUE("blue"), GRAY("gray"), GREEN("green"), ORANGE("orange"), PINK("pink"), PURPLE("purple"), RED("red");
        private final String folderName;
        CatColor(String folderName) { this.folderName = folderName; }
        public String getFolderName() { return folderName; }
    }

    private int hp;
    private int hunger;
    private int energy;

    private float speed;
    private int attackPower;
    
    protected Map<String, Animation<TextureRegion>> animations;
    protected Map<String, Texture> rawTextures;
    
    private float stateTimer;
    private boolean facingRight;
    private CatColor color;

    private ICatState currentState;
    private List<IObserver> observers = new ArrayList<>();

    public Cat(float x, float y, float width, float height, CatColor color) {
        super(x, y, width, height);
        this.color = color;
        this.hp = 100;
        this.hunger = 100;
        this.energy = 100;
        
        this.speed = 150f;
        this.attackPower = 10;
        
        this.stateTimer = 0f;
        this.facingRight = true;
        
        this.animations = new HashMap<>();
        this.rawTextures = new HashMap<>();
        loadAnimations();

        changeState(new IdleState());
    }

    private void loadAnimations() {
        String basePath = "images/HUD/Cat/" + color.getFolderName() + "/";
        loadSingleAnimation("IDLE", basePath + "IDLE.png", 0.2f);
        loadSingleAnimation("RUN", basePath + "RUN.png", 0.1f);
        loadSingleAnimation("WALK", basePath + "WALK.png", 0.15f);
        loadSingleAnimation("JUMP", basePath + "JUMP.png", 0.1f);
        loadSingleAnimation("HURT", basePath + "HURT.png", 0.1f);
        loadSingleAnimation("ATTACK", basePath + "ATTACK.png", 0.1f);
    }

    private void loadSingleAnimation(String animName, String filePath, float frameDuration) {
        if (!Gdx.files.internal(filePath).exists()) return;

        Texture texture = new Texture(Gdx.files.internal(filePath));
        rawTextures.put(animName, texture);
        
        int frameHeight = texture.getHeight();
        int frameWidth = frameHeight; 
        int cols = texture.getWidth() / frameWidth;
        
        TextureRegion[][] tmp = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[cols];
        for (int i = 0; i < cols; i++) {
            frames[i] = tmp[0][i];
        }
        
        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        animations.put(animName, animation);
    }

    public void changeState(ICatState newState) {
        if (currentState != null) {
            currentState.exit(this);
        }
        currentState = newState;
        stateTimer = 0f; 
        if (currentState != null) {
            currentState.enter(this);
        }
    }

    public ICatState getCurrentState() {
        return currentState;
    }

    public boolean isFacingRight() { return facingRight; }
    public void setFacingRight(boolean facingRight) { this.facingRight = facingRight; }

    public abstract void applyPassiveSkill(float dt);

    @Override
    public void update(float dt) {
        applyPassiveSkill(dt);
        if (currentState != null) {
            currentState.update(this, dt);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (currentState != null) {
            currentState.render(this, batch);
        }
    }
    
    public void renderAnimation(SpriteBatch batch, String animName, float dt) {
        stateTimer += dt;
        Animation<TextureRegion> anim = animations.get(animName);
        if (anim != null) {
            TextureRegion currentFrame = anim.getKeyFrame(stateTimer, true);
            
            if ((!facingRight && !currentFrame.isFlipX()) || (facingRight && currentFrame.isFlipX())) {
                currentFrame.flip(true, false);
            }
            
            batch.setColor(Color.WHITE);
            // Giả sử vẽ to gấp đôi để nhìn rõ hơn
            batch.draw(currentFrame, x, y, width * 2, height * 2); 
        }
    }

    public void dispose() {
        for (Texture tex : rawTextures.values()) {
            tex.dispose();
        }
    }

    // Getters & Setters
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = Math.max(0, hp); notifyObservers(); }
    public void decreaseHp(int amount) { this.hp = Math.max(0, this.hp - amount); notifyObservers(); }
    public void increaseHp(int amount) { this.hp = Math.min(100, this.hp + amount); notifyObservers(); }

    public int getHunger() { return hunger; }
    public void decreaseHunger(int amount) { this.hunger = Math.max(0, this.hunger - amount); notifyObservers(); }
    public void increaseHunger(int amount) { this.hunger = Math.min(100, this.hunger + amount); notifyObservers(); }

    public int getEnergy() { return energy; }
    public void decreaseEnergy(int amount) { this.energy = Math.max(0, this.energy - amount); notifyObservers(); }
    public void increaseEnergy(int amount) { this.energy = Math.min(100, this.energy + amount); notifyObservers(); }

    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }

    public int getAttackPower() { return attackPower; }
    public void setAttackPower(int attackPower) { this.attackPower = attackPower; }

    @Override
    public void addObserver(IObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Object... args) {
        for (IObserver observer : observers) {
            observer.onNotify(args);
        }
    }
}
