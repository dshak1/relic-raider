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
import java.util.ArrayList;

/**
 * Extended test to demonstrate more game features including movement, collisions, and scoring.
 */
public class ExtendedGameTest {
    
    public static void main(String[] args) {
        System.out.println("=== Extended Game Test ===");
        
        // Create a larger game map
        Game game = Game.createSimpleGame(8, 8);
        System.out.println("✓ Created 8x8 game map");
        System.out.println("Map:\n" + game.getMap());
        
        // Add multiple enemies
        MobileEnemy skeleton1 = new MobileEnemy("skeleton1", 1, new Position(2, 3), new SimplePathfinder());
        MobileEnemy skeleton2 = new MobileEnemy("skeleton2", 1, new Position(4, 5), new SimplePathfinder());
        StationaryEnemy spike1 = new StationaryEnemy("spike1", 1, new Position(3, 4));
        StationaryEnemy spike2 = new StationaryEnemy("spike2", 1, new Position(5, 2));
        
        // Add multiple rewards
        BasicReward coin1 = new BasicReward(new Position(1, 2), 10);
        BasicReward coin2 = new BasicReward(new Position(2, 6), 10);
        BasicReward coin3 = new BasicReward(new Position(6, 1), 10);
        BonusReward totem1 = new BonusReward(new Position(3, 6), 25, java.time.Duration.ofSeconds(30));
        BonusReward totem2 = new BonusReward(new Position(6, 5), 25, java.time.Duration.ofSeconds(30));
        
        // Build the complete game
        game = Game.builder()
            .setMap(game.getMap())
            .setPlayer(game.getPlayer())
            .addEnemy(skeleton1)
            .addEnemy(skeleton2)
            .addEnemy(spike1)
            .addEnemy(spike2)
            .addReward(coin1)
            .addReward(coin2)
            .addReward(coin3)
            .addReward(totem1)
            .addReward(totem2)
            .build();
        
        System.out.println("✓ Added " + game.getEnemies().size() + " enemies");
        System.out.println("✓ Added " + game.getRewards().size() + " rewards");
        System.out.println("Basic rewards to collect: " + game.getBasicToCollect());
        System.out.println("Player starting position: " + game.getPlayer().getPosition());
        
        // Start the game
        game.start();
        System.out.println("\n=== Game Loop Test ===");
        
        // Simulate player movement and game ticks
        Direction[] playerMoves = {
            Direction.RIGHT, Direction.RIGHT, Direction.DOWN, 
            Direction.DOWN, Direction.LEFT, Direction.UP,
            Direction.RIGHT, Direction.RIGHT, Direction.DOWN
        };
        
        for (int i = 0; i < playerMoves.length; i++) {
            Direction move = playerMoves[i];
            game.tick(move);
            
            System.out.printf("Tick %d - Move: %s, Player: %s, Score: %d, Basic Collected: %d/%d, Game Over: %s%n",
                i + 1, move, game.getPlayer().getPosition(), 
                game.getScore(), game.getBasicCollected(), game.getBasicToCollect(), 
                game.isGameOver());
            
            // Show enemy positions
            System.out.print("  Enemies: ");
            for (int j = 0; j < game.getEnemies().size(); j++) {
                System.out.print(game.getEnemies().get(j).getClass().getSimpleName() + 
                               " at " + game.getEnemies().get(j).getPosition() + " ");
            }
            System.out.println();
            
            // Check if player collected any rewards
            if (game.getScore() > 0) {
                System.out.println("  🎉 Player collected rewards! Score increased!");
            }
            
            // Check win condition
            if (game.checkWin()) {
                System.out.println("  🏆 WIN CONDITION MET! Player reached exit with all basic rewards!");
                break;
            }
            
            // Check lose condition
            if (game.checkLose()) {
                System.out.println("  💀 LOSE CONDITION MET! Player died!");
                break;
            }
            
            System.out.println();
        }
        
        System.out.println("=== Test Results ===");
        System.out.println("Final Score: " + game.getScore());
        System.out.println("Basic Rewards Collected: " + game.getBasicCollected() + "/" + game.getBasicToCollect());
        System.out.println("Player Final Position: " + game.getPlayer().getPosition());
        System.out.println("Game Over: " + game.isGameOver());
        System.out.println("Player Alive: " + game.getPlayer().isAlive());
        
        // Test game reset
        System.out.println("\n=== Testing Game Reset ===");
        game.reset();
        System.out.println("After reset - Score: " + game.getScore() + 
                         ", Basic Collected: " + game.getBasicCollected() + 
                         ", Player Position: " + game.getPlayer().getPosition());
        
        System.out.println("\n✅ Extended test completed successfully!");
    }
    
    /**
     * Simple pathfinder implementation for testing.
     */
    static class SimplePathfinder implements PathfindingStrategy {
        @Override
        public List<Position> findPath(game.map.Map map, game.map.Position start, game.map.Position target) {
            // Simple implementation that returns a direct path
            List<Position> path = new ArrayList<>();
            path.add(start);
            path.add(target);
            return path;
        }
    }
}
