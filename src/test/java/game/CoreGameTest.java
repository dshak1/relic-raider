package game;

import game.core.Game;
import game.core.Direction;
import game.entity.Player;
import game.entity.MobileEnemy;
import game.entity.StationaryEnemy;
import game.reward.BasicReward;
import game.reward.BonusReward;
import game.map.Position;
import game.behaviour.PathfindingStrategy;
import java.util.List;

/**
 * Simple test class to verify the core game functionality works.
 */
public class CoreGameTest {
    
    public static void main(String[] args) {
        System.out.println("Testing Core Game Functionality...");
        
        // Create a simple game
        Game game = Game.createSimpleGame(10, 10);
        System.out.println("✓ Game created successfully");
        
        // Add some enemies
        MobileEnemy mobileEnemy = new MobileEnemy("skeleton1", 1, new Position(3, 3), new SimplePathfinder());
        StationaryEnemy stationaryEnemy = new StationaryEnemy("spike1", 1, new Position(5, 5));
        
        game = Game.builder()
            .setMap(game.getMap())
            .setPlayer(game.getPlayer())
            .addEnemy(mobileEnemy)
            .addEnemy(stationaryEnemy)
            .build();
        
        System.out.println("✓ Enemies added successfully");
        
        // Add some rewards
        BasicReward basicReward = new BasicReward(new Position(2, 2), 10);
        BonusReward bonusReward = new BonusReward(new Position(7, 7), 25, java.time.Duration.ofSeconds(30));
        
        game = Game.builder()
            .setMap(game.getMap())
            .setPlayer(game.getPlayer())
            .addEnemy(mobileEnemy)
            .addEnemy(stationaryEnemy)
            .addReward(basicReward)
            .addReward(bonusReward)
            .build();
        
        System.out.println("✓ Rewards added successfully");
        System.out.println("Basic rewards to collect: " + game.getBasicToCollect());
        
        // Test game loop
        game.start();
        System.out.println("✓ Game started");
        
        // Run a few ticks
        for (int i = 0; i < 5; i++) {
            game.tick(Direction.RIGHT);
            System.out.println("Tick " + (i + 1) + " - Score: " + game.getScore() + 
                             ", Basic Collected: " + game.getBasicCollected() + 
                             ", Game Over: " + game.isGameOver());
        }
        
        System.out.println("✓ Game loop executed successfully");
        System.out.println("All core functionality tests passed!");
    }
    
    /**
     * Simple pathfinder implementation for testing.
     */
    static class SimplePathfinder implements PathfindingStrategy {
        @Override
        public List<Position> findPath(game.map.Map map, game.map.Position start, game.map.Position target) {
            // Simple implementation that just returns the target position
            return java.util.Arrays.asList(start, target);
        }
    }
}
