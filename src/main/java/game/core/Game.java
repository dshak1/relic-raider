package game.core;

import game.behaviour.PathfindingStrategy;
import game.entity.Enemy;
import game.entity.Player;
import game.map.Map;
import game.map.Position;
import game.reward.Reward;
import game.ui.HUD;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Game} class represents the main controller for gameplay.
 * It manages the map, player, enemies, rewards, score, and win/loss state.
 *
 * <p>This class encapsulates the game loop logic and collision handling
 * between the player, enemies, and rewards.</p>
 */
public class Game {

    /** Current total score accumulated by the player. */
    private int score;

    /** True if the game is currently over (win or loss). */
    private boolean isGameOver;

    /** Duration of time since the game started. */
    private Duration elapsedTime;

    /** Total number of basic rewards that must be collected to win. */
    private int basicToCollect;

    /** Number of basic rewards collected so far. */
    private int basicCollected;

    /** The current game map containing tiles and positions. */
    private Map map;

    /** The player character controlled by the user. */
    private Player player;

    /** List of enemies active in the game. */
    private List<Enemy> enemies;

    /** List of rewards placed on the map. */
    private List<Reward> rewards;

    private HUD hud;

    private long lastUpdateMillis = System.currentTimeMillis();

    /**
     * Private constructor for Game instances.
     * Use Game.Builder to create new instances.
     */
    public Game(
        int score,
        boolean isGameOver,
        Duration elapsedTime,
        int basicToCollect,
        int basicCollected,
        Map map,
        Player player,
        List<Enemy> enemies,
        List<Reward> rewards
    ) {
        this.score = score;
        this.isGameOver = isGameOver;
        this.elapsedTime = elapsedTime;
        this.basicToCollect = basicToCollect;
        this.basicCollected = basicCollected;
        this.map = map;
        this.player = player;
        this.enemies = enemies;
        this.rewards = rewards;
    }


    /**
     * Starts or restarts the game, resetting timers and status flags.
     */
    public void start() {
        System.out.println("Game Started!");
        isGameOver = false;
        elapsedTime = Duration.ZERO;
    }

    /**
     * Advances the game by one logical step (or frame).
     * Processes player actions, collisions, and win/loss conditions.
     *
     * @param input optional player input or direction for this frame
     */
    public void tick(Direction input) {
        long now = System.currentTimeMillis();
        long delta = now - lastUpdateMillis;
        lastUpdateMillis = now;
        elapsedTime = elapsedTime.plusMillis(delta);
        
        if (isGameOver) return;

        // Process player decision and movement
        Position nextPos = player.decideNext(map, input);
        player.moveTo(nextPos);

        // Process enemy movement with pathfinding integration
        processEnemyMovement();

        // Handle entity interactions
        resolveCollisions();

        if (hud != null) {
            hud.update();
        }

        // Evaluate win/lose conditions
        if (checkWin()) {
            end();
            System.out.println("You win!");
            hud.showMessage("You Win! Score: " + score + ", Time: " + elapsedTime.toSeconds() + "s");
        } else if (checkLose()) {
            end();
            System.out.println("You lose!");
        }
    }

    /**
     * Processes movement for all mobile enemies in the game.
     * Uses pathfinding algorithms if available, otherwise falls back to random movement.
     */
    private void processEnemyMovement() {
        for (Enemy enemy : enemies) {
            if (enemy instanceof game.behaviour.Movable) {
                game.behaviour.Movable movableEnemy = (game.behaviour.Movable) enemy;
                Position enemyNextPos;
                
                // Use pathfinding if enemy is MobileEnemy with pathfinder
                if (enemy instanceof game.entity.MobileEnemy) {
                    game.entity.MobileEnemy mobileEnemy = (game.entity.MobileEnemy) enemy;
                    if (mobileEnemy.getPathfinder() != null) {
                        // Use the specific pathfinding method with player position as target
                        enemyNextPos = mobileEnemy.decideNext(map, player.getPosition());
                    } else {
                        // No pathfinder, use random movement
                        enemyNextPos = movableEnemy.decideNext(map, null);
                    }
                } else {
                    // Random movement for other movable enemies
                    enemyNextPos = movableEnemy.decideNext(map, null);
                }
                
                movableEnemy.moveTo(enemyNextPos);
            }
        }
    }
    
    /**
     * Handles all collision checks between player, rewards, and enemies.
     * Updates score and state accordingly.
     */
    public void resolveCollisions() {
        // Player-Reward collisions
        for (Reward r : rewards) {
            if (player.collidesWith(r) && !r.isCollected()) {
                player.collect(r);
                score += r.getValue();
                r.onCollect(player);
                
                // Update basic collected counter for non-bonus rewards
                if (!r.isBonus()) {
                    basicCollected++;
                }
            }
        }

        // Player-Enemy collisions
        for (Enemy e : enemies) {
            if (player.collidesWith(e)) {
                e.onContact(player);
            }
        }
    }

