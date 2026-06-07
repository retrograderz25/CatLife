package hust.hedspi.oop.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.hedspi.oop.game.utils.TimeCondition;
import hust.hedspi.oop.game.managers.GameManager;
import hust.hedspi.oop.game.managers.ScreenManager;
import hust.hedspi.oop.game.screens.MinigameScreen;
import hust.hedspi.oop.game.minigames.IMinigameStrategy;





public class TriggerZone extends Entity implements IInteractable {
    private TimeCondition timeCondition;
    private IMinigameStrategy linkedMinigame;
    private String zoneName;

    public TriggerZone(float x, float y, float width, float height, String zoneName) {
        super(x, y, width, height);
        this.zoneName = zoneName;
    }

    public void setTimeCondition(TimeCondition condition) {
        this.timeCondition = condition;
    }

    public void setLinkedMinigame(IMinigameStrategy minigame) {
        this.linkedMinigame = minigame;
    }

    public String getZoneName() {
        return zoneName;
    }

    public boolean canTrigger() {
        
        if (timeCondition != null && !timeCondition.isCurrentlyValid()) {
            return false;
        }
        
        if (!hust.hedspi.oop.game.managers.StoryManager.getInstance().isZoneUnlocked(zoneName)) {
            return false;
        }
        return true;
    }

    @Override
    public void onInteract(Cat player) {
        
        if (linkedMinigame != null) {
            System.out.println("TriggerZone [" + zoneName + "]: Trigger Minigame!");
            ScreenManager.getInstance().pushScreen(new MinigameScreen(linkedMinigame));
        } else {
            System.out.println("TriggerZone [" + zoneName + "]: Story Event!");
        }
    }

    @Override
    public void update(float dt) {
        
    }

    @Override
    public void render(SpriteBatch batch) {
        
        
    }
}
