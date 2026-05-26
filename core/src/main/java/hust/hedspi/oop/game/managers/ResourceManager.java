package hust.hedspi.oop.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.I18NBundle;
import hust.hedspi.oop.game.utils.Constants;

public class ResourceManager {
    private static ResourceManager instance;
    
    // Khai báo các loại Font
    public BitmapFont dialogFont;
    public BitmapFont nameFont;
    public BitmapFont hudFont;

    // Bundle ngôn ngữ
    private I18NBundle bundle;

    private ResourceManager() {
        // Chỉ gọi generateFonts nếu ứng dụng LibGDX đã khởi tạo
        if (Gdx.files != null) {
            generateFonts();
            loadBundle();
        }
    }
    
    public static ResourceManager getInstance() {
        if(instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    public void initialize() {
        if (dialogFont == null) {
            generateFonts();
        }
        if (bundle == null) {
            loadBundle();
        }
    }

    private void loadBundle() {
        bundle = I18NBundle.createBundle(Gdx.files.internal("i18n/dialogues_vi"));
    }

    public I18NBundle getBundle() {
        if (bundle == null) {
            loadBundle();
        }
        return bundle;
    }

    private void generateFonts() {
        // Trỏ tới file font tải về để trong thư mục assets/fonts/
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/zpix.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        // 1. CHUẨN BỊ KÝ TỰ TIẾNG VIỆT
        parameter.characters = Constants.VIETNAMESE_CHARACTERS;

        // 2. BÍ QUYẾT CHO PIXEL ART: Tắt chế độ làm mượt (Anti-aliasing)
        // Nếu không có 2 dòng này, chữ sẽ bị nhòe mờ rất xấu!
        parameter.minFilter = TextureFilter.Nearest;
        parameter.magFilter = TextureFilter.Nearest;
        
        // 3. TẠO DIALOG FONT (Chữ trắng, không viền)
        parameter.size = 18; // Kích thước chữ
        parameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        parameter.borderWidth = 0;
        dialogFont = generator.generateFont(parameter);

        // 4. TẠO NAME FONT (Chữ màu, có viền đen để nổi bật)
        parameter.size = 22;
        parameter.color = com.badlogic.gdx.graphics.Color.GOLD;
        parameter.borderWidth = 1.5f; // Viền đen 1.5px
        parameter.borderColor = com.badlogic.gdx.graphics.Color.BLACK;
        nameFont = generator.generateFont(parameter);

        // 5. TẠO HUD FONT (To, có đổ bóng)
        parameter.size = 30;
        parameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        parameter.borderWidth = 1;
        parameter.shadowOffsetX = 2; // Đổ bóng sang phải 2px
        parameter.shadowOffsetY = 2; // Đổ bóng xuống 2px
        parameter.shadowColor = new com.badlogic.gdx.graphics.Color(0, 0, 0, 0.7f);
        hudFont = generator.generateFont(parameter);

        // Chống rò rỉ bộ nhớ (Memory Leak)
        generator.dispose(); 
    }

    public void dispose() {
        if (dialogFont != null) dialogFont.dispose();
        if (nameFont != null) nameFont.dispose();
        if (hudFont != null) hudFont.dispose();
    }
}
