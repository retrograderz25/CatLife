package hust.hedspi.oop.game.utils;

public class Constants {
    
    public static final String VIETNAMESE_CHARACTERS = 
        "aAàÀảẢãÃáÁạẠăĂằẰẳẲẵẴắẮặẶâÂầẦẩẨẫẪấẤậẬ" +
        "bBcCdDđĐeEèÈẻẺẽẼéÉẹẸêÊềỀểỂễỄếẾệỆ" +
        "fFgGhHiIìÌỉỈĩĨíÍịỊjJkKlLmMnNoOòÒỏỎõÕóÓọỌ" +
        "ôÔồỒổỔỗỖốỐộỘơƠờỜởỞỡỠớỚợỢpPqQrRsStTuU" +
        "ùÙủỦũŨúÚụỤưƯừỪửỬữỮứỨựỰvVwWxXyYỳỲỷỶỹỸýÝỵỴzZ" +
        "0123456789! @#$%^&*()_+-=[]{}|;':,./<>?\"\\ ";

    
    public static final float REAL_SECONDS_PER_IN_GAME_MINUTE = 0.2f; 
    public static final int START_HOUR = 6;
    public static final int START_MINUTE = 0;
    
    
    
    
    public static final int MAX_HP = 100;

    public static final float BASE_SPEED = 150f;
    public static final int BASE_ATTACK_POWER = 10;
    public static final int LOW_HP_THRESHOLD = 30; 

    
    public static final int MINIGAME_WIN_HP_REWARD = 20;

    public static final int MINIGAME_LOSE_HP_PENALTY = 20;
    
    
    public static final float HUD_FONT_SIZE = 1.0f;
    
    
    public static final int VIRTUAL_WIDTH = 1280;
    public static final int VIRTUAL_HEIGHT = 720;

    
    public static final float CAO_MONG_DURATION          = 30f;
    public static final int   CAO_MONG_WIN_THRESHOLD     = 45;
    public static final float CAO_MONG_HAND_SHOW_TIME    = 0.3f;  
    public static final float CAO_MONG_SPRITE_SCALE      = 7f;    
    public static final float CAO_MONG_HAND_SCALE        = 2f;    

    
    public static final float THOAT_CONG_DURATION        = 45f;   
    public static final int   THOAT_CONG_SPAWN_AREA      = 190;   
    public static final int   THOAT_CONG_GOAL_AREA       = 150;   
    public static final float THOAT_CONG_CAT_SPEED       = 100f;  
    public static final float THOAT_CONG_CAT_RUN_SPEED   = 180f;  
    public static final float THOAT_CONG_MAZE_SCALE       = 1.5f;  
    public static final float THOAT_CONG_SPRITE_SCALE    = 1.0f;  
    public static final float THOAT_CONG_DARK_FADE       = 1.2f;  

    
    public static final float BATH_DURATION         = 30f;   
    public static final int   BATH_WIN_THRESHOLD    = 60;    
    public static final float BATH_BUBBLE_SPEED     = 50f;   
    public static final float BATH_SPAWN_INTERVAL   = 0.4f;  
    public static final int   BATH_BUBBLE_SCALE     = 2;     
    public static final float BATH_CURSOR_SCALE     = 1.5f;  

    
    public static final int   TTT_QUESTION_COUNT    = 5;     
    public static final float TTT_QUESTION_DURATION = 5f;    
    public static final int   TTT_OPTIONS_PER_Q     = 3;     
    public static final float TTT_FEEDBACK_DURATION = 0.5f;  
    public static final int   TTT_CARD_PADDING      = 10;    
}
