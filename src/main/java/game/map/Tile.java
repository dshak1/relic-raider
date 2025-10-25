package game.map;

/**
 * Represents a single tile (cell) on the game board.
 * Each tile has properties that determine its behavior and appearance,
 * including whether it is blocked (wall), an entry point, or an exit point.
 */
public class Tile {

    private boolean blocked;
    private boolean isEntry;
    private boolean isExit;

    /**
     * Constructs a default Tile that is not blocked, not an entry, and not an exit.
     */
    public Tile() {
        this.blocked = false;
        this.isEntry = false;
        this.isExit = false;
    }

    /**
     * Constructs a Tile with specified properties.
     *
     * @param blocked true if this tile blocks movement (wall/barrier)
     * @param isEntry true if this tile is the entry point
     * @param isExit true if this tile is the exit point
     */
    public Tile(boolean blocked, boolean isEntry, boolean isExit) {
        this.blocked = blocked;
        this.isEntry = isEntry;
        this.isExit = isExit;
    }

    /**
     * Checks if this tile is blocked (cannot be traversed).
     *
     * @return true if the tile is blocked, false otherwise
     */
    public boolean isBlocked() {
        return blocked;
    }

    /**
     * Sets whether this tile is blocked.
     *
     * @param blocked true to make this tile impassable, false to make it passable
     */
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    /**
     * Checks if this tile is the entry point where the player starts.
     *
     * @return true if this is the entry tile, false otherwise
     */
    public boolean isEntry() {
        return isEntry;
    }

    /**
     * Sets whether this tile is the entry point.
     *
     * @param isEntry true to mark this as the entry point, false otherwise
     */
    public void setEntry(boolean isEntry) {
        this.isEntry = isEntry;
    }

    /**
     * Checks if this tile is the exit point where the player must reach to win.
     *
     * @return true if this is the exit tile, false otherwise
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Sets whether this tile is the exit point.
     *
     * @param isExit true to mark this as the exit point, false otherwise
     */
    public void setExit(boolean isExit) {
        this.isExit = isExit;
    }

    /**
     * Checks if this tile is passable (not blocked).
     *
     * @return true if entities can move to this tile, false otherwise
     */
    public boolean isPassable() {
        return !blocked;
    }
}
