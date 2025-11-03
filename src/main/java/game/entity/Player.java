package game.entity;

import game.map.Map;
import game.map.Position;
import game.core.Direction;
import game.reward.Reward;
import game.behaviour.Movable;

/**
 * The {@code Player} class represents the user-controlled player in the game.
 * <p>
 * The player moves across the map, collects rewards, and avoids enemies.
 * Movement is determined by directional input and validated against the
 * {@link game.map.Map} layout.
 * </p>
 */
public class Player extends Entity implements Movable {
    private boolean alive = true;
    
    /**
     * Constructs a new player entity.
     *
     * @param p the starting position of the player
     */
    public Player(Position p) {
        setPosition(p);
    }
    
    /**
     * Determines the next valid position based on the input direction and map layout.
     * The player stays in place if the intended tile is blocked or out of bounds.
     *
     * @param map   the current game map
     * @param input the direction the player attempts to move
     * @return      the next valid {@link Position} to move to
     */
    public Position decideNext(Map map, Direction input) {        
        int row = position.getRow();
        int col = position.getCol();

        switch (input) {
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
            default: // no valid user input
                return getPosition();
        }

        Position next = new Position(row, col);

        if (map.inBounds(next) && map.isPassable(next)) {
            return next;
        }

        return getPosition();
    }
    
    /**
     * Moves the player to the given position.
     * 
     * @param p new position of the player
     */
    public void moveTo(Position p) {
        setPosition(p);
    }

    /**
     * Collects the given reward on the player.
     * @param r the reward to collect
     */
    public void collect(Reward r) {
        r.onCollect(this);
    }

    /**
     * Checks whether the player is at the game exit point
     * 
     * @param map the game map
     * @return {@code true} if the player is at the exit, {@code false} otherwise
     */
    public boolean atExit(Map map) {
        return map.isExit(getPosition());
    }

    /**
     * Returns whether the player is still alive.
     *
     * @return {@code true} if the player is alive, {@code false} otherwise
     */
    public boolean isAlive() { return alive; }
    
    /**
     * Updates the player's alive status.
     *
     * @param alive {@code true} if the player is alive, {@code false} if defeated
     */
    public void setAlive(boolean alive) { this.alive = alive; }
}
