package game.ui;

import game.entity.Player;
import game.entity.MobileEnemy;
import game.entity.StationaryEnemy;
import game.core.Direction;
import game.map.Map;
import game.map.Position;
import game.reward.BasicReward;
import game.reward.BonusReward;
import game.reward.FinalReward;
import javafx.application.Platform;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SpriteManager class.
 * Note: Requires JavaFX toolkit initialization.
 */
public class SpriteManagerTest {
    
    @BeforeAll
    static void initJavaFX() {
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {});
        }
    }
    
    @BeforeEach
    void setUp() {
        // Initialize sprite manager before each test
        SpriteManager.initialize();
    }
    
    @Test
    void testGetSprite_Player() {
        Player player = new Player(new Position(1, 1));
        Image sprite = SpriteManager.getSprite(player);
        assertNotNull(sprite, "Should return sprite for player");
    }
    
    @Test
    void testGetSprite_PlayerDirectional() {
        Map map = new Map(10, 10);
        Player player = new Player(new Position(1, 1));
        
        // Test left direction by moving left
        player.decideNext(map, Direction.LEFT);
        Image leftSprite = SpriteManager.getSprite(player);
        assertNotNull(leftSprite, "Should return sprite for player facing left");
        
        // Test right direction by moving right
        player.decideNext(map, Direction.RIGHT);
        Image rightSprite = SpriteManager.getSprite(player);
        assertNotNull(rightSprite, "Should return sprite for player facing right");
    }
    
    @Test
    void testGetSprite_MobileEnemy() {
        MobileEnemy enemy = new MobileEnemy("test", 1, new Position(1, 1), null);
        Image sprite = SpriteManager.getSprite(enemy);
        assertNotNull(sprite, "Should return sprite for mobile enemy");
    }
    
    @Test
    void testGetSprite_MobileEnemyDirectional() {
        Map map = new Map(10, 10);
        MobileEnemy enemy = new MobileEnemy("test", 1, new Position(1, 1), null);
        
        // Test left direction by moving left
        enemy.decideNext(map, Direction.LEFT);
        Image leftSprite = SpriteManager.getSprite(enemy);
        assertNotNull(leftSprite, "Should return sprite for enemy facing left");
        
        // Test right direction by moving right
        enemy.decideNext(map, Direction.RIGHT);
        Image rightSprite = SpriteManager.getSprite(enemy);
        assertNotNull(rightSprite, "Should return sprite for enemy facing right");
    }
    
    @Test
    void testGetSprite_StationaryEnemy() {
        StationaryEnemy enemy = new StationaryEnemy("spike", 1, new Position(1, 1));
        Image sprite = SpriteManager.getSprite(enemy);
        assertNotNull(sprite, "Should return sprite for stationary enemy");
    }
    
    @Test
    void testGetSprite_BasicReward() {
        BasicReward reward = new BasicReward(new Position(1, 1), 10);
        Image sprite = SpriteManager.getSprite(reward);
        assertNotNull(sprite, "Should return sprite for basic reward");
    }
    
    @Test
    void testGetSprite_BonusReward() {
        BonusReward reward = new BonusReward(new Position(1, 1), 50, 100, 50);
        Image sprite = SpriteManager.getSprite(reward);
        assertNotNull(sprite, "Should return sprite for bonus reward");
    }
    
    @Test
    void testGetSprite_FinalReward() {
        FinalReward reward = new FinalReward(new Position(1, 1), 100);
        Image sprite = SpriteManager.getSprite(reward);
        assertNotNull(sprite, "Should return sprite for final reward");
    }
    
    @Test
    void testGetSprite_Null() {
        Image sprite = SpriteManager.getSprite(null);
        assertNotNull(sprite, "Should return default sprite for null entity");
    }
    
    @Test
    void testGetTileSprite() {
        assertNotNull(SpriteManager.getTileSprite("wall"));
        assertNotNull(SpriteManager.getTileSprite("entry"));
        assertNotNull(SpriteManager.getTileSprite("exit"));
        assertNotNull(SpriteManager.getTileSprite("door"));
        assertNotNull(SpriteManager.getTileSprite("door_open"));
        assertNotNull(SpriteManager.getTileSprite("floor"));
        assertNotNull(SpriteManager.getTileSprite("default"));
    }
    
    @Test
    void testGetTileSprite_CaseInsensitive() {
        Image wall1 = SpriteManager.getTileSprite("WALL");
        Image wall2 = SpriteManager.getTileSprite("wall");
        assertNotNull(wall1);
        assertNotNull(wall2);
    }
    
    @Test
    void testGetTileSprite_Unknown() {
        Image sprite = SpriteManager.getTileSprite("unknown_type");
        assertNotNull(sprite, "Should return default sprite for unknown tile type");
    }
}

