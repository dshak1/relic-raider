package game.entity;

import game.map.Map;
import game.map.Position;
import game.core.Direction;
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
     * Determines the next position this enemy should move to.
     * Implements the Movable interface.
     * 
     * @param map the current game map
     * @param input the direction input (ignored for AI-controlled enemies)
     * @return the next position to move to
     */
    @Override
    public Position decideNext(Map map, Direction input) {
        // For now, use a simple random movement since we don't have player position
        // In a real implementation, you'd need access to the player's position
        // This is a simplified version that just moves randomly
        Direction[] directions = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
        Direction randomDir = directions[(int) (Math.random() * directions.length)];
        
        int row = getPosition().getRow();
        int col = getPosition().getCol();
        
        switch (randomDir) {
            case UP:
                row -= 1;
                break;
            case DOWN:
                row += 1;
                break;
            case LEFT:
                col -= 1;
                break;
            case RIGHT:
                col += 1;
                break;
            default:
                return getPosition();
        }
        
        Position next = new Position(row, col);
        
        if (map.inBounds(next) && map.isPassable(next)) {
            return next;
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
