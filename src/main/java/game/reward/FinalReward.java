package game.reward;

import game.entity.Player;
import game.map.Position;

/**
 * The final reward (golden idol) that unlocks the exit door.
 * This is the only reward required to win the game.
 * Other rewards are optional and provide extra points.
 */
public class FinalReward extends Reward {

    /**
     * Constructs a new final reward at the specified position.
     * Final rewards are not bonus items and cannot respawn.
     *
     * @param position the position of this reward on the map
     * @param value the point value awarded when collected
     */
    public FinalReward(Position position, int value) {
        super(position, value, false, false);
    }

    /**
     * Handles collection of this final reward by the player.
     * Marks the reward as collected, which unlocks the exit door.
     *
     * @param player the player who collected this reward
     */
    @Override
    public void onCollect(Player player) {
        applyTo(player);
    }
}

