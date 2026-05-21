package hust.hedspi.oop.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.hedspi.oop.game.utils.TimeCondition;
import hust.hedspi.oop.game.managers.GameManager;
import hust.hedspi.oop.game.managers.ScreenManager;
import hust.hedspi.oop.game.screens.MinigameScreen;
import hust.hedspi.oop.game.minigames.IMinigameStrategy;

/**
 * TriggerZone là vùng va chạm ẩn (Hitbox).
 * Nó implement IInteractable để người chơi có thể nhấn [E] tương tác khi đứng đè lên.
 */
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

    @Override
    public void onInteract(Cat player) {
        // Kiểm tra xem có điều kiện thời gian không và thời gian có hợp lệ không
        if (timeCondition != null && !timeCondition.isCurrentlyValid()) {
            System.out.println("TriggerZone [" + zoneName + "]: Sự kiện chưa mở vào giờ này.");
            // TODO: Bắn thông báo UI lên màn hình "Hãy quay lại sau!"
            return;
        }

        // Nếu hợp lệ và có Minigame được liên kết, mở Minigame
        if (linkedMinigame != null) {
            System.out.println("TriggerZone [" + zoneName + "]: Kích hoạt Minigame!");
            ScreenManager.getInstance().pushScreen(new MinigameScreen(linkedMinigame));
        } else {
            System.out.println("TriggerZone [" + zoneName + "]: Tương tác thành công (Story Event)!");
            // TODO: Kích hoạt thoại hoặc sự kiện cốt truyện
        }
    }

    @Override
    public void update(float dt) {
        // TriggerZone là vật thể tĩnh, ẩn, thường không cần update logic chuyển động
    }

    @Override
    public void render(SpriteBatch batch) {
        // TriggerZone là vô hình (Invisible), KHÔNG render gì cả.
        // Chỉ vẽ khi dùng chức năng Debug (sử dụng ShapeRenderer thay vì SpriteBatch).
    }
}
