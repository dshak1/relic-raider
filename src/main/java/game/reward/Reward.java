package game.reward;

import game.entity.Entity;
import game.entity.Player;
import game.map.Position;

/**
 * Abstract base class for collectible items that provide score bonuses.
 * Rewards can be basic (required) or bonus (optional), and may respawn.
 */
public abstract class Reward extends Entity {

    private int value;
    private boolean isBonus;
    private boolean collected;
    private boolean respawnable;

    private static int totalRewards = 0;
    private static int totalCollected = 0;

    /**
     * Constructs a new reward with specified properties.
     *
     * @param position the position of this reward on the map
     * @param value the point value of this reward
     * @param isBonus true if this is a bonus reward
     * @param respawnable true if this reward can respawn
     */
    public Reward(
        Position position,
        int value,
        boolean isBonus,
        boolean respawnable
    ) {
        this.position = position;
        this.value = value;
        this.isBonus = isBonus;
        this.respawnable = respawnable;
        this.collected = false;
        totalRewards++;
    }

    /**
     * Gets the point value of this reward.
     *
     * @return the reward's point value
     */
    public int getValue() {
        return value;
    }

    /**
     * Checks if this is a bonus reward.
     *
     * @return true if this is a bonus reward, false if basic
     */
    public boolean isBonus() {
        return isBonus;
    }

    /**
     * Checks if this reward has been collected.
     *
     * @return true if collected, false otherwise
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Sets the collected status of this reward.
     *
     * @param collected true if collected, false otherwise
     */
    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    /**
     * Checks if this reward can respawn after being collected.
     *
     * @return true if respawnable, false otherwise
     */
    public boolean isRespawnable() {
        return respawnable;
    }

    /**
     * Gets the total number of rewards created in the game.
     *
     * @return the total reward count
     */
    public static int getTotalRewards() {
        return totalRewards;
    }

    /**
     * Gets the total number of rewards collected by the player.
     *
     * @return the total collected count
     */
    public static int getTotalCollected() {
        return totalCollected;
    }

    /**
     * Resets the total rewards and collected counters to zero.
     * Useful when starting a new game.
     */
    public static void resetCounters() {
        totalRewards = 0;
        totalCollected = 0;
    }

    /**
     * Applies this reward's effect to the player when collected.
     * Default implementation marks the reward as collected and increments the counter.
     *
     * @param player the player collecting this reward
     */
    public void applyTo(Player player) {
        if (!this.collected) {
            this.collected = true;
            totalCollected++;
        }
    }

    /**
     * Called when the player collects this reward.
     * Subclasses must implement specific collection behavior.
     *
     * @param player the player who collected this reward
     */
    public abstract void onCollect(Player player);
}
