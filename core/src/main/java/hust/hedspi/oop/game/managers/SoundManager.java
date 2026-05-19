package hust.hedspi.oop.game.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    private static SoundManager instance;
    
    private AssetManager assetManager;
    private Music currentBGM;
    private float bgmVolume = 1.0f;
    private float sfxVolume = 1.0f;

    private SoundManager() {
        assetManager = new AssetManager();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playBGM(String filePath) {
        // Implement logic using assetManager
        System.out.println("Playing BGM: " + filePath + " at volume " + bgmVolume);
    }

    public void stopBGM() {
        if (currentBGM != null) {
            currentBGM.stop();
        }
    }

    public void playSFX(String filePath) {
        // Implement logic using assetManager
        System.out.println("Playing SFX: " + filePath + " at volume " + sfxVolume);
    }

    public void setBGMVolume(float volume) {
        this.bgmVolume = Math.max(0f, Math.min(1f, volume));
        if (currentBGM != null) {
            currentBGM.setVolume(this.bgmVolume);
        }
    }

    public void setSFXVolume(float volume) {
        this.sfxVolume = Math.max(0f, Math.min(1f, volume));
    }
}
