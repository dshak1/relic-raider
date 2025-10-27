package game.behaviour;

import game.map.Map;
import game.map.Position;
import java.util.List;

/**
 * Interface for pathfinding algorithms used by mobile enemies.
 * Provides methods for finding paths between two positions on a map.
 */
public interface PathfindingStrategy {
    
    /**
     * Finds a path from the start position to the target position.
     * 
     * @param map the game map to navigate
     * @param start the starting position
     * @param target the target position
     * @return a list of positions representing the path, or null if no path exists
     */
    List<Position> findPath(Map map, Position start, Position target);
}
