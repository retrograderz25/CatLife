package hust.hedspi.oop.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.I18NBundle;
import hust.hedspi.oop.game.utils.Constants;
import java.util.Locale;

public class ResourceManager {
    private static ResourceManager instance;
    
    
    public BitmapFont dialogFont;
    public BitmapFont nameFont;
    public BitmapFont hudFont;

    
    private I18NBundle bundle;

    private ResourceManager() {
        
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
        
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Arimo-Regular.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        
        parameter.characters = Constants.VIETNAMESE_CHARACTERS;

        
        
        parameter.minFilter = TextureFilter.Nearest;
        parameter.magFilter = TextureFilter.Nearest;
        
        
        parameter.size = 18; 
        parameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        parameter.borderWidth = 0;
        dialogFont = generator.generateFont(parameter);

        
        parameter.size = 22;
        parameter.color = com.badlogic.gdx.graphics.Color.GOLD;
        parameter.borderWidth = 1.5f; 
        parameter.borderColor = com.badlogic.gdx.graphics.Color.BLACK;
        nameFont = generator.generateFont(parameter);

        
        parameter.size = 30;
        parameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        parameter.borderWidth = 1;
        parameter.shadowOffsetX = 2; 
        parameter.shadowOffsetY = 2; 
        parameter.shadowColor = new com.badlogic.gdx.graphics.Color(0, 0, 0, 0.7f);
        hudFont = generator.generateFont(parameter);

        
        generator.dispose(); 
    }

    public void dispose() {
        if (dialogFont != null) dialogFont.dispose();
        if (nameFont != null) nameFont.dispose();
        if (hudFont != null) hudFont.dispose();
    }
}
