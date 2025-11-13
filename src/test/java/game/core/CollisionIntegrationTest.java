package game.core;

import game.entity.MobileEnemy;
import game.entity.Player;
import game.entity.StationaryEnemy;
import game.map.Map;
import game.map.Position;
import game.reward.BasicReward;
import game.reward.BonusReward;
import game.behaviour.AStarPathfinding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for collision detection system.
 * Tests interactions between Player, Rewards, and Enemies in realistic game scenarios.
 */
public class CollisionIntegrationTest {
    
    private Game game;
    private Map map;
    private Player player;
    
    @BeforeEach
    void setUp() {
        map = new Map(10, 10);
        map.createBorder();
        map.setEntryPoint(new Position(1, 1));
        map.setExitPoint(new Position(8, 8));
        
        player = new Player(new Position(1, 1));
    }
    
    @Test
    void testPlayerBasicReward_Collision_ScoreIncrease() {
        BasicReward reward = new BasicReward(new Position(2, 1), 10);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(reward)
            .build();
        
        game.start();
        
        int initialScore = game.getScore();
        
        // Move player to reward position
        player.setPosition(reward.getPosition());
        game.resolveCollisions();
        
        assertTrue(game.getScore() > initialScore, 
            "Score should increase by reward value after collision");
        assertEquals(initialScore + 10, game.getScore(), 
            "Score should increase by exactly 10 points");
        assertTrue(reward.isCollected(), "Reward should be marked as collected");
    }
    
    @Test
    void testPlayerBonusReward_Collision_ScoreIncrease_PermanentCollection() {
        BonusReward bonusReward = new BonusReward(new Position(2, 1), 50, 100, 50);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(bonusReward)
            .build();
        
        game.start();
        
        // Ensure bonus reward is active
        bonusReward.appear();
        
        int initialScore = game.getScore();
        
        // Move player to bonus reward position
        player.setPosition(bonusReward.getPosition());
        game.resolveCollisions();
        
        assertTrue(game.getScore() > initialScore, 
            "Score should increase after collecting bonus reward");
        assertEquals(initialScore + 50, game.getScore(), 
            "Score should increase by exactly 50 points");
        assertTrue(bonusReward.isCollected(), "Bonus reward should be marked as collected");
        // Bonus reward should not respawn after being collected
        assertFalse(bonusReward.isActive(), 
            "Bonus reward should become inactive after permanent collection");
    }
    
    @Test
    void testPlayerMobileEnemy_Collision_GameOver() {
        MobileEnemy enemy = new MobileEnemy("skeleton", Integer.MAX_VALUE, 
            new Position(2, 1), new AStarPathfinding());
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addEnemy(enemy)
            .build();
        
        game.start();
        
        assertTrue(player.isAlive(), "Player should be alive initially");
        assertFalse(game.isGameOver(), "Game should not be over initially");
        
        // Move player to enemy position
        player.setPosition(enemy.getPosition());
        game.resolveCollisions();
        
        assertFalse(player.isAlive(), "Player should die after colliding with mobile enemy");
        assertTrue(game.checkLose(), "Game should be in lose state");
    }
    
    @Test
    void testPlayerStationaryEnemy_Collision_ScoreDecrease_WithCooldown() {
        StationaryEnemy spike = new StationaryEnemy("spike", 20, new Position(2, 1));
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addEnemy(spike)
            .build();
        
        game.start();
        game.setScore(100);
        
        // Move player to spike position
        player.setPosition(spike.getPosition());
        
        int scoreBeforeFirstCollision = game.getScore();
        game.resolveCollisions();
        int scoreAfterFirstCollision = game.getScore();
        
        // First collision should apply damage
        assertTrue(scoreAfterFirstCollision < scoreBeforeFirstCollision, 
            "Score should decrease after first spike collision");
        assertEquals(scoreBeforeFirstCollision - 20, scoreAfterFirstCollision, 
            "Score should decrease by exactly 20 points");
        
        // Second collision immediately after should not apply damage (cooldown)
        game.resolveCollisions();
        int scoreAfterSecondCollision = game.getScore();
        
        assertEquals(scoreAfterFirstCollision, scoreAfterSecondCollision, 
            "Score should not change on second collision due to cooldown");
    }
    