    /**
     * Checks whether the player has satisfied all win conditions.
     *
     * @return true if the player reached the exit and collected all required rewards
     */
    public boolean checkWin() {
        return player.atExit(map) && (basicCollected >= basicToCollect);
    }

    /**
     * Checks whether the player has lost the game.
     *
     * @return true if the game is over due to defeat
     */
    public boolean checkLose() {
        return isGameOver;
    }

    /**
     * Ends the current game session, printing a summary message.
     */
    public void end() {
        isGameOver = true;
        System.out.println("Game ended. Final score: " + score);
    }

    /**
     * Adds a reward to the map.
     *
     * @param r the reward to add
     */
    public void addReward(Reward r) {
        rewards.add(r);
        if (!r.isBonus()) basicToCollect++;
    }

    /**
     * Removes a reward from the map.
     *
     * @param r the reward to remove
     */
    public void removeReward(Reward r) {
        rewards.remove(r);
    }

    /**
     * Determines if a reward should respawn after being collected.
     *
     * @param r the reward to check
     * @return true if the reward should respawn, false otherwise
     */
    public boolean shouldRespawn(Reward r) {
        return r.isRespawnable();
    }

    /** @return the current score value */
    public int getScore() {
        return score;
    }
    
    /**
     * Sets the current score value.
     * Primarily used for testing and game state restoration.
     * 
     * @param score the new score value
     */
    public void setScore(int score) {
        this.score = score;
    }

    /** @return true if the game is over */
    public boolean isGameOver() {
        return isGameOver;
    }

    /** @return total time elapsed since the game started */
    public Duration getElapsedTime() {
        return elapsedTime;
    }

    /** @return the current map */
    public Map getMap() {
        return map;
    }

    /** @return the player instance */
    public Player getPlayer() {
        return player;
    }

    public void setHUD(HUD hud) {
        this.hud = hud;
    }

    /**
     * Builder class for creating Game instances with a fluent interface.
     */
    public static class Builder {
        private int score = 0;
        private boolean isGameOver = false;
        private Duration elapsedTime = Duration.ZERO;
        private int basicToCollect = 0;
        private int basicCollected = 0;
        private Map map;
        private Player player;
        private List<Enemy> enemies = new java.util.ArrayList<>();
        private List<Reward> rewards = new java.util.ArrayList<>();

        public Builder setMap(Map map) {
            this.map = map;
            return this;
        }

        public Builder setPlayer(Player player) {
            this.player = player;
            return this;
        }

        public Builder addEnemy(Enemy enemy) {
            this.enemies.add(enemy);
            return this;
        }

        public Builder addReward(Reward reward) {
            this.rewards.add(reward);
            if (!reward.isBonus()) {
                this.basicToCollect++;
            }
            return this;
        }

        public Builder setScore(int score) {
            this.score = score;
            return this;
        }

        public Builder setBasicToCollect(int basicToCollect) {
            this.basicToCollect = basicToCollect;
            return this;
        }

        public Game build() {
            if (map == null) {
                throw new IllegalStateException("Map is required");
            }
            if (player == null) {
                throw new IllegalStateException("Player is required");
            }
            return new Game(score, isGameOver, elapsedTime, basicToCollect, 
                           basicCollected, map, player, enemies, rewards);
        }
    }

