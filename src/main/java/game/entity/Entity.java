package game.entity;

import game.map.Position;

/**
 * The abstract {@code Entity} class represents any object that exists on the game board,
 * such as players, enemies, and rewards.
 * <p>
 * Each entity has a {@link Position} that defines its location on the {@link game.map.Map}.
 * </p>
 */
public abstract class Entity {
    protected Position position;

    /**
     * Returns the current position of this entity.
     *
     * @return the entity's position
     */
    public Position getPosition() {
      return position;
    }

    /**
     * Updates this entity’s position to the given location.
     *
     * @param p the new position to move this entity to
     */
    public void setPosition(Position p) {
      position = p;
    }
}