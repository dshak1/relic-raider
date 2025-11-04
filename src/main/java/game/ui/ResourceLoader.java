package game.ui;

import game.ui.GameConfig; //needed to get file paths later.
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javafx.scene.image.Image;
import javafx.scene.text.Font;

/**
 * ResourceLoader is a utility class responsible for loading and caching
 * game assets such as images and fonts. 
 * <p>
 * This ensures assets are only loaded once and reused throughout the game,
 * improving performance and consistency.
 * </p>
 */
public class ResourceLoader {
    
    /** Cache for loaded images keyed by filename */
    private static final Map<String, Image> imageCache = new HashMap<>();

    /** Cache for loaded fonts keyed by "filename_size" */
    private static final Map<String, Font> fontCache = new HashMap<>();

    /** Private constructor to prevent instantiation */
    private ResourceLoader(){}

    // ======================
    // IMAGE METHODS
    // ======================

    /**
     * Loads an image from the sprites directory and caches it.
     * 
     * @param fileName the image file name (e.g., "player.png")
     * @return the loaded Image object
     */
    public static Image loadImage(String fileName) {
        return imageCache.computeIfAbsent(fileName, name -> {
            String path = GameConfig.SPRITES_PATH + name;
            // Use getResourceAsStream for classpath resource
            return new Image(ResourceLoader.class.getResourceAsStream(path));
        });
    }



    /**
     * Returns a cached image if it exists, otherwise null.
     * 
     * @param fileName the image file name
     * @return the cached Image object or null if not loaded
     */
    public static Image getCachedImage(String fileName){
        return imageCache.get(fileName);
    }

    // ======================
    // FONT METHODS
    // ======================

    /**
     * Loads a font from the fonts directory at the given size and caches it.
     * 
     * @param fileName the font file name (e.g., "arial.ttf")
     * @param size the font size in points
     * @return the loaded Font object
     */
    public static Font loadFont(String fileName, double size){
        String key = fileName + "_" + size;
        return fontCache.computeIfAbsent(key, k -> {
            String path = GameConfig.FONTS_PATH + fileName;
            return Font.loadFont(path,size);
        });
    }

    /**
     * Returns a cached font if it exists, otherwise null.
     * 
     * @param fileName the font file name
     * @param size the font size
     * @return the cached Font object or null if not loaded
     */
    public static Font getCachedFont (String fileName, double size){
        String key = fileName + "_" + size;
        return fontCache.get(key);
    }

    /**
     * Clears all cached resources. Can be used if assets need to be reloaded.
     */
    public static void clearCache() {
        imageCache.clear();
        fontCache.clear();
    }
}
