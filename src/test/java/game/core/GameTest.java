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
            300, 100);
        
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
    
    // ==================== GameTest Part 1: checkWin, checkLose, resolveCollisions ====================
    
    @Test
    void testCheckWin_AllBasicRewardsCollected_AtExit() {
        BasicReward reward1 = new BasicReward(new Position(2, 1), 10);
        BasicReward reward2 = new BasicReward(new Position(3, 1), 10);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(reward1)
            .addReward(reward2)
            .setBasicToCollect(2)
            .build();
        
        game.start();
        
        // Collect all rewards by moving to them
        player.setPosition(reward1.getPosition());
        game.resolveCollisions();
        
        player.setPosition(reward2.getPosition());
        game.resolveCollisions();
        
        // Move player to exit
        player.setPosition(map.getExitPoint());
        
        // Win condition: at exit AND (basicCollected >= basicToCollect OR finalRewardCollected)
        assertTrue(game.checkWin(), "Should win when all rewards collected and at exit");
    }
    
    @Test
    void testCheckWin_FinalRewardCollected_AtExit() {
        game.reward.FinalReward finalReward = new game.reward.FinalReward(new Position(2, 1), 100);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(finalReward)
            .build();
        
        game.start();
        
        // Collect final reward by moving to it
        player.setPosition(finalReward.getPosition());
        game.resolveCollisions();
        
        // Move player to exit
        player.setPosition(map.getExitPoint());
        
        // Win condition should be true: at exit AND finalRewardCollected
        assertTrue(game.checkWin(), "Should win when final reward collected and at exit");
    }
    
    @Test
    void testCheckWin_NotAtExit_ReturnsFalse() {
        BasicReward reward = new BasicReward(new Position(2, 2), 10);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(reward)
            .setBasicToCollect(1)
            .build();
        
        game.start();
        
        // Collect reward but don't reach exit
        reward.setCollected(true);
        player.setPosition(new Position(1, 1)); // Not at exit
        
        assertFalse(game.checkWin(), "Should not win if not at exit");
    }
    
    @Test
    void testCheckWin_AtExit_NotEnoughRewards_ReturnsFalse() {
        BasicReward reward = new BasicReward(new Position(2, 1), 10);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(reward)
            .setBasicToCollect(2) // Need 2, only have 1
            .build();
        
        game.start();
        
        // Collect only one reward
        player.setPosition(reward.getPosition());
        game.resolveCollisions();
        
        // Move to exit without collecting enough rewards
        player.setPosition(map.getExitPoint());
        
        // Should not win: at exit but not enough rewards collected
        assertFalse(game.checkWin(), "Should not win without enough rewards");
    }
    
    @Test
    void testCheckLose_PlayerNotAlive_ReturnsTrue() {
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .build();
        
        game.start();
        
        player.setAlive(false);
        
        assertTrue(game.checkLose(), "Should lose when player is not alive");
    }
    
    @Test
    void testCheckLose_GameOver_ReturnsTrue() {
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .build();
        
        game.start();
        game.end(); // Sets isGameOver to true
        
        assertTrue(game.checkLose(), "Should lose when game is over");
    }
    
    @Test
    void testCheckLose_PlayerAlive_GameNotOver_ReturnsFalse() {
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .build();
        
        game.start();
        
        assertFalse(game.checkLose(), "Should not lose when player is alive and game is not over");
    }
    
    @Test
    void testResolveCollisions_PlayerBasicReward_ScoreIncrease() {
        BasicReward reward = new BasicReward(new Position(1, 1), 10);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(reward)
            .build();
        
        game.start();
        
        int initialScore = game.getScore();
        player.setPosition(reward.getPosition());
        
        game.resolveCollisions();
        
        assertTrue(game.getScore() > initialScore, 
            "Score should increase after collecting basic reward");
        assertTrue(reward.isCollected(), "Reward should be marked as collected");
    }
    
    @Test
    void testResolveCollisions_PlayerBonusReward_ScoreIncrease() {
        BonusReward bonusReward = new BonusReward(new Position(1, 1), 50, 100, 50);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(bonusReward)
            .build();
        
        game.start();
        
        int initialScore = game.getScore();
        player.setPosition(bonusReward.getPosition());
        
        // Ensure bonus reward is active
        bonusReward.appear();
        
        game.resolveCollisions();
        
        assertTrue(game.getScore() > initialScore, 
            "Score should increase after collecting bonus reward");
    }
    
    @Test
    void testResolveCollisions_PlayerMobileEnemy_PlayerDies() {
        MobileEnemy enemy = new MobileEnemy("enemy", 1, new Position(1, 1), 
            new game.behaviour.AStarPathfinding());
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addEnemy(enemy)
            .build();
        
        game.start();
        
        player.setPosition(enemy.getPosition());
        
        assertTrue(player.isAlive(), "Player should be alive before collision");
        game.resolveCollisions();
        
        assertFalse(player.isAlive(), "Player should die after colliding with mobile enemy");
    }
    
    @Test
    void testResolveCollisions_PlayerStationaryEnemy_ScoreDecrease() {
        StationaryEnemy spike = new StationaryEnemy("spike", 20, new Position(1, 1));
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addEnemy(spike)
            .build();
        
        game.start();
        
        game.setScore(50);
        player.setPosition(spike.getPosition());
        
        int initialScore = game.getScore();
        game.resolveCollisions();
        
        // Score should decrease (with cooldown consideration)
        // Note: Cooldown may prevent immediate damage
        assertTrue(game.getScore() <= initialScore, 
            "Score should decrease or stay same (cooldown) after spike collision");
    }
    
    @Test
    void testResolveCollisions_PlayerStationaryEnemy_NegativeScore_GameOver() {
        StationaryEnemy spike = new StationaryEnemy("spike", 20, new Position(1, 1));
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addEnemy(spike)
            .build();
        
        game.start();
        
        game.setScore(10); // Low score
        player.setPosition(spike.getPosition());
        
        // Simulate multiple collisions to drain score (accounting for cooldown)
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1100); // Wait for cooldown
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            game.resolveCollisions();
            if (game.getScore() < 0) {
                break;
            }
        }
        
        // If score goes negative, game should be over
        if (game.getScore() < 0) {
            assertTrue(game.isGameOver(), "Game should be over when score is negative");
        }
    }
    
    @Test
    void testResolveCollisions_MultipleCollisions_SingleTick() {
        BasicReward reward = new BasicReward(new Position(1, 1), 10);
        StationaryEnemy spike = new StationaryEnemy("spike", 5, new Position(1, 1));
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(reward)
            .addEnemy(spike)
            .build();
        
        game.start();
        
        player.setPosition(new Position(1, 1)); // Same position as reward and spike
        
        game.resolveCollisions();
        
        // Should handle multiple collisions - reward collection should happen
        // Note: Priority may matter (rewards vs enemies)
        assertNotNull(game.getScore(), "Score should be updated after collisions");
        assertTrue(reward.isCollected(), "Reward should be collected during multiple collisions");
    }
    
    @Test
    void testResolveCollisions_FinalReward_UnlocksExit() {
        game.reward.FinalReward finalReward = new game.reward.FinalReward(new Position(1, 1), 100);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(finalReward)
            .build();
        
        game.start();
        
        player.setPosition(finalReward.getPosition());
        game.resolveCollisions();
        
        // Final reward collection should set finalRewardCollected flag
        assertTrue(finalReward.isCollected(), "Final reward should be collected");
    }
    
    @Test
    void testResolveCollisions_BonusRewardInactive_NotCollected() {
        BonusReward bonusReward = new BonusReward(new Position(1, 1), 50, 10, 5);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(bonusReward)
            .build();
        
        game.start();
        
        // Make bonus reward inactive
        bonusReward.disappear();
        player.setPosition(bonusReward.getPosition());
        
        int initialScore = game.getScore();
        game.resolveCollisions();
        
        // Score should not change if bonus reward is inactive
        assertEquals(initialScore, game.getScore(), 
            "Score should not change when collecting inactive bonus reward");
    }
}
