package game.entity;

import game.map.Position;

/**
 * The {@code StationaryEnemy} class represents a non-moving enemy that maintains
 * a fixed position on the game board and inflicts damage upon contact with the player.
 * <p>
 * Examples include spikes place throughout the game board.
 * </p>
 */
public class StationaryEnemy extends Enemy {
    
    /**
     * Constructs a new stationary enemy at a given position.
     *
     * @param id       the unique identifier for this enemy
     * @param damage   the amount of score deducted when touched
     * @param p the position on the map
     */
    public StationaryEnemy(String id, int damage, Position p) { 
        this.id = id;
        setDamage(damage);
        setPosition(p); 
    }

    /**
     * Reduces the player's score upon contact.
     * If the player's score falls below zero, the player loses.
     *
     * @param p the player who collided with this enemy
     */
    @Override
    public void onContact(Player p) {
        // decrease the score
        // if the score is negative, p.setAlive(false) - lose
    }
}
