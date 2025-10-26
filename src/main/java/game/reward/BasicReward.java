package game.reward;

import game.entity.Player;
import game.map.Position;

/**
 * Standard collectible reward required to unlock the exit.
 * Does not respawn once collected.
 */
public class BasicReward extends Reward {

    /**
     * Constructs a new basic reward at the specified position.
     * Basic rewards are not bonus items and cannot respawn.
     *
     * @param position the position of this reward on the map
     * @param value the point value awarded when collected
     */
    public BasicReward(Position position, int value) {
        super(position, value, false, false);
    }

    /**
     * Handles collection of this basic reward by the player.
     * Marks the reward as collected for removal from the game.
     *
     * @param player the player who collected this reward
     */
    @Override
    public void onCollect(Player player) {
        applyTo(player);
    }
}