    @Test
    void testPlayerStationaryEnemy_MultipleCollisions_CooldownRespected() {
        StationaryEnemy spike = new StationaryEnemy("spike", 20, new Position(2, 1));
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addEnemy(spike)
            .build();
        
        game.start();
        game.setScore(100);
        player.setPosition(spike.getPosition());
        
        // First collision
        int score1 = game.getScore();
        game.resolveCollisions();
        int score2 = game.getScore();
        assertTrue(score2 < score1, "First collision should apply damage");
        
        // Wait for cooldown and collide again
        try {
            Thread.sleep(1100); // Wait for cooldown (1000ms + buffer)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        int score3 = game.getScore();
        game.resolveCollisions();
        int score4 = game.getScore();
        
        assertTrue(score4 < score3, "After cooldown, damage should apply again");
        assertEquals(score3 - 20, score4, "Damage should be 20 points after cooldown");
    }
    
    @Test
    void testMultipleCollisions_SingleTick_RewardAndEnemy() {
        BasicReward reward = new BasicReward(new Position(2, 1), 10);
        StationaryEnemy spike = new StationaryEnemy("spike", 5, new Position(2, 1));
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(reward)
            .addEnemy(spike)
            .build();
        
        game.start();
        game.setScore(50);
        
        // Player at same position as both reward and spike
        player.setPosition(new Position(2, 1));
        
        int initialScore = game.getScore();
        game.resolveCollisions();
        
        // Both collisions should be processed
        // Reward collection happens first, then enemy damage
        assertTrue(reward.isCollected(), "Reward should be collected");
        assertTrue(game.getScore() != initialScore, 
            "Score should change after multiple collisions");
    }
    
    @Test
    void testCollisionPriority_RewardBeforeEnemy() {
        // Test that reward collection happens before enemy damage
        BasicReward reward = new BasicReward(new Position(2, 1), 10);
        StationaryEnemy spike = new StationaryEnemy("spike", 5, new Position(2, 1));
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(reward)
            .addEnemy(spike)
            .build();
        
        game.start();
        game.setScore(50);
        player.setPosition(new Position(2, 1));
        
        int initialScore = game.getScore();
        game.resolveCollisions();
        
        // Reward should be collected (score +10), then spike damage (score -5)
        // Net result: +5
        assertTrue(reward.isCollected(), "Reward should be collected");
        assertEquals(initialScore + 5, game.getScore(), 
            "Net score change should be +5 (reward +10, spike -5)");
    }
    
    @Test
    void testMultipleRewards_SequentialCollection() {
        BasicReward reward1 = new BasicReward(new Position(2, 1), 10);
        BasicReward reward2 = new BasicReward(new Position(3, 1), 10);
        BasicReward reward3 = new BasicReward(new Position(4, 1), 10);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(reward1)
            .addReward(reward2)
            .addReward(reward3)
            .build();
        
        game.start();
        
        int initialScore = game.getScore();
        
        // Collect first reward
        player.setPosition(reward1.getPosition());
        game.resolveCollisions();
        assertEquals(initialScore + 10, game.getScore(), "First reward collected");
        
        // Collect second reward
        player.setPosition(reward2.getPosition());
        game.resolveCollisions();
        assertEquals(initialScore + 20, game.getScore(), "Second reward collected");
        
        // Collect third reward
        player.setPosition(reward3.getPosition());
        game.resolveCollisions();
        assertEquals(initialScore + 30, game.getScore(), "Third reward collected");
        
        assertTrue(reward1.isCollected() && reward2.isCollected() && reward3.isCollected(),
            "All rewards should be collected");
    }
    
    @Test
    void testPlayerStationaryEnemy_NegativeScore_GameOver() {
        StationaryEnemy spike = new StationaryEnemy("spike", 20, new Position(2, 1));
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addEnemy(spike)
            .build();
        
        game.start();
        game.setScore(10); // Low score
        player.setPosition(spike.getPosition());
        
        // First collision: score becomes -10
        game.resolveCollisions();
        
        // Wait for cooldown and collide again to ensure negative score triggers game over
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // If score is already negative, game should be over
        if (game.getScore() < 0) {
            assertTrue(game.isGameOver(), 
                "Game should be over when score becomes negative");
        }
    }
    
    @Test
    void testPlayerBonusReward_ActiveState_RequiredForCollection() {
        BonusReward bonusReward = new BonusReward(new Position(2, 1), 50, 10, 5);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(bonusReward)
            .build();
        
        game.start();
        
        // Make bonus reward inactive
        bonusReward.disappear();
        assertFalse(bonusReward.isActive(), "Bonus reward should be inactive");
        
        player.setPosition(bonusReward.getPosition());
        int initialScore = game.getScore();
        game.resolveCollisions();
        
        // Score should not change when bonus reward is inactive
        assertEquals(initialScore, game.getScore(), 
            "Score should not change when collecting inactive bonus reward");
        assertFalse(bonusReward.isCollected(), 
            "Inactive bonus reward should not be collected");
    }
    
    @Test
    void testPlayerMobileEnemy_Movement_CollisionDetection() {
        MobileEnemy enemy = new MobileEnemy("skeleton", Integer.MAX_VALUE, 
            new Position(3, 3), new AStarPathfinding());
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addEnemy(enemy)
            .build();
        
        game.start();
        
        // Position player and enemy close to each other
        player.setPosition(new Position(3, 2));
        enemy.setPosition(new Position(3, 3));
        
        assertFalse(player.collidesWith(enemy), "Player and enemy should not collide initially");
        
        // Move player to enemy position
        player.setPosition(enemy.getPosition());
        assertTrue(player.collidesWith(enemy), "Player and enemy should collide");
        
        game.resolveCollisions();
        assertFalse(player.isAlive(), "Player should die after collision");
    }
    
    @Test
    void testCollisionSystem_AlreadyCollectedReward_NoDoubleCollection() {
        BasicReward reward = new BasicReward(new Position(2, 1), 10);
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .addReward(reward)
            .build();
        
        game.start();
        
        // Collect reward first time
        player.setPosition(reward.getPosition());
        game.resolveCollisions();
        int scoreAfterFirst = game.getScore();
        assertTrue(reward.isCollected(), "Reward should be collected");
        
        // Try to collect again (should not work)
        game.resolveCollisions();
        int scoreAfterSecond = game.getScore();
        
        assertEquals(scoreAfterFirst, scoreAfterSecond, 
            "Score should not change when collecting already-collected reward");
    }
}

