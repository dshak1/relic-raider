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

import game.ui.GameEndScreen;

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

    /** The game's HUD used to display score, time, and other info */
    private HUD hud;

    /** Timestamp of the last update in milliseconds */
    private long lastUpdateMillis = System.currentTimeMillis();

    /** Timestamp in milliseconds when the player last took damage from a spike. */
    private long lastDamageTime = 0;

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

        // Update bonus rewards (check if they should disappear)
        updateBonusRewards();

        // Handle entity interactions
        resolveCollisions();

        if (hud != null) {
            hud.update();
        }

        // Evaluate win/lose conditions
        if (checkWin()) {
            end();
        } else if (checkLose()) {
            end();
        }

    }

    /**
     * Updates all bonus rewards, checking if they should disappear after their duration.
     */
    private void updateBonusRewards() {
        for (Reward reward : rewards) {
            if (reward instanceof game.reward.BonusReward) {
                game.reward.BonusReward bonusReward = (game.reward.BonusReward) reward;
                bonusReward.tick(map, enemies, rewards);  // Pass map, enemies, and rewards for collision checking
            }
        }
    }

    /**
     * Processes movement for all mobile enemies in the game.
     * Uses pathfinding algorithms if available, otherwise falls back to random movement.
     */
    private int enemyMovementTicks = 0;
    private static final int ENEMY_MOVEMENT_DELAY = 7; // Move every 
    
    private void processEnemyMovement() {
        enemyMovementTicks++;
        
        // Only move enemies every ENEMY_MOVEMENT_DELAY ticks
        if (enemyMovementTicks >= ENEMY_MOVEMENT_DELAY) {
            enemyMovementTicks = 0; // Reset counter
            
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
    }
    
    /**
     * Handles all collision checks between player, rewards, and enemies.
     * Updates score and state accordingly.
     */
    public void resolveCollisions() {
        // Player-Reward collisions
        System.out.println("Player position: " + player.getPosition());
        System.out.println("Number of rewards: " + rewards.size());
        
        for (Reward r : rewards) {
            System.out.println("Reward position: " + r.getPosition() + ", collected: " + r.isCollected());
            
            // Skip bonus rewards that are not active
            if (r instanceof game.reward.BonusReward) {
                game.reward.BonusReward bonusReward = (game.reward.BonusReward) r;
                if (!bonusReward.isActive()) {
                    continue;
                }
            }

            if (player.collidesWith(r) && !r.isCollected()) {
                System.out.println("COLLISION DETECTED WITH REWARD!");
                player.collect(r);
                score += r.getValue();
                System.out.println("New score: " + score);
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
                
                // Handle stationary enemy damage with cooldown
                if (e instanceof game.entity.StationaryEnemy) {
                    long currentTime = System.currentTimeMillis();
                    long timeSinceLastDamage = currentTime - lastDamageTime;
                    
                    // Only apply damage if cooldown has expired
                    if (timeSinceLastDamage >= game.ui.GameConfig.DAMAGE_COOLDOWN_MS) {
                        score -= e.getDamage();
                        lastDamageTime = currentTime;
                        
                        if (score < 0) {
                            isGameOver = true;
                        }
                    }
                }
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
        return isGameOver || !player.isAlive();
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

    /**
     * Sets the {@link HUD} instance for the game.
     * 
     * @param hud the {@link HUD} to display during gameplay
     */
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
        
        // Set entry point (bottom-left area)
        Position entry = new Position(mapHeight - 2, 1);
        map.setEntryPoint(entry);
        
        // Set exit point (right side, in circular room area)
        Position exit = new Position(mapHeight / 2, mapWidth - 6);
        map.setExitPoint(exit);
        
        // Create room structures and corridors
        
        map.getTile(new Position(mapHeight - 10 - 1, 1)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 2)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 3)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 4)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 6)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 7)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 8)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 9)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 10)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 11)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 1 - 1, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 2, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 3, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 4, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 5, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 6, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 7, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 8, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 9, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 10, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 11, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 12, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 13, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 14, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 15, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 16, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 17, 11)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 1 - 17, 12)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 17, 13)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 17, 14)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 1 - 16, 14)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 15, 14)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 15, 15)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 15, 16)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 15, 17)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 15, 18)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 15, 19)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 15, 20)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 15, 21)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 15, 22)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 15, 23)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 1 - 16, 23)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 17, 23)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 17, 24)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 17, 25)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 18, 25)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 1 - 18, 16)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 18, 17)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 18, 18)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 18, 19)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 18, 20)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 18, 21)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 18, 22)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 1 - 17, 16)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 17, 17)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 17, 18)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 17, 19)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 17, 20)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 17, 21)).setBlocked(true);

        //
        map.getTile(new Position(mapHeight - 10 - 1 - 9, 12)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 9, 13)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 9, 14)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 9, 15)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 9, 16)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 9, 17)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 9, 18)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 9, 19)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 9, 20)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 9, 21)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 9, 22)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 - 9, 23)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 1 + 3, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 4, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 5, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 6, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 8, 11)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 9, 11)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 1 + 3, 12)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 3, 13)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 3, 14)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 3, 15)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 3, 16)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 3, 17)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 3, 18)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 1 + 3, 20)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 3, 21)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 3, 22)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 3, 23)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 3, 24)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 3, 25)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 1 + 4, 20)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 5, 20)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 6, 20)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 7, 20)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 8, 20)).setBlocked(true);
        
        map.getTile(new Position(mapHeight - 10 - 1 + 3, 25)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 2, 25)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 1, 25)).setBlocked(true);
        
        map.getTile(new Position(mapHeight - 10 - 1 + 4, 25)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 5, 25)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 6, 25)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 1 + 6, 26)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 6, 27)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 6, 28)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 6, 29)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 6, 30)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 6, 31)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 6, 32)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 6, 33)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 6, 34)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 6, 35)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 6, 36)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 6, 37)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 6, 38)).setBlocked(true);
        
        map.getTile(new Position(mapHeight - 10 - 1 + 6, 23)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 7, 23)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 8, 23)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1 + 9, 23)).setBlocked(true);
        
        map.getTile(new Position(mapHeight - 10 - 5, 17)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 5, 16)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 5, 15)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 5, 14)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 5, 13)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 4, 13)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 3, 13)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 2, 13)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 13)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 1, 14)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 15)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 16)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 17)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 18)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 19)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 20)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 1, 21)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 2, 21)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 3, 21)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 4, 21)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 5, 21)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 6, 21)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 7, 21)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 19, 30)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 18, 30)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 17, 30)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 16, 30)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 15, 30)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 15, 31)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 15, 32)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 15, 33)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 15, 34)).setBlocked(true);
        
        map.getTile(new Position(mapHeight - 10 - 14, 34)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 13, 34)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 12, 34)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 11, 34)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 10, 1)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 10, 2)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 10, 3)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 10, 4)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 10, 5)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 10, 6)).setBlocked(true);

        map.getTile(new Position(mapHeight - 10 - 9, 6)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 8, 6)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 7, 6)).setBlocked(true);
        map.getTile(new Position(mapHeight - 10 - 6, 6)).setBlocked(true);

        // Circular final room walls (approximation)
        int centerRow = mapHeight / 2;
        int centerCol = mapWidth - 8;
        int radius = 5;
        for (int r = centerRow - radius; r <= centerRow + radius; r++) {
            for (int c = centerCol - radius; c <= centerCol + radius; c++) {
                int distSq = (r - centerRow) * (r - centerRow) + (c - centerCol) * (c - centerCol);
                if (distSq >= (radius - 1) * (radius - 1) && distSq <= (radius + 1) * (radius + 1)) {
                    Position pos = new Position(r, c);
                    if (map.inBounds(pos) && !pos.equals(exit)) {
                        map.getTile(pos).setBlocked(true);
                    }
                }
            }
        }
        // Create entrance to circular room
        map.getTile(new Position(centerRow, centerCol - radius)).setBlocked(false);
        
        Player player = new Player(entry);
        Game.Builder builder = Game.builder()
            .setMap(map)
            .setPlayer(player);
        
        // Add mobile enemies (red - skeletons/boulders) with A* pathfinding
        PathfindingStrategy pathfinder = new game.behaviour.AStarPathfinding();
        
        // Enemy positions based on diagram
        Position[] enemyPositions = {
            new Position(mapHeight - 6, 6),      // Bottom-left room
            new Position(12, 18),                 // Central area
            new Position(10, 25),                 // Right-center area
            new Position(20, 20),                 // Lower-right area
            new Position(18, 8)                   // Left-center area
        };
        /* 
        for (int i = 0; i < enemyPositions.length; i++) {
            Position pos = enemyPositions[i];
            if (map.inBounds(pos) && map.isPassable(pos)) {
                builder.addEnemy(new game.entity.MobileEnemy(
                    "mobile_enemy_" + (i + 1),
                    Integer.MAX_VALUE,
                    pos,
                    pathfinder
                ));
            }
        }*/
        
        // Add stationary enemies (purple - spike traps)
        Position[] spikePositions = {
            new Position(mapHeight - 7, 15),
            new Position(mapHeight - 12, 7),
            new Position(14, 12),
            new Position(16, 22),
            new Position(10, 10),
            new Position(20, 16),
            new Position(12, 8),
            new Position(22, 25)
        };
        
        for (int i = 0; i < spikePositions.length; i++) {
            Position pos = spikePositions[i];
            if (map.inBounds(pos) && map.isPassable(pos) && !pos.equals(entry)) {
                builder.addEnemy(new game.entity.StationaryEnemy(
                    "spike_" + (i + 1),
                    game.ui.GameConfig.SPIKE_TRAP_PENALTY,
                    pos
                ));
            }
        }
        
        // Add regular rewards (orange - golden coins)
        Position[] coinPositions = {
            new Position(mapHeight - 5, 5),
            new Position(mapHeight - 8, 8),
            new Position(15, 8),
            new Position(10, 20),
            new Position(18, 18),
            new Position(22, 10),
            new Position(12, 25),
            new Position(8, 15),
            new Position(20, 30),
            new Position(mapHeight - 15, 12)
        };
        
        for (Position pos : coinPositions) {
            if (map.inBounds(pos) && map.isPassable(pos) && !pos.equals(entry) && !pos.equals(exit)) {
                builder.addReward(new game.reward.BasicReward(
                    pos,
                    game.ui.GameConfig.REGULAR_REWARD_VALUE
                ));
            }
        }
        
        // Add bonus rewards (yellow - treasure chests)
        Position[] bonusPositions = {
            new Position(5, 5),
            new Position(mapHeight - 3, mapWidth - 5)
        };
        
        // Add bonus rewards (optional, high value) - place next to player start
        Position bonusPos = new Position(entry.getRow(), entry.getCol() + 3);
        builder.addReward(new game.reward.BonusReward(
            bonusPos,
            game.ui.GameConfig.BONUS_REWARD_VALUE,
            game.ui.GameConfig.BONUS_REWARD_DURATION_TICKS,
            game.ui.GameConfig.BONUS_REWARD_RESPAWN_DELAY_TICKS
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
        lastDamageTime = 0;
        
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
