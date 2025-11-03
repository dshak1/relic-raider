package game.ui;

import java.util.Map;
import java.util.HashMap;

import javafx.scene.image.Image;
import javafx.scene.text.Font;

public class ResourceLoader {
    
    private static final Map<String, Image> imageCache = new HashMap<>();

    private static final Map<String, Font> fontCache = new HashMap<>();

    private ResourceLoader(){}

    public static Image loadImage(String fileName){
        return imageCache.computeIfAbsent(fileName, name -> {
            String path = GameConfig.SPRITES_PATH + name;
            return new Image(path);
        });
    }

    public static Image getCachedImage(String fileName){
        return imageCache.get(fileName);
    }

    public static Font loadFont(String fileName, double size){
        String key = fileName + "_" + size;
        return fontCache.computeIfAbsent(key, k -> {
            String path = GameConfig.FONTS_PATH + fileName;
            return Font.loadFont(path,size);
        });
    }

    public static Font getCachedFont (String fileName, double size){
        String key = fileName + "_" + size;
        return fontCache.get(key);
    }

    public static void clearCache() {
        imageCache.clear();
        fontCache.clear();
    }
}
