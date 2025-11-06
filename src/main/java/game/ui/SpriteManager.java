package game.ui;

import java.util.HashMap;
import java.util.Map;

import game.entity.Entity;
import game.entity.Enemy;
import game.entity.MobileEnemy;
import game.entity.Player;
import game.entity.StationaryEnemy;
import game.reward.BasicReward;
import game.reward.BonusReward;
import game.reward.FinalReward;
import game.reward.Reward;
import javafx.scene.image.Image;

/**
 * Manages sprite images for all game entities and map tiles.
 * <p>
 * Provides centralized access to images for rendering in {@link game.ui.GameCanvas}.
 * Maps entity classes to their corresponding images, and caches tile sprites for efficiency.
 * </p>
 * <p>
 * Before using, {@link #initialize()} must be called once during game startup.
 * </p>
 */
public class SpriteManager {

    // -------------------------
    // Cache for entity sprites
    // -------------------------
    private static final Map<Class<? extends Entity>, Image> spriteCache = new HashMap<>();

    // -------------------------
    // Tile images
    // -------------------------
    private static Image wall;
    private static Image entry;
    private static Image exit;
    private static Image door;
    private static Image floor;
    private static Image defaultSprite;

    /**
     * Loads and caches all entity and tile sprites.
     * <p>
     * Must be called once before any calls to {@link #getSprite(Entity)} or {@link #getTileSprite(String)}.
     * </p>
     */
    public static void initialize() {
        // Entity sprites
        spriteCache.put(Player.class, ResourceLoader.loadImage(GameConfig.IMAGE_PLAYER));
        spriteCache.put(BasicReward.class, ResourceLoader.loadImage(GameConfig.IMAGE_REWARD_BASIC));
        spriteCache.put(BonusReward.class, ResourceLoader.loadImage(GameConfig.IMAGE_REWARD_BONUS));
        spriteCache.put(MobileEnemy.class, ResourceLoader.loadImage(GameConfig.IMAGE_ENEMY));
        spriteCache.put(Reward.class, ResourceLoader.loadImage(GameConfig.IMAGE_REWARD_FINAL));
        spriteCache.put(StationaryEnemy.class, ResourceLoader.loadImage(GameConfig.IMAGE_SPIKES));
        spriteCache.put(FinalReward.class, ResourceLoader.loadImage(GameConfig.IMAGE_REWARD_FINAL));


        // Tile sprites
        wall = ResourceLoader.loadImage(GameConfig.IMAGE_WALL);
        floor = ResourceLoader.loadImage(GameConfig.IMAGE_FLOOR);
        entry = ResourceLoader.loadImage(GameConfig.IMAGE_ENTRY);
        exit = ResourceLoader.loadImage(GameConfig.IMAGE_EXIT);

        // Fallback
        defaultSprite = ResourceLoader.loadImage(GameConfig.IMAGE_DEFAULT);
    }

    /**
     * Returns the sprite image for the given entity.
     * <p>
     * If no specific sprite exists for the entity class, returns the default sprite.
     * </p>
     *
     * @param entity the entity to retrieve a sprite for
     * @return the corresponding {@link Image}, or default if none exists
     */
    public static Image getSprite(Entity entity) {
        if (entity == null) return defaultSprite;
        return spriteCache.getOrDefault(entity.getClass(), defaultSprite);
    }

    /**
     * Returns the tile sprite for the given type string.
     * <p>
     * Supports "wall", "entry", "exit", "door", "floor". Any other string returns the default sprite.
     * </p>
     *
     * @param type the type of tile ("wall", "entry", "exit", "door", "floor")
     * @return the corresponding {@link Image}, or default if unknown
     */
    public static Image getTileSprite(String type) {
        return switch (type.toLowerCase()) {
            case "wall" -> wall;
            case "entry" -> entry;
            case "exit" -> exit;
            case "door" -> door;
            case "floor", "default" -> floor; // treats default as the floor as well, can be changed later
            default -> defaultSprite;
        };
    }
}
