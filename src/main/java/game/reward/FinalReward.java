package game.reward;

import game.entity.Player;
import game.map.Position;

/**
 * Special final reward that unlocks the exit door when collected.
 * Collecting this reward is equivalent to collecting all basic rewards for win condition.
 * This is the golden idol/relic reward that serves as an alternative win path.
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
     * Marks the reward as collected for removal from the game.
     *
     * @param player the player who collected this reward
     */
    @Override
    public void onCollect(Player player) {
        applyTo(player);
    }
}
