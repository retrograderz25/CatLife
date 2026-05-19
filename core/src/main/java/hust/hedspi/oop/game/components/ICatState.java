package hust.hedspi.oop.game.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.hedspi.oop.game.entities.Cat;

public interface ICatState {
    void enter(Cat cat);
    void update(Cat cat, float dt);
    void render(Cat cat, SpriteBatch batch);
    void exit(Cat cat);
}
