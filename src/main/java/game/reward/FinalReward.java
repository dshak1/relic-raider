package game.reward;

import game.entity.Player;
import game.map.Position;
import game.core.Game;

public class FinalReward extends Reward {

    private final Game game;

    public FinalReward(Position position, int value, Game game) {
        super(position, value, true, false); // bonus reward, not respawnable
        this.game = game;
    }

    @Override
    public void onCollect(Player player) {
        // Only allow pickup if all basic rewards are collected
        if (game.getBasicCollected() >= game.getBasicToCollect()) {
            applyTo(player);
            System.out.println("Final reward collected!");
        } else {
            System.out.println("You must collect all basic rewards first!");
        }
    }
}
