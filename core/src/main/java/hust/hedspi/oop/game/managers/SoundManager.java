package hust.hedspi.oop.game.managers;

// Placeholder cho SoundManager
public class SoundManager {
    private static SoundManager instance;

    private SoundManager() {
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playBgm(String path) {
        // TODO: Implement using libgdx Music
    }

    public void playSfx(String path) {
        // TODO: Implement using libgdx Sound
    }
}
