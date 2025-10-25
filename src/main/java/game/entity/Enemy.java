package entity;

public abstract class Enemy extends Entity {
    protected String id;
    private int damage;
    
    // overridden by subclasses
    public void onContact(Player p) {}
    
    public int getDamage() { return damage; }
    public void setDamage(int d) { this.damage = d; }
}
