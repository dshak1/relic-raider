package game.entity;

import game.map.Map;
import game.map.Position;
import game.map.Direction;
import game.behaviour.PathfindingStrategy;

import java.util.List;

/**
 * The {@code MobileEnemy} class represents an enemy that moves dynamically
 * across the map, pursuing the player using a pathfinding algorithm.
 * <p>
 * Examples include moving skeletons and boulders that cause immediate defeat upon collision
 * with the player.
 * </p>
 */
public class MobileEnemy extends Enemy implements Movable {
    private PathfindingStrategy pathfinder;

    /**
     * Constructs a new mobile enemy. 
     * 
     * @param id     the enemy's unique identifier
     * @param damage the damage inflicted by this enemy
     * @param p      the starting position of this enemy
     * @param pf     the pathfinding algorithm used for movement
     */
    public MobileEnemy(String id, int damage, Position p, PathfindingStrategy pf) {
        this.id = id;
        setDamage(damage);
        setPosition(p);
        this.pathfinder = pf;
    }

    /**
     * Determines the next valid position based on the player's current position
     * and the pathfinding strategy.
     *
     * @param map            the current game map
     * @param playerPosition the current position of the player
     * @return               the next valid {@link Position} to move to
     */
    public Position decideNext(Map map, Position playerPosition) {
        List<Position> path = pathfinder.findPath(map, getPosition(), playerPosition);

        if (path == null || path.size() < 2) {
            return getPosition(); // don't move if there is no valid path
        }

        Position next = path.get(1);

        if (map.inBounds(next) && map.isPassable(next)) {
            return next; // move if there is a valid path
        }

        return getPosition();
    }
    
    /**
     * Moves the mobile enemy to the given position.
     * 
     * @param p new position of the enemy
     */
    public void moveTo(Position p) {
        setPosition(p);
    }

    /**
     * Handles player contact with this enemy — the player is immediately defeated.
     *
     * @param p the player who collided with this enemy
     */
    @Override
    public void onContact(Player p) {
        p.setAlive(false);
    }
}
