package hust.hedspi.oop.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import hust.hedspi.oop.game.entities.Cat;
import hust.hedspi.oop.game.managers.MapManager;

public class RunState implements ICatState {

    @Override
    public void enter(Cat cat) {
    }

    @Override
    public void update(Cat cat, float dt) {
        boolean isMoving = false;
        float x = cat.getX();
        float y = cat.getY();
        float speed = cat.getSpeed();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) { y += speed * dt; isMoving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { y -= speed * dt; isMoving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { 
            x -= speed * dt; 
            isMoving = true;
            cat.setFacingRight(false);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { 
            x += speed * dt; 
            isMoving = true;
            cat.setFacingRight(true);
        }

        // Giới hạn (Clamp) nhân vật không chạy ra khỏi viền Map
        float mapWidth = MapManager.getInstance().getMapPixelWidth();
        float mapHeight = MapManager.getInstance().getMapPixelHeight();
        
        x = MathUtils.clamp(x, 0, mapWidth - cat.getWidth());
        y = MathUtils.clamp(y, 0, mapHeight - cat.getHeight());

        cat.setPosition(x, y);

        if (!isMoving) {
            cat.changeState(new IdleState());
        }
    }

    @Override
    public void render(Cat cat, SpriteBatch batch) {
        cat.renderAnimation(batch, "RUN", Gdx.graphics.getDeltaTime());
    }

    @Override
    public void exit(Cat cat) {
    }
}
