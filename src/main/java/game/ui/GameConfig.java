package game.ui;

import java.awt.event.KeyEvent;

/**
 * Contains all game-wide constants including display settings, timing parameters,
 * and input key bindings.
 * 
 * This class serves as a single source of truth for configuration values
 * used throughout the game, making it easy to adjust gameplay parameters
 * and maintain consistency across components.
 */
public class GameConfig {
    
    // ========================
    // DISPLAY CONFIGURATION
    // ========================
    
    /**
     * Size of each tile in pixels (width and height).
     * All game entities are rendered on a grid where each cell is TILE_SIZE × TILE_SIZE pixels.
     * Based on Phase 1 design specification: 16×16 pixel tiles for pixel art.
     */
    public static final int TILE_SIZE = 16;
    
    /**
     * Number of tiles horizontally on the game board.
     */
    public static final int BOARD_WIDTH_TILES = 40;
    
    /**
     * Number of tiles vertically on the game board.
     */
    public static final int BOARD_HEIGHT_TILES = 30;
    
    /**
     * Height of the HUD (Heads-Up Display) in pixels.
     * The HUD displays score and time elapsed above the game board.
     */
    public static final int HUD_HEIGHT = 50;
    
    /**
     * Total window width in pixels.
     */
    public static final int WINDOW_WIDTH = BOARD_WIDTH_TILES * TILE_SIZE;
    
    /**
     * Total window height in pixels.
     */
    public static final int WINDOW_HEIGHT = (BOARD_HEIGHT_TILES * TILE_SIZE) + HUD_HEIGHT; // 
    
    /**
     * Title displayed in the game window title bar.
     */
    public static final String GAME_TITLE = "Relic Raider";
    
    
    // ========================
    // TIMING CONFIGURATION
    // ========================
    
    /**
     * Target frame rate in frames per second (FPS).
     */
    public static final int FRAME_RATE = 60;
    
    /**
     * Game tick rate (game logic updates per second).
     * This determines how often entities move and game state updates.
     */
    public static final int GAME_TICK_RATE = 10;
    
    /**
     * Milliseconds between each game tick.
     */
    public static final long TICK_DURATION_MS = 1000L / GAME_TICK_RATE;
    
    /**
     * Duration (in ticks) that bonus rewards remain visible before disappearing.
     */
    public static final int BONUS_REWARD_DURATION_TICKS = 10;
    
    
    // ========================
    // INPUT KEY BINDINGS
    // ========================
    
    /**
     * Key code for moving the player character up.
     * Default: UP ARROW key
     */
    public static final int KEY_UP = KeyEvent.VK_UP;
    
    /**
     * Key code for moving the player character down.
     * Default: DOWN ARROW key
     */
    public static final int KEY_DOWN = KeyEvent.VK_DOWN;
    
    /**
     * Key code for moving the player character left.
     * Default: LEFT ARROW key
     */
    public static final int KEY_LEFT = KeyEvent.VK_LEFT;
    
    /**
     * Key code for moving the player character right.
     * Default: RIGHT ARROW key
     */
    public static final int KEY_RIGHT = KeyEvent.VK_RIGHT;
    
    /**
     * Key code for pausing the game.
     * Default: P key
     */
    public static final int KEY_PAUSE = KeyEvent.VK_P;
    
    /**
     * Key code for restarting the game.
     * Default: R key
     */
    public static final int KEY_RESTART = KeyEvent.VK_R;
    
    /**
     * Key code for returning to main menu.
     * Default: ESC key
     */
    public static final int KEY_MENU = KeyEvent.VK_ESCAPE;
    
    
    // ========================
    // GAMEPLAY CONFIGURATION
    // ========================
    
    /**
     * Points awarded when collecting a regular reward (coin).
     * Based on Phase 1 spec: "+10 points each time they are collected"
     */
    public static final int REGULAR_REWARD_VALUE = 10;
    
    /**
     * Points awarded when collecting a bonus reward (totem).
     * Based on Phase 1 spec: "score goes up by 50 points each time collected"
     */
    public static final int BONUS_REWARD_VALUE = 50;
    
    /**
     * Points deducted when stepping on a punishment (spike trap).
     * Based on Phase 1 spec: "reduce player's score by 20 points when in contact"
     */
    public static final int SPIKE_TRAP_PENALTY = 20;
    
    /**
     * Initial score at game start.
     */
    public static final int INITIAL_SCORE = 0;
    
    /**
     * Minimum score threshold. If score drops below this, player loses.
     * Based on Phase 1 spec: "If the player's score becomes negative, the player loses"
     */
    public static final int MIN_SCORE_THRESHOLD = 0;
    
    
    // ========================
    // ASSET CONFIGURATION
    // ========================
    
