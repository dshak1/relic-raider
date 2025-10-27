package game.entity;

import game.map.Map;
import game.map.Position;
import game.core.Direction;

/**
 * Interface for entities that can move around the game map.
 * Provides methods for movement decision-making and execution.
 */
public interface Movable {
    
    /**
     * Determines the next position this entity should move to.
     * 
     * @param map the current game map
     * @param input the direction input (may be null for AI-controlled entities)
     * @return the next position to move to
     */
    Position decideNext(Map map, Direction input);
    
    /**
     * Moves this entity to the specified position.
     * 
     * @param position the new position to move to
     */
    void moveTo(Position position);
}
