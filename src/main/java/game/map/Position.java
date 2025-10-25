package game.map;

/**
 * Represents a position on the game board using row and column coordinates.
 * This class provides a simple 2D coordinate system for locating entities,
 * tiles, and other game objects on the map grid.
 */
public class Position {

    private int row;
    private int col;

    /**
     * Constructs a new Position with specified row and column.
     *
     * @param row the row coordinate
     * @param col the column coordinate
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Gets the row coordinate of this position.
     *
     * @return the row coordinate
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column coordinate of this position.
     *
     * @return the column coordinate
     */
    public int getCol() {
        return col;
    }

    /**
     * Sets the row coordinate of this position.
     *
     * @param row the new row coordinate
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Sets the column coordinate of this position.
     *
     * @param col the new column coordinate
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Sets both row and column coordinates from another Position.
     *
     * @param position the Position to copy coordinates from
     */
    public void setPosition(Position position) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        this.row = position.row;
        this.col = position.col;
    }

    /**
     * Returns a copy of this Position object.
     *
     * @return a new Position with the same coordinates
     */
    public Position getPosition() {
        return new Position(this.row, this.col);
    }

    /**
     * Checks if this position is equal to another position.
     * Two positions are equal if they have the same row and column.
     *
     * @param obj the object to compare with
     * @return true if the positions are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return row == position.row && col == position.col;
    }

    /**
     * Returns a string representation of this position.
     *
     * @return string in format "(row, col)"
     */
    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
