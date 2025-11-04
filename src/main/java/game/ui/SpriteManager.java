package game.ui;


import java.util.HashMap;
import java.util.Map;

import game.entity.Enemy;
import game.entity.Player;
import game.reward.BasicReward;
import game.reward.BonusReward;
import javafx.scene.image.Image;

public class SpriteManager {
    private static final Map<Class<?>, Image> spriteCache = new HashMap<>();
    private static Image wall, entry, exit, defaultSprite;

    public static void initialize(){
        // Entities
        spriteCache.put(Player.class, ResourceLoader.loadImage(GameConfig.SPRITES_PATH + "player.png"));
        spriteCache.put(BasicReward.class, ResourceLoader.loadImage(GameConfig.SPRITES_PATH + "reward_basic.png"));
        spriteCache.put(BonusReward.class, ResourceLoader.loadImage(GameConfig.SPRITES_PATH + "reward_bonus.png"));
        spriteCache.put(Enemy.class, ResourceLoader.loadImage(GameConfig.SPRITES_PATH + "enemy.png"));

        // Tiles
        wall = ResourceLoader.loadImage(GameConfig.SPRITES_PATH+"tile_wall.png");
        entry = ResourceLoader.loadImage(GameConfig.SPRITES_PATH+"tile_entry.png");
        exit = ResourceLoader.loadImage(GameConfig.SPRITES_PATH+"tile_exit.png");

        // Fallback
        defaultSprite = ResourceLoader.loadImage(GameConfig.SPRITES_PATH+"default.png");
    }

    public static Image getSprite(Object entity){
        if (entity == null) return defaultSprite;
        return spriteCache.getOrDefault(entity.getClass(), defaultSprite);
    }

    public static Image getTileSprite(String type){
        return switch (type){
            case "wall" -> wall;
            case "entry" -> entry;
            case "exit" -> exit;
            default -> defaultSprite;
        };
    }
}
