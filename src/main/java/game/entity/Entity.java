package entity;

import game.map.Position;

public abstract class Entity {
    protected Position position;

    public Position getPosition() {
      return position;
    }

    public void setPosition(Position p) {
      position = p;
    }
}