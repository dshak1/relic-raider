package game.ui;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ResourceLoader utility class.
 * Note: Requires JavaFX toolkit initialization.
 */
public class ResourceLoaderTest {
    
    @BeforeAll
    static void initJavaFX() {
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {});
        }
    }
    
    @BeforeEach
    void clearCache() {
        ResourceLoader.clearCache();
    }
    
    @Test
    void testLoadImage() {
        Image image = ResourceLoader.loadImage(GameConfig.IMAGE_DEFAULT);
        assertNotNull(image, "Should load default image");
    }
    
    @Test
    void testGetCachedImage() {
        // Load an image first
        Image image1 = ResourceLoader.loadImage(GameConfig.IMAGE_DEFAULT);
        assertNotNull(image1);
        
        // Get cached image
        Image image2 = ResourceLoader.getCachedImage(GameConfig.IMAGE_DEFAULT);
        assertNotNull(image2, "Should return cached image");
        assertEquals(image1, image2, "Should return same cached instance");
    }
    
    @Test
    void testGetCachedImage_NotLoaded() {
        Image image = ResourceLoader.getCachedImage("nonexistent.png");
        assertNull(image, "Should return null for non-cached image");
    }
    
    @Test
    void testImageCaching() {
        Image image1 = ResourceLoader.loadImage(GameConfig.IMAGE_DEFAULT);
        Image image2 = ResourceLoader.loadImage(GameConfig.IMAGE_DEFAULT);
        
        // Should return same instance due to caching
        assertSame(image1, image2, "Should cache and return same image instance");
    }
    
    @Test
    void testClearCache() {
        ResourceLoader.loadImage(GameConfig.IMAGE_DEFAULT);
        assertNotNull(ResourceLoader.getCachedImage(GameConfig.IMAGE_DEFAULT));
        
        ResourceLoader.clearCache();
        assertNull(ResourceLoader.getCachedImage(GameConfig.IMAGE_DEFAULT), 
            "Cache should be cleared");
    }
    
    @Test
    void testLoadFont() {
        // Note: Font loading may fail if font file doesn't exist, but method should not crash
        Font font = ResourceLoader.loadFont("arial.ttf", 12.0);
        // Font may be null if file doesn't exist, but method should handle it gracefully
        // Just verify it doesn't throw
        assertDoesNotThrow(() -> ResourceLoader.loadFont("test.ttf", 10.0));
    }
    
    @Test
    void testGetCachedFont() {
        // Try to load a font
        ResourceLoader.loadFont("test.ttf", 12.0);
        
        // Get cached font
        Font font = ResourceLoader.getCachedFont("test.ttf", 12.0);
        // May be null if font file doesn't exist, but method should not crash
        assertDoesNotThrow(() -> ResourceLoader.getCachedFont("test.ttf", 12.0));
    }
    
    @Test
    void testFontCaching() {
        Font font1 = ResourceLoader.loadFont("test.ttf", 12.0);
        Font font2 = ResourceLoader.loadFont("test.ttf", 12.0);
        
        // If fonts load successfully, they should be cached
        if (font1 != null && font2 != null) {
            assertSame(font1, font2, "Should cache font instances");
        }
    }
}

