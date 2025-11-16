package game.ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GameConfig utility class.
 */
public class GameConfigTest {
    
    @Test
    void testConstantsArePositive() {
        assertTrue(GameConfig.TILE_SIZE > 0);
        assertTrue(GameConfig.BOARD_WIDTH_TILES > 0);
        assertTrue(GameConfig.BOARD_HEIGHT_TILES > 0);
        assertTrue(GameConfig.FRAME_RATE > 0);
        assertTrue(GameConfig.GAME_TICK_RATE > 0);
        assertTrue(GameConfig.REGULAR_REWARD_VALUE > 0);
        assertTrue(GameConfig.BONUS_REWARD_VALUE > 0);
        assertTrue(GameConfig.FINAL_REWARD_VALUE > 0);
        assertTrue(GameConfig.SPIKE_TRAP_PENALTY > 0);
    }
    
    @Test
    void testPixelsToTiles() {
        assertEquals(0, GameConfig.pixelsToTiles(0));
        assertEquals(1, GameConfig.pixelsToTiles(16));
        assertEquals(2, GameConfig.pixelsToTiles(32));
        assertEquals(5, GameConfig.pixelsToTiles(80));
    }
    
    @Test
    void testTilesToPixels() {
        assertEquals(0, GameConfig.tilesToPixels(0));
        assertEquals(16, GameConfig.tilesToPixels(1));
        assertEquals(32, GameConfig.tilesToPixels(2));
        assertEquals(80, GameConfig.tilesToPixels(5));
    }
    
    @Test
    void testIsWithinBounds() {
        assertTrue(GameConfig.isWithinBounds(0, 0));
        assertTrue(GameConfig.isWithinBounds(
            GameConfig.BOARD_WIDTH_TILES - 1, 
            GameConfig.BOARD_HEIGHT_TILES - 1));
        assertFalse(GameConfig.isWithinBounds(-1, 0));
        assertFalse(GameConfig.isWithinBounds(0, -1));
        assertFalse(GameConfig.isWithinBounds(GameConfig.BOARD_WIDTH_TILES, 0));
        assertFalse(GameConfig.isWithinBounds(0, GameConfig.BOARD_HEIGHT_TILES));
    }
    
    @Test
    void testGetKeyName() {
        String upKey = GameConfig.getKeyName(GameConfig.KEY_UP);
        assertNotNull(upKey);
        assertFalse(upKey.isEmpty());
    }
    
    @Test
    void testValidateConfiguration() {
        assertTrue(GameConfig.validateConfiguration());
    }
    
    @Test
    void testWindowDimensions() {
        int expectedWidth = GameConfig.BOARD_WIDTH_TILES * GameConfig.TILE_SIZE;
        int expectedHeight = (GameConfig.BOARD_HEIGHT_TILES * GameConfig.TILE_SIZE) + GameConfig.HUD_HEIGHT;
        
        assertEquals(expectedWidth, GameConfig.WINDOW_WIDTH);
        assertEquals(expectedHeight, GameConfig.WINDOW_HEIGHT);
    }
    
    @Test
    void testTickDuration() {
        long expected = 1000L / GameConfig.GAME_TICK_RATE;
        assertEquals(expected, GameConfig.TICK_DURATION_MS);
    }
    
    @Test
    void testGameTitle() {
        assertNotNull(GameConfig.GAME_TITLE);
        assertFalse(GameConfig.GAME_TITLE.isEmpty());
    }
}

