package hust.hedspi.oop.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class SoundManager {
    public static final String BGM_MENU    = "music/menu_bgm.mp3";
    public static final String BGM_DAY     = "music/day_bgm.mp3";
    public static final String BGM_NIGHT   = "music/night_bgm.mp3";
    public static final String BGM_ENDING  = "music/ending_bgm.mp3";

    // Minigame BGMs
    public static final String BGM_MG_FUNNY1  = "music/minigame_funny1_bgm.mp3";   // CaoMong, PetBeg
    public static final String BGM_MG_FUNNY2  = "music/minigame_funny2_bgm.mp3";   // Bath, HipHop
    public static final String BGM_MG_FUNNY3  = "music/minigame_funny3_bgm.mp3";   // Detective, Rhythm
    public static final String BGM_MG_ESCAPE1 = "music/minigame_escape1_bgm.mp3";  // ThoatKhoiCong
    public static final String BGM_MG_ESCAPE2 = "music/minigame_escape2_bgm.mp3";  // TromMeo
    public static final String BGM_MG_ESCAPE3 = "music/minigame_escape3_bgm.mp3";  // ThoatKhoiLong, TronKimTiem
    public static final String BGM_MG_FIGHT1  = "music/minigame_fight1_bgm.mp3";   // CombatMinigame (gang/stray)
    public static final String BGM_MG_FIGHT2  = "music/minigame_fight2_bgm.mp3";   // CombatDon (1v1)
    public static final String BGM_MG_BOSS    = "music/minigame_boss_bgm.mp3";     // Boss fight

    private static SoundManager instance;

    private Music currentBGM;
    private String currentBGMPath;
    private float bgmVolume = 0.6f;

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    /** Play BGM, looping. Skips if the same file is already playing. */
    public void playBGM(String filePath) {
        if (filePath == null) return;
        if (filePath.equals(currentBGMPath) && currentBGM != null && currentBGM.isPlaying()) return;

        stopBGM();

        currentBGM = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        currentBGM.setLooping(true);
        currentBGM.setVolume(bgmVolume);
        currentBGM.play();
        currentBGMPath = filePath;
    }

    public void stopBGM() {
        if (currentBGM != null) {
            currentBGM.stop();
            currentBGM.dispose();
            currentBGM = null;
        }
        currentBGMPath = null;
    }

    public void pauseBGM() {
        if (currentBGM != null && currentBGM.isPlaying()) {
            currentBGM.pause();
        }
    }

    public void resumeBGM() {
        if (currentBGM != null && !currentBGM.isPlaying()) {
            currentBGM.play();
        }
    }

    public void setBGMVolume(float volume) {
        bgmVolume = Math.max(0f, Math.min(1f, volume));
        if (currentBGM != null) {
            currentBGM.setVolume(bgmVolume);
        }
    }

    public float getBGMVolume() {
        return bgmVolume;
    }

    public void dispose() {
        stopBGM();
    }
}
