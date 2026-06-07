package hust.hedspi.oop.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * NPC class represents a non-player character cat.
 * It stays at a fixed position and displays an idle animation.
 */
public class NPC extends Cat {
    
    private String npcName;

    public NPC(float x, float y, float width, float height, CatColor color, String npcName) {
        super(x, y, width, height, color);
        this.npcName = npcName;
    }

    @Override
    public void applyPassiveSkill(float dt) {
        // NPCs don't usually have passive skills that affect stats like the player
    }

    @Override
    public void update(float dt) {
        // NPC logic - currently just staying idle
        super.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        // Render idle animation
        renderAnimation(batch, "IDLE", com.badlogic.gdx.Gdx.graphics.getDeltaTime());
    }

    public String getNpcName() {
        return npcName;
    }
}
