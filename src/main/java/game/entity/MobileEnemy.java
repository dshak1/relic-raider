package entity;

import game.map.Map;
import game.map.Position;
import game.map.Direction;

import java.util.List;

import game.behaviour.PathfindingStrategy;

public class MobileEnemy extends Enemy implements Movable {
    private PathfindingStrategy pathfinder;

    public MobileEnemy(String id, int damage, Position p, PathfindingStrategy pf) {
        this.id = id;
        setDamage(damage);
        setPosition(p);
        this.pathfinder = pf;
    }

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
    
    public void moveTo(Position p) {
        setPosition(p);
    }

    @Override
    public void onContact(Player p) {
        p.setAlive(false);
    }
}
