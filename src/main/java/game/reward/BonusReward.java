package game.reward;

import game.entity.Player;
import game.map.Position;
import game.map.Map;

/**
 * High-value optional reward that can appear, disappear, and respawn.
 */
public class BonusReward extends Reward {

    private boolean isActive;
    private int ticksActive;  // Track how many ticks this reward has been active
    private int ticksSinceDisappeared;  // Track how many ticks since it disappeared
    private int maxActiveTicks;  // Maximum ticks before disappearing
    private int respawnDelayTicks;  // Ticks to wait before respawning
    private boolean permanentlyCollected;  // True if collected by player (don't respawn)

    private static int totalBonusRewards = 0;
    private static int collectedBonusRewards = 0;

    /**
     * Constructs a new bonus reward at the specified position.
     *
     * @param position the position of this reward on the map
     * @param value the point value awarded when collected
     * @param maxActiveTicks maximum number of ticks this reward stays visible
     * @param respawnDelayTicks number of ticks to wait before respawning
     */
    public BonusReward(Position position, int value, int maxActiveTicks, int respawnDelayTicks) {
        super(position, value, true, true);
        this.isActive = true;
        this.ticksActive = 0;
        this.ticksSinceDisappeared = 0;
        this.maxActiveTicks = maxActiveTicks;
        this.respawnDelayTicks = respawnDelayTicks;
        this.permanentlyCollected = false;
        totalBonusRewards++;
    }

    /**
     * Updates the bonus reward state for one tick.
     * Increments the active timer and disappears if duration exceeded.
     * Also handles respawn timing.
     * 
     * @param map the game map (needed for finding new spawn positions)
     * @param enemies list of enemies to avoid when respawning
     * @param rewards list of other rewards to avoid when respawning
     */
    public void tick(Map map, java.util.List<game.entity.Enemy> enemies, java.util.List<Reward> rewards) {
        // Don't do anything if the reward was collected by the player
        if (permanentlyCollected) {
            return;
        }
        
        if (isActive && !isCollected()) {
            ticksActive++;
            if (ticksActive >= maxActiveTicks) {
                disappear();
            }
        } else if (!isActive) {
            // Count ticks since disappeared for respawning
            ticksSinceDisappeared++;
            if (ticksSinceDisappeared >= respawnDelayTicks) {
                respawnAtRandomPosition(map, enemies, rewards);
            }
        }
    }


    /**
     * Makes this bonus reward appear on the map at its current position.
     */
    public void appear() {
        this.isActive = true;
        this.setCollected(false);
        this.ticksActive = 0;
        this.ticksSinceDisappeared = 0;
    }

    /**
     * Makes this bonus reward disappear from the map.
     */
    public void disappear() {
        this.isActive = false;
        this.ticksSinceDisappeared = 0;
    }

    /**
     * Respawns the bonus reward at a random valid position on the map.
     * 
     * @param map the game map
     * @param enemies list of enemies to avoid
     * @param rewards list of other rewards to avoid
     */
    private void respawnAtRandomPosition(Map map, java.util.List<game.entity.Enemy> enemies, java.util.List<Reward> rewards) {
        Position newPos = findRandomValidPosition(map, enemies, rewards);
        if (newPos != null) {
            this.position = newPos;
            appear();
        }
    }

    /**
     * Finds a random valid position on the map that doesn't overlap with enemies or other rewards.
     * 
     * @param map the game map
     * @param enemies list of enemies to avoid
     * @param rewards list of other rewards to avoid
     * @return a valid random position, or null if none found after many attempts
     */
    private Position findRandomValidPosition(Map map, java.util.List<game.entity.Enemy> enemies, java.util.List<Reward> rewards) {
        int attempts = 0;
        int maxAttempts = 100;
        
        while (attempts < maxAttempts) {
            int row = 1 + (int)(Math.random() * (map.getHeight() - 2));
            int col = 1 + (int)(Math.random() * (map.getWidth() - 2));
            Position pos = new Position(row, col);
            
            // Check if position is passable and not entry/exit
            if (!map.isPassable(pos) || map.isEntry(pos) || map.isExit(pos)) {
                attempts++;
                continue;
            }
            
            // Check if position overlaps with any enemy
            boolean overlapsEnemy = false;
            for (game.entity.Enemy enemy : enemies) {
                if (enemy.getPosition().equals(pos)) {
                    overlapsEnemy = true;
                    break;
                }
            }
            if (overlapsEnemy) {
                attempts++;
                continue;
            }
            
            // Check if position overlaps with any other reward
            boolean overlapsReward = false;
            for (Reward reward : rewards) {
                // Skip checking against itself
                if (reward == this) {
                    continue;
                }
                // For bonus rewards, only check if they're active
                if (reward instanceof BonusReward) {
                    BonusReward bonusReward = (BonusReward) reward;
                    if (bonusReward.isActive() && bonusReward.getPosition().equals(pos)) {
                        overlapsReward = true;
                        break;
                    }
                } else {
                    // For regular rewards, check if not collected
                    if (!reward.isCollected() && reward.getPosition().equals(pos)) {
                        overlapsReward = true;
                        break;
                    }
                }
            }
            if (overlapsReward) {
                attempts++;
                continue;
            }
            
            // Position is valid!
            return pos;
        }
        
        // If we couldn't find a position, return current position as fallback
        return this.position;
    }

    /**
     * Checks if this bonus reward is currently active and visible.
     *
     * @return true if the reward is active, false otherwise
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Gets the total number of bonus rewards created in the game.
     *
     * @return the total bonus reward count
     */
    public static int getTotalBonusRewards() {
        return totalBonusRewards;
    }

    /**
     * Gets the total number of bonus rewards collected by the player.
     *
     * @return the total bonus collected count
     */
    public static int getCollectedBonusRewards() {
        return collectedBonusRewards;
    }

    /**
     * Resets the total bonus rewards and collected counters to zero.
     */
    public static void resetBonusCounters() {
        totalBonusRewards = 0;
        collectedBonusRewards = 0;
    }

    /**
     * Handles collection of this bonus reward by the player.
     *
     * @param player the player who collected this reward
     */
    @Override
    public void onCollect(Player player) {
        if (isActive && !isCollected()) {
            this.setCollected(true);
            this.permanentlyCollected = true;  // Mark as permanently collected
            collectedBonusRewards++;
            applyTo(player);
            disappear();
        }
    }
}