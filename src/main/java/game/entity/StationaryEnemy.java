package entity;

import game.map.Position;

public class StationaryEnemy extends Enemy {
    
    public StationaryEnemy(String id, int damage, Position p) { 
        this.id = id;
        setDamage(damage);
        setPosition(p); 
    }

    @Override
    public void onContact(Player p) {
        // decrease the score
        // if the score is negative, p.setAlive(false) - lose
    }
}