    /**
     * Creates a new Game instance using the builder pattern.
     * 
     * @return a new Game.Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a simple game with basic setup for testing.
     * 
     * @param mapWidth the width of the game map
     * @param mapHeight the height of the game map
     * @return a new Game instance with basic setup
     */
    public static Game createSimpleGame(int mapWidth, int mapHeight) {
        Map map = new Map(mapWidth, mapHeight);
        map.createBorder();
        
        // Set entry point (left side, inside border)
        Position entry = new Position(mapHeight - 3, 2);
        map.setEntryPoint(entry);
        
        // Set exit point (right side, inside border)
        Position exit = new Position(3, mapWidth - 3);
        map.setExitPoint(exit);
        
        // Add some simple internal walls for maze structure
        // Vertical wall in left area
        for (int row = 5; row < 15; row++) {
            Position wallPos = new Position(row, 10);
            if (map.inBounds(wallPos)) {
                map.getTile(wallPos).setBlocked(true);
            }
        }
        
        // Vertical wall in right area
        for (int row = 10; row < 25; row++) {
            Position wallPos = new Position(row, 30);
            if (map.inBounds(wallPos)) {
                map.getTile(wallPos).setBlocked(true);
            }
        }
        
        // Horizontal wall in middle
        for (int col = 15; col < 25; col++) {
            Position wallPos = new Position(15, col);
            if (map.inBounds(wallPos)) {
                map.getTile(wallPos).setBlocked(true);
            }
        }
        
        // Small room structure in center
        for (int col = 18; col < 23; col++) {
            if (col != 20) { // Leave opening
                Position wallPos = new Position(10, col);
                if (map.inBounds(wallPos)) {
                    map.getTile(wallPos).setBlocked(true);
                }
            }
        }
        
        Player player = new Player(entry);
        
        Game.Builder builder = Game.builder()
            .setMap(map)
            .setPlayer(player);
        
        // Add mobile enemies with A* pathfinding
        PathfindingStrategy pathfinder = new game.behaviour.AStarPathfinding();
        
        // Add 4 mobile enemies in safe positions
        builder.addEnemy(new game.entity.MobileEnemy(
            "skeleton1", 
            100,
            new Position(8, 5),
            pathfinder
        ));
        
        builder.addEnemy(new game.entity.MobileEnemy(
            "skeleton2",
            100,
            new Position(20, 15),
            pathfinder
        ));
        
        builder.addEnemy(new game.entity.MobileEnemy(
            "boulder1",
            100,
            new Position(12, 25),
            pathfinder
        ));
        
        builder.addEnemy(new game.entity.MobileEnemy(
            "skeleton3",
            100,
            new Position(5, 35),
            pathfinder
        ));
        
        // Add stationary enemies (spike traps)
        builder.addEnemy(new game.entity.StationaryEnemy(
            "spike1",
            game.ui.GameConfig.SPIKE_TRAP_PENALTY,
            new Position(15, 8)
        ));
        
        builder.addEnemy(new game.entity.StationaryEnemy(
            "spike2",
            game.ui.GameConfig.SPIKE_TRAP_PENALTY,
            new Position(10, 28)
        ));
        
        builder.addEnemy(new game.entity.StationaryEnemy(
            "spike3",
            game.ui.GameConfig.SPIKE_TRAP_PENALTY,
            new Position(18, 12)
        ));
        
        // Add basic rewards (must collect all to win)
        builder.addReward(new game.reward.BasicReward(
            new Position(5, 5),
            game.ui.GameConfig.REGULAR_REWARD_VALUE
        ));
        
        builder.addReward(new game.reward.BasicReward(
            new Position(10, 15),
            game.ui.GameConfig.REGULAR_REWARD_VALUE
        ));
        
        builder.addReward(new game.reward.BasicReward(
            new Position(20, 20),
            game.ui.GameConfig.REGULAR_REWARD_VALUE
        ));
        
        builder.addReward(new game.reward.BasicReward(
            new Position(25, 10),
            game.ui.GameConfig.REGULAR_REWARD_VALUE
        ));
        
        builder.addReward(new game.reward.BasicReward(
            new Position(8, 35),
            game.ui.GameConfig.REGULAR_REWARD_VALUE
        ));
        
        // Add bonus rewards (optional, high value)
        builder.addReward(new game.reward.BonusReward(
            new Position(12, 20),
            game.ui.GameConfig.BONUS_REWARD_VALUE,
            null
        ));
        
        builder.addReward(new game.reward.BonusReward(
            new Position(22, 28),
            game.ui.GameConfig.BONUS_REWARD_VALUE,
            null
        ));
        
        return builder.build();
    }

    /**
     * Resets the game to its initial state.
     * Useful for restarting without creating a new Game instance.
     */
    public void reset() {
        score = 0;
        isGameOver = false;
        elapsedTime = Duration.ZERO;
        basicCollected = 0;
        
        // Reset player position to entry point
        if (map != null && map.getEntryPoint() != null) {
            player.setPosition(map.getEntryPoint());
        }
        
        // Reset all rewards to uncollected state
        for (Reward reward : rewards) {
            reward.setCollected(false);
        }
    }

    /**
     * Gets the number of basic rewards collected so far.
     * 
     * @return the number of basic rewards collected
     */
    public int getBasicCollected() {
        return basicCollected;
    }

    /**
     * Gets the total number of basic rewards that need to be collected to win.
     * 
     * @return the total number of basic rewards required
     */
    public int getBasicToCollect() {
        return basicToCollect;
    }

    /**
     * Gets the list of enemies in the game.
     * 
     * @return the list of enemies
     */
    public List<Enemy> getEnemies() {
        return new java.util.ArrayList<>(enemies);
    }

    /**
     * Gets the list of rewards in the game.
     * 
     * @return the list of rewards
     */
    public List<Reward> getRewards() {
        return new java.util.ArrayList<>(rewards);
    }
}
