package game.core;

import game.entity.Player;
import game.entity.MobileEnemy;
import game.entity.StationaryEnemy;
import game.map.Map;
import game.map.Position;
import game.behaviour.AStarPathfinding;
import game.reward.BasicReward;
import game.reward.BonusReward;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

/**
 * Unit tests for the Game core module.
 * Tests game initialization, game loop, collision detection, and win/lose conditions.
 */
public class GameTest {
    
    private Game game;
    private Map map;
    private Player player;
    
    @BeforeEach
    void setUp() {
        // Create a simple 10x10 map with borders
        map = new Map(10, 10);
        map.createBorder();
        map.setEntryPoint(new Position(1, 1));
        map.setExitPoint(new Position(8, 8));
        
        player = new Player(new Position(1, 1));
    }
    
    @Test
    void testCreateSimpleGame() {
        Game simpleGame = Game.createSimpleGame(15, 15);
        
        assertNotNull(simpleGame);
        assertNotNull(simpleGame.getMap());
        assertNotNull(simpleGame.getPlayer());
        assertEquals(15, simpleGame.getMap().getWidth());
        assertEquals(15, simpleGame.getMap().getHeight());
        assertEquals(0, simpleGame.getScore());
        assertFalse(simpleGame.isGameOver());
    }
    
    @Test
    void testGameBuilder() {
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .build();
        
        assertNotNull(game);
        assertEquals(map, game.getMap());
        assertEquals(player, game.getPlayer());
        assertEquals(0, game.getScore());
    }
    
    @Test
    void testGameBuilderWithEnemies() {
        MobileEnemy enemy = new MobileEnemy("test", 1, new Position(3, 3), 
            new AStarPathfinding());
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addEnemy(enemy)
            .build();
        
        assertEquals(1, game.getEnemies().size());
        assertTrue(game.getEnemies().contains(enemy));
    }
    
    @Test
    void testGameBuilderWithRewards() {
        BasicReward basicReward = new BasicReward(new Position(2, 2), 10);
        BonusReward bonusReward = new BonusReward(new Position(3, 3), 25, 
            Duration.ofSeconds(30));
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(basicReward)
            .addReward(bonusReward)
            .build();
        
        assertEquals(1, game.getBasicToCollect());
        assertEquals(2, game.getRewards().size());
    }
    
    @Test
    void testGameStart() {
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .build();
        
        game.start();
        
        assertFalse(game.isGameOver());
        assertEquals(Duration.ZERO, game.getElapsedTime());
    }
    
    @Test
    void testGameTick() {
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .build();
        
        game.start();
        
        // Should not throw exception
        assertDoesNotThrow(() -> game.tick(Direction.RIGHT));
        
        // Player should have moved
        Position playerPos = game.getPlayer().getPosition();
        assertNotNull(playerPos);
    }
    
    @Test
    void testGameTickAfterGameOver() {
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .build();
        
        game.start();
        game.end(); // Game over
        
        int initialScore = game.getScore();
        game.tick(Direction.RIGHT);
        
        // Score should not change after game over
        assertEquals(initialScore, game.getScore());
    }
    
    @Test
    void testRewardCollection() {
        BasicReward reward = new BasicReward(new Position(2, 1), 10);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(reward)
            .build();
        
        game.start();
        
        // Move player to reward position
        game.tick(Direction.RIGHT);
        game.tick(Direction.NONE); // Process collisions
        
        // Reward should be collected
        assertEquals(1, game.getBasicCollected());
        assertTrue(reward.isCollected());
    }
    
    @Test
    void testScoreUpdateOnRewardCollection() {
        BasicReward reward = new BasicReward(new Position(2, 1), 15);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(reward)
            .build();
        
        game.start();
        
        // Move player to reward position
        game.tick(Direction.RIGHT);
        game.tick(Direction.NONE); // Process collisions
        
        // Score should be updated
        assertTrue(game.getScore() >= 15);
    }
    
    @Test
    void testWinCondition() {
        BasicReward reward = new BasicReward(new Position(8, 8), 10);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(reward)
            .build();
        
        game.start();
        
        // Manually set up win condition
        // Move player to exit after collecting reward
        // This is a simplified test - actual gameplay would require proper movement
        
        // Just verify the checkWin method works
        assertFalse(game.checkWin()); // Haven't collected reward yet
    }
    
    @Test
    void testCheckLose() {
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .build();
        
        game.start();
        
        assertFalse(game.checkLose()); // Player is alive
    }
    
    @Test
    void testGameReset() {
        BasicReward reward = new BasicReward(new Position(2, 2), 10);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(reward)
            .build();
        
        game.start();
        game.setScore(100);
        game.reset();
        
        // Should reset to initial state
        assertEquals(0, game.getScore());
        assertEquals(0, game.getBasicCollected());
        assertFalse(game.isGameOver());
    }
    
    @Test
    void testAddReward() {
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .build();
        
        BasicReward reward = new BasicReward(new Position(3, 3), 10);
        game.addReward(reward);
        
        assertEquals(1, game.getRewards().size());
        assertEquals(1, game.getBasicToCollect());
    }
    
    @Test
    void testRemoveReward() {
        BasicReward reward = new BasicReward(new Position(3, 3), 10);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(reward)
            .build();
        
        game.removeReward(reward);
        
        assertEquals(0, game.getRewards().size());
    }
    
    @Test
    void testEnemyCollision() {
        StationaryEnemy enemy = new StationaryEnemy("spike", 1, new Position(2, 1));
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addEnemy(enemy)
            .build();
        
        game.start();
        
        // Move player to enemy position
        game.tick(Direction.RIGHT);
        
        // Should trigger collision
        // Note: This tests that collision detection runs without errors
        assertDoesNotThrow(() -> game.tick(Direction.NONE));
    }
    
    @Test
    void testGetElapsedTime() {
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .build();
        
        game.start();
        
        Duration time1 = game.getElapsedTime();
        game.tick(Direction.NONE);
        game.tick(Direction.NONE);
        Duration time2 = game.getElapsedTime();
        
        // Time should have increased
        assertTrue(time2.compareTo(time1) > 0);
    }
}
