package entity;

import game.map.Map;
import game.map.Position;
import game.map.Direction;
import game.entity.Reward;

public class Player extends Entity implements Movable {
    private boolean alive = true;
    
    public Player(Position p) {
        setPosition(p);
    }
    
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
    
    public void moveTo(Position p) {
        setPosition(p);
    }

    public void collect(Reward r) {
        r.onCollect(this);
    }

    public boolean atExit(Map map) {
        return map.isExit(getPosition());
    }

    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
}
