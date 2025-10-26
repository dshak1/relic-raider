package game.reward;

import game.entity.Player;
import game.map.Position;
import java.time.Duration;

/**
 * High-value optional reward that can appear, disappear, and respawn.
 */
public class BonusReward extends Reward {

    private boolean isActive;
    private boolean isCollected;
    private Duration respawnInterval;

    private static int totalBonusRewards = 0;
    private static int collectedBonusRewards = 0;

    /**
     * Constructs a new bonus reward at the specified position.
     *
     * @param position the position of this reward on the map
     * @param value the point value awarded when collected
     * @param respawnInterval how long until respawn, or null if non-respawnable
     */
    public BonusReward(Position position, int value, Duration respawnInterval) {
        super(position, value, true, respawnInterval != null);
        this.isActive = true;
        this.isCollected = false;
        this.respawnInterval = respawnInterval;
        totalBonusRewards++;
    }

    /**
     * Makes this bonus reward appear on the map.
     */
    public void appear() {
        this.isActive = true;
        this.isCollected = false;
    }

    /**
     * Makes this bonus reward disappear from the map.
     */
    public void disappear() {
        this.isActive = false;
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
     * Checks if this bonus reward has been collected.
     *
     * @return true if collected, false otherwise
     */
    public boolean isCollected() {
        return isCollected;
    }

    /**
     * Gets the respawn interval for this reward.
     *
     * @return how long until respawn, or null if non-respawnable
     */
    public Duration getRespawnInterval() {
        return respawnInterval;
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
        if (isActive && !isCollected) {
            this.isCollected = true;
            collectedBonusRewards++;
            applyTo(player);
            disappear();
        }
    }

    /**
     * Checks if this bonus reward should respawn and handles respawn logic.
     * Should be called periodically by the game loop.
     */
    public void onRespawnCheck() {
        if (isCollected && respawnInterval != null) {
            appear();
        }
    }
}
