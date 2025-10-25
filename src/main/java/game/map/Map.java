package game.map;

/**
 * Represents the game board/map as a 2D grid of tiles.
 * The map manages the layout of the game world, including walls, barriers,
 * entry and exit points. It provides methods for querying tile properties
 * and validating positions.
 */
public class Map {

    private int width;
    private int height;
    private Tile[][] tiles;
    private Position entryPoint;
    private Position exitPoint;

    /**
     * Constructs a new Map with specified dimensions.
     * Initializes all tiles as passable (not blocked).
     *
     * @param width the width of the map (number of columns)
     * @param height the height of the map (number of rows)
     * @throws IllegalArgumentException if width or height is less than 1
     */
    public Map(int width, int height) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException(
                "Map dimensions must be at least 1x1"
            );
        }

        this.width = width;
        this.height = height;
        this.tiles = new Tile[height][width];

        // Initialize all tiles as default (passable)
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                tiles[row][col] = new Tile();
            }
        }
    }

    /**
     * Gets the width of the map.
     *
     * @return the number of columns in the map
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the map.
     *
     * @return the number of rows in the map
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the entry point position where the player starts.
     *
     * @return the entry point Position, or null if not set
     */
    public Position getEntryPoint() {
        return entryPoint;
    }

    /**
     * Sets the entry point position where the player starts.
     * Also marks the tile at that position as the entry.
     *
     * @param entryPoint the position to set as entry point
     * @throws IllegalArgumentException if position is out of bounds
     */
    public void setEntryPoint(Position entryPoint) {
        if (!inBounds(entryPoint)) {
            throw new IllegalArgumentException(
                "Entry point is out of bounds: " + entryPoint
            );
        }
        this.entryPoint = entryPoint;
        getTile(entryPoint).setEntry(true);
    }

    /**
     * Gets the exit point position where the player must reach to win.
     *
     * @return the exit point Position, or null if not set
     */
    public Position getExitPoint() {
        return exitPoint;
    }

    /**
     * Sets the exit point position where the player must reach to win.
     * Also marks the tile at that position as the exit.
     *
     * @param exitPoint the position to set as exit point
     * @throws IllegalArgumentException if position is out of bounds
     */
    public void setExitPoint(Position exitPoint) {
        if (!inBounds(exitPoint)) {
            throw new IllegalArgumentException(
                "Exit point is out of bounds: " + exitPoint
            );
        }
        this.exitPoint = exitPoint;
        getTile(exitPoint).setExit(true);
    }

    /**
     * Gets the tile at the specified position.
     *
     * @param position the position of the tile to retrieve
     * @return the Tile at the specified position
     * @throws IllegalArgumentException if position is out of bounds
     */
    public Tile getTile(Position position) {
        if (!inBounds(position)) {
            throw new IllegalArgumentException(
                "Position is out of bounds: " + position
            );
        }
        return tiles[position.getRow()][position.getCol()];
    }

    /**
     * Gets the tile at the specified row and column.
     *
     * @param row the row index
     * @param col the column index
     * @return the Tile at the specified coordinates
     * @throws IllegalArgumentException if coordinates are out of bounds
     */
    public Tile getTile(int row, int col) {
        return getTile(new Position(row, col));
    }

    /**
     * Sets a tile at the specified position.
     *
     * @param position the position where to set the tile
     * @param tile the Tile to set at this position
     * @throws IllegalArgumentException if position is out of bounds
     */
    public void setTile(Position position, Tile tile) {
        if (!inBounds(position)) {
            throw new IllegalArgumentException(
                "Position is out of bounds: " + position
            );
        }
        if (tile == null) {
            throw new IllegalArgumentException("Tile cannot be null");
        }
        tiles[position.getRow()][position.getCol()] = tile;
    }

    /**
     * Checks if a position is within the bounds of the map.
     *
     * @param position the position to check
     * @return true if the position is within bounds, false otherwise
     */
    public boolean inBounds(Position position) {
        if (position == null) {
            return false;
        }
        int row = position.getRow();
        int col = position.getCol();
        return row >= 0 && row < height && col >= 0 && col < width;
    }

    /**
     * Checks if a position is the entry point.
     *
     * @param position the position to check
     * @return true if this position is the entry point, false otherwise
     */
    public boolean isEntry(Position position) {
        return inBounds(position) && getTile(position).isEntry();
    }

    /**
     * Checks if a position is the exit point.
     *
     * @param position the position to check
     * @return true if this position is the exit point, false otherwise
     */
    public boolean isExit(Position position) {
        return inBounds(position) && getTile(position).isExit();
    }

    /**
     * Checks if a position is blocked (wall or barrier).
     *
     * @param position the position to check
     * @return true if the position is blocked or out of bounds, false otherwise
     */
    public boolean isBlocked(Position position) {
        if (!inBounds(position)) {
            return true; // Out of bounds positions are considered blocked
        }
        return getTile(position).isBlocked();
    }

    /**
     * Checks if a position is passable (not blocked).
     *
     * @param position the position to check
     * @return true if the position is passable and in bounds, false otherwise
     */
    public boolean isPassable(Position position) {
        return inBounds(position) && getTile(position).isPassable();
    }

    /**
     * Creates a border of blocked tiles around the entire map perimeter.
     * This is useful for creating the outer walls required by the game specification.
     */
    public void createBorder() {
        // Top and bottom walls
        for (int col = 0; col < width; col++) {
            tiles[0][col].setBlocked(true); // Top wall
            tiles[height - 1][col].setBlocked(true); // Bottom wall
        }

        // Left and right walls
        for (int row = 0; row < height; row++) {
            tiles[row][0].setBlocked(true); // Left wall
            tiles[row][width - 1].setBlocked(true); // Right wall
        }
    }

    /**
     * Returns a simple text representation of the map for debugging.
     * Uses '#' for blocked tiles, 'E' for entry, 'X' for exit, and '.' for passable.
     *
     * @return a string representation of the map
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb
            .append("Map [")
            .append(width)
            .append("x")
            .append(height)
            .append("]\n");

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Tile tile = tiles[row][col];
                if (tile.isEntry()) {
                    sb.append('E');
                } else if (tile.isExit()) {
                    sb.append('X');
                } else if (tile.isBlocked()) {
                    sb.append('#');
                } else {
                    sb.append('.');
                }
                sb.append(' ');
            }
            sb.append('\n');
        }

        return sb.toString();
    }
}
