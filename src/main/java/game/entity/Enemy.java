package game.entity;

/**
 * The abstract {@code Enemy} class represents a harmful entity that can interact with the player in the game.
 * <p>
 * Each enemy has a unique ID and damage amount.
 * </p>
 */
public abstract class Enemy extends Entity {
    protected String id;
    private int damage;
    
    /**
     * Defines what happens when this enemy collides with the player.
     * <p>
     * Subclasses should override this method to implement specific behavior.
     * </p>
     *
     * @param p the player who made contact with this enemy
     */
    public void onContact(Player p) {}
    
    /**
     * Returns the damage value this enemy inflicts.
     *
     * @return the damage amount
     */
    public int getDamage() { return damage; }
    
    /**
     * Sets the damage amount this enemy inflicts.
     * 
     * @param d the damage amount to set
     */
    public void setDamage(int d) { this.damage = d; }
}
