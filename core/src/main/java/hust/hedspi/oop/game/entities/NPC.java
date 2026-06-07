package hust.hedspi.oop.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;





public class NPC extends Cat {
    
    private String npcName;

    public NPC(float x, float y, float width, float height, CatColor color, String npcName) {
        super(x, y, width, height, color);
        this.npcName = npcName;
    }

    @Override
    public void applyPassiveSkill(float dt) {
        
    }

    @Override
    public void update(float dt) {
        
        super.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        
        renderAnimation(batch, "IDLE", com.badlogic.gdx.Gdx.graphics.getDeltaTime());
    }

    public String getNpcName() {
        return npcName;
    }
}
