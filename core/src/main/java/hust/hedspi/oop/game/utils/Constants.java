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
    public static final float REAL_SECONDS_PER_IN_GAME_MINUTE = 0.5f; // 1 real second = 2 game minutes => 0.5 real seconds = 1 game minute
    public static final int START_HOUR = 6;
    public static final int START_MINUTE = 0;
    
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
    public static final float CAO_MONG_HAND_SCALE        = 2f;  // scale bàn tay
}
