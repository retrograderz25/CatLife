package hust.hedspi.oop.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    
    
    
    public static final String BGM_MENU    = "music/menu_bgm.mp3";
    public static final String BGM_DAY     = "music/day_bgm.mp3";
    public static final String BGM_NIGHT   = "music/night_bgm.mp3";
    public static final String BGM_ENDING  = "music/ending_bgm.mp3";

    
    public static final String BGM_MG_FUNNY1   = "music/minigame_funny1_bgm.mp3";
    public static final String BGM_MG_FUNNY2   = "music/minigame_funny2_bgm.mp3";
    public static final String BGM_MG_FUNNY3   = "music/minigame_funny3_bgm.mp3";
    public static final String BGM_MG_ESCAPE1  = "music/minigame_escape1_bgm.mp3";
    public static final String BGM_MG_ESCAPE2  = "music/minigame_escape2_bgm.mp3";
    public static final String BGM_MG_ESCAPE3  = "music/minigame_escape3_bgm.mp3";
    public static final String BGM_MG_FIGHT1   = "music/minigame_fight1_bgm.mp3";
    public static final String BGM_MG_FIGHT2   = "music/minigame_fight2_bgm.mp3";
    public static final String BGM_MG_BOSS     = "music/minigame_boss_bgm.mp3";
    public static final String BGM_MG_HIPHOP   = "music/hip_hop_bgm.mp3";
    public static final String BGM_MG_BONGBONG = "music/minigame_bongbong_bgm.mp3";

    
    
    
    public static final String AMB_DAY   = "sounds/Env_Day_City.mp3";
    public static final String AMB_NIGHT = "sounds/Env_Night_Alley.mp3";

    
    
    
    
    public static final String SFX_CAT_MEOW_NORMAL  = "sounds/Cat_Meow_Normal.mp3";
    public static final String SFX_CAT_MEOW_PURR    = "sounds/Cat_Meow_Cute_Purr.mp3";
    public static final String SFX_CAT_HISS          = "sounds/Cat_Hiss_Angry.mp3";
    public static final String SFX_CAT_HURT          = "sounds/Cat_Hurt_Screech.mp3";
    public static final String SFX_CAT_FOOTSTEPS     = "sounds/Cat_Footsteps.mp3";
    public static final String SFX_CAT_SCRATCH       = "sounds/Cat_Scratch_Object.mp3";
    
    public static final String SFX_UI_HOVER          = "sounds/UI_Hover_Select.mp3";
    public static final String SFX_UI_CONFIRM        = "sounds/UI_Click_Confirm.mp3";
    public static final String SFX_UI_CANCEL         = "sounds/UI_Cancel_Back.mp3";
    public static final String SFX_UI_DIALOGUE_BLIP  = "sounds/UI_Dialogue_Blip.mp3";
    
    public static final String SFX_ENV_MOTORBIKE     = "sounds/Env_Motorbike_Loud.mp3";
    public static final String SFX_ENV_DOG_BARKING   = "sounds/Env_Dog_Barking.mp3";
    
    public static final String SFX_FIGHT_PUNCH       = "sounds/Fight_Punch_Slap.mp3";
    public static final String SFX_FIGHT_MISS        = "sounds/Fight_Whoosh_Miss.mp3";
    public static final String SFX_FIGHT_HEAVY       = "sounds/Fight_Impact_Heavy.mp3";
    
    public static final String SFX_HIPHOP_SCRATCH    = "sounds/Hiphop_Record_Scratch.mp3";
    public static final String SFX_DETECTIVE_CLUE    = "sounds/Detective_Clue_Found.mp3";
    public static final String SFX_BATH_BUBBLE_POP   = "sounds/Bath_Bubble_Pop.mp3";
    public static final String SFX_BATH_SPLASH       = "sounds/Bath_Water_Splash.mp3";
    public static final String SFX_SEWER_RAT         = "sounds/Sewer_Rat_Squeak.mp3";
    public static final String SFX_ESCAPE_RUN        = "sounds/Escape_Run_Tile.mp3";
    public static final String SFX_VET_SYRINGE       = "sounds/Vet_Syringe_Flick.mp3";
    public static final String SFX_CAGE_RATTLE       = "sounds/Cage_Rattle.mp3";
    public static final String SFX_DANGER_ALERT      = "sounds/Danger_Alert_JumpScare.mp3";

    
    
    
    private static SoundManager instance;
    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }
    private SoundManager() {}

    
    
    
    private Music currentBGM;
    private String currentBGMPath;
    private float  bgmVolume = 0.6f;

    public void playBGM(String path) {
        if (path == null) return;
        if (path.equals(currentBGMPath) && currentBGM != null && currentBGM.isPlaying()) return;
        stopBGM();
        currentBGM = Gdx.audio.newMusic(Gdx.files.internal(path));
        currentBGM.setLooping(true);
        currentBGM.setVolume(bgmVolume);
        currentBGM.play();
        currentBGMPath = path;
    }
    public void stopBGM() {
        if (currentBGM != null) { currentBGM.stop(); currentBGM.dispose(); currentBGM = null; }
        currentBGMPath = null;
    }
    public void pauseBGM()  { if (currentBGM != null && currentBGM.isPlaying())  currentBGM.pause(); }
    public void resumeBGM() { if (currentBGM != null && !currentBGM.isPlaying()) currentBGM.play();  }
    public void setBGMVolume(float v) {
        bgmVolume = Math.max(0f, Math.min(1f, v));
        if (currentBGM != null) currentBGM.setVolume(bgmVolume);
    }

    
    
    
    private Music currentAmbient;
    private String currentAmbientPath;
    private float  ambientVolume = 0.25f;

    public void playAmbient(String path) {
        if (path == null) return;
        if (path.equals(currentAmbientPath) && currentAmbient != null && currentAmbient.isPlaying()) return;
        stopAmbient();
        currentAmbient = Gdx.audio.newMusic(Gdx.files.internal(path));
        currentAmbient.setLooping(true);
        currentAmbient.setVolume(ambientVolume);
        currentAmbient.play();
        currentAmbientPath = path;
    }
    public void stopAmbient() {
        if (currentAmbient != null) { currentAmbient.stop(); currentAmbient.dispose(); currentAmbient = null; }
        currentAmbientPath = null;
    }

    
    
    
    private final Map<String, Sound> sfxCache = new HashMap<>();
    private float sfxVolume = 0.8f;

    
    public void playSFX(String path) {
        playSFX(path, sfxVolume);
    }

    
    public void playSFX(String path, float volume) {
        if (path == null) return;
        Sound s = sfxCache.get(path);
        if (s == null) {
            s = Gdx.audio.newSound(Gdx.files.internal(path));
            sfxCache.put(path, s);
        }
        s.play(Math.max(0f, Math.min(1f, volume)));
    }

    



    private final Map<String, Float> sfxTimers = new HashMap<>();
    public void playSFXThrottled(String path, float minInterval) {
        Float last = sfxTimers.get(path);
        float now  = (System.currentTimeMillis() / 1000f);
        if (last == null || (now - last) >= minInterval) {
            playSFX(path);
            sfxTimers.put(path, now);
        }
    }

    public void setSFXVolume(float v) { sfxVolume = Math.max(0f, Math.min(1f, v)); }
    public float getSFXVolume()       { return sfxVolume; }

    
    
    
    public void dispose() {
        stopBGM();
        stopAmbient();
        for (Sound s : sfxCache.values()) s.dispose();
        sfxCache.clear();
        sfxTimers.clear();
    }
}
