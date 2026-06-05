package hust.hedspi.oop.game.utils;

public class Constants {
    // Chuỗi này chứa toàn bộ chữ cái Tiếng Việt (Viết hoa, viết thường, số và ký tự đặc biệt)
    public static final String VIETNAMESE_CHARACTERS = 
        "aAàÀảẢãÃáÁạẠăĂằẰẳẲẵẴắẮặẶâÂầẦẩẨẫẪấẤậẬ" +
        "bBcCdDđĐeEèÈẻẺẽẼéÉẹẸêÊềỀểỂễỄếẾệỆ" +
        "fFgGhHiIìÌỉỈĩĨíÍịỊjJkKlLmMnNoOòÒỏỎõÕóÓọỌ" +
        "ôÔồỒổỔỗỖốỐộỘơƠờỜởỞỡỠớỚợỢpPqQrRsStTuU" +
        "ùÙủỦũŨúÚụỤưƯừỪửỬữỮứỨựỰvVwWxXyYỳỲỷỶỹỸýÝỵỴzZ" +
        "0123456789! @#$%^&*()_+-=[]{}|;':,./<>?\"\\ ";

    // Thời gian
    public static final float REAL_SECONDS_PER_IN_GAME_MINUTE = 0.25f; 
    public static final int START_HOUR = 6;
    public static final int START_MINUTE = 0;
    
    // ==========================================
    // CÁC CHỈ SỐ SINH TỒN (GAME BALANCE STATS)
    // ==========================================
    public static final int MAX_HP = 100;
    public static final int MAX_HUNGER = 100;
    public static final int MAX_ENERGY = 100;

    public static final float BASE_SPEED = 150f;
    public static final int BASE_ATTACK_POWER = 10;
    public static final int LOW_HP_THRESHOLD = 30; // Mức máu thấp để kích hoạt nội tại

    // Tốc độ phục hồi Thể lực (Energy)
    public static final float STRAY_CAT_ENERGY_RECOVERY_RATE = 5.0f; // Giây thực tế để hồi 1 Energy (Chậm)
    public static final float HOUSE_CAT_ENERGY_RECOVERY_RATE = 0.2f; // Giây thực tế để hồi 1 Energy 
    public static final float SLEEP_ENERGY_RECOVERY_RATE = 1.0f;     // Giây thực tế để hồi SLEEP_ENERGY_RECOVERY_AMOUNT
    public static final int SLEEP_ENERGY_RECOVERY_AMOUNT = 5;

    // Phần thưởng / Hình phạt Minigame
    public static final int MINIGAME_WIN_HP_REWARD = 20;
    public static final int MINIGAME_WIN_ENERGY_REWARD = 20;
    public static final int MINIGAME_WIN_HUNGER_PENALTY = 10; // Trừ độ đói khi chơi xong (dù thắng)
    
    public static final int MINIGAME_LOSE_HP_PENALTY = 20;
    public static final int MINIGAME_LOSE_ENERGY_PENALTY = 20;
    public static final int MINIGAME_LOSE_HUNGER_PENALTY = 15; // Trừ độ đói khi chơi xong (thua mệt hơn)
    
    // UI
    public static final float HUD_FONT_SIZE = 1.0f;
    
    // Màn hình
    public static final int VIRTUAL_WIDTH = 1280;
    public static final int VIRTUAL_HEIGHT = 720;

    // Minigame: Cào Móng (DAILY_SCRATCH)
    public static final float CAO_MONG_DURATION          = 30f;
    public static final int   CAO_MONG_WIN_THRESHOLD     = 45;
    public static final float CAO_MONG_HAND_SHOW_TIME    = 0.3f;  // tổng thời gian 1 animation (vươn + giữ + rút)
    public static final float CAO_MONG_SPRITE_SCALE      = 7f;    // scale cho board và mũi tên
    public static final float CAO_MONG_HAND_SCALE        = 2f;    // scale bàn tay

    // Minigame: Thoát Khỏi Cống (DAILY_ESCAPE_SEWER)
    public static final float THOAT_CONG_DURATION        = 45f;   // giây
    public static final int   THOAT_CONG_SPAWN_AREA      = 190;   // pixel vùng spawn góc trên-trái
    public static final int   THOAT_CONG_GOAL_AREA       = 150;   // pixel vùng đích góc dưới-trái
    public static final float THOAT_CONG_CAT_SPEED       = 100f;  // pixel/giây (đi bộ)
    public static final float THOAT_CONG_CAT_RUN_SPEED   = 180f;  // pixel/giây (chạy – giữ Shift)
    public static final float THOAT_CONG_MAZE_SCALE       = 1.5f;  // Scale lớn hơn cho màn hình 1280x720
    public static final float THOAT_CONG_SPRITE_SCALE    = 1.0f;  // hệ số nhân lên trên mazeScale để tính kích thước mèo
    public static final float THOAT_CONG_DARK_FADE       = 1.2f;  // alpha/giây fade-in bóng tối

    // Minigame: Tắm Mèo (PET_BATH)
    public static final float BATH_DURATION         = 30f;   // giây
    public static final int   BATH_WIN_THRESHOLD    = 60;    // số bong bóng cần bấm để thắng
    public static final float BATH_BUBBLE_SPEED     = 50f;   // pixel/giây bong bóng bay lên
    public static final float BATH_SPAWN_INTERVAL   = 0.4f;  // giây giữa hai lần spawn bong bóng
    public static final int   BATH_BUBBLE_SCALE     = 2;     // scale bong bóng (24px → 48px)
    public static final float BATH_CURSOR_SCALE     = 1.5f;  // scale con trỏ chuột mèo

    // Minigame: Tìm Tiểu Tam (LOVE_DETECTIVE)
    public static final int   TTT_QUESTION_COUNT    = 5;     // số câu hỏi
    public static final float TTT_QUESTION_DURATION = 5f;    // giây mỗi câu
    public static final int   TTT_OPTIONS_PER_Q     = 3;     // 1 đúng + 2 sai
    public static final float TTT_FEEDBACK_DURATION = 0.5f;  // giây hiện flash sau khi chọn
    public static final int   TTT_CARD_PADDING      = 10;    // khoảng cách px giữa/xung quanh các card
}
