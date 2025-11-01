package game.core;

import game.entity.Enemy;
import game.entity.Player;
import game.map.Map;
import game.map.Position;
import game.reward.Reward;
import java.time.Duration; 
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

    /**
     * Private constructor for Game instances.
     * Use Game.Builder to create new instances.
     */
    private Game(
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
        if (isGameOver) return;

        // Process player decision and movement
        Position nextPos = player.decideNext(map, input);
        player.moveTo(nextPos);

        // Process enemy movement (AI-controlled)
        for (Enemy enemy : enemies) {
            if (enemy instanceof game.behaviour.Movable movableEnemy) {
                Position enemyNextPos = movableEnemy.decideNext(map, null);
                movableEnemy.moveTo(enemyNextPos);
            }
        }

        // Handle entity interactions
        resolveCollisions();

        // Evaluate win/lose conditions
        if (checkWin()) {
            end();
            System.out.println("You win!");
        } else if (checkLose()) {
            end();
            System.out.println("You lose!");
        }

        // Update time (roughly one frame at 60 FPS)
        elapsedTime = elapsedTime.plusMillis(16);
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
        
        // Set entry point (top-left corner, inside border)
        Position entry = new Position(1, 1);
        map.setEntryPoint(entry);
        
        // Set exit point (bottom-right corner, inside border)
        Position exit = new Position(mapHeight - 2, mapWidth - 2);
        map.setExitPoint(exit);
        
        Player player = new Player(entry);
        
        return Game.builder()
            .setMap(map)
            .setPlayer(player)
            .build();
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