    /**
     * Base directory path for game assets.
     * Following Maven standard: src/main/resources
     */
    public static final String ASSETS_PATH = "/assets/";
    
    /**
     * Directory path for sprite images.
     */
    public static final String SPRITES_PATH = ASSETS_PATH + "sprites/";

    /** Path to the player sprite image. */
    public static final String IMAGE_PLAYER = SPRITES_PATH + "player.png";

    /** Path to the enemy sprite image. */
    public static final String IMAGE_ENEMY = SPRITES_PATH + "enemy.png";

    /** Path to the basic reward (coin) sprite image. */
    public static final String IMAGE_REWARD_BASIC = SPRITES_PATH + "reward_basic.png";

    /** Path to the bonus reward (totem) sprite image. */
    public static final String IMAGE_REWARD_BONUS = SPRITES_PATH + "reward_bonus.png";

    /** Path to the wall tile sprite image. */
    public static final String IMAGE_WALL = SPRITES_PATH + "tile_wall.png";

    /** Path to the entry tile sprite image (level start). */
    public static final String IMAGE_ENTRY = SPRITES_PATH + "tile_entry.png";

    /** Path to the exit tile sprite image (level goal). */
    public static final String IMAGE_EXIT = SPRITES_PATH + "tile_exit.png";

    /** Path to the default placeholder sprite image, used when no other sprite is available. */
    public static final String IMAGE_DEFAULT = SPRITES_PATH + "default.png";
    
    /**
     * Directory path for fonts.
     */
    public static final String FONTS_PATH = ASSETS_PATH + "fonts/";
    
    
    // ========================
    // UTILITY METHODS
    // ========================
    
    /**
     * Converts pixel coordinates to tile coordinates.
     * 
     * @param pixels the pixel position
     * @return the corresponding tile index
     */
    public static int pixelsToTiles(int pixels) {
        return pixels / TILE_SIZE;
    }
    
    /**
     * Converts tile coordinates to pixel coordinates.
     * 
     * @param tiles the tile index
     * @return the corresponding pixel position (top-left corner of tile)
     */
    public static int tilesToPixels(int tiles) {
        return tiles * TILE_SIZE;
    }
    
    /**
     * Checks if given tile coordinates are within the game board bounds.
     * 
     * @param tileX the x-coordinate in tiles
     * @param tileY the y-coordinate in tiles
     * @return true if coordinates are within bounds, false otherwise
     */
    public static boolean isWithinBounds(int tileX, int tileY) {
        return tileX >= 0 && tileX < BOARD_WIDTH_TILES 
            && tileY >= 0 && tileY < BOARD_HEIGHT_TILES;
    }
    
    /**
     * Returns the key name as a readable string for UI display.
     * 
     * @param keyCode the KeyEvent constant
     * @return human-readable key name
     */
    public static String getKeyName(int keyCode) {
        return KeyEvent.getKeyText(keyCode);
    }
    
    /**
     * Validates the current configuration for logical consistency.
     * Useful for debugging configuration issues during development.
     * 
     * @return true if configuration is valid, false otherwise
     */
    public static boolean validateConfiguration() {
        boolean valid = true;
        
        // Check positive dimensions
        if (TILE_SIZE <= 0 || BOARD_WIDTH_TILES <= 0 || BOARD_HEIGHT_TILES <= 0) {
            System.err.println("GameConfig Error: Display dimensions must be positive");
            valid = false;
        }
        
        // Check reasonable frame rate
        if (FRAME_RATE <= 0) {
            System.err.println("GameConfig Error: Frame rate should be above 1 FPS");
            valid = false;
        }
        
        // Check tick rate makes sense
        if (GAME_TICK_RATE <= 0 || GAME_TICK_RATE > FRAME_RATE) {
            System.err.println("GameConfig Error: Tick rate should be between 1 and FRAME_RATE");
            valid = false;
        }
        
        // Check scoring values
        if (REGULAR_REWARD_VALUE <= 0 || BONUS_REWARD_VALUE <= 0) {
            System.err.println("GameConfig Error: Reward values must be positive");
            valid = false;
        }
        
        if (SPIKE_TRAP_PENALTY <= 0) {
            System.err.println("GameConfig Error: Penalty values must be positive");
            valid = false;
        }
        
        return valid;
    }
    
    
    // ========================
    // PRIVATE CONSTRUCTOR
    // ========================
    
    /**
     * Private constructor to prevent instantiation.
     * This class is designed to be used as a static utility/configuration holder only.
     */
    private GameConfig() {
        throw new AssertionError("GameConfig cannot be instantiated");
    }
}
