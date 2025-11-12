package game.entity;

import game.behaviour.AStarPathfinding;
import game.core.Direction;
import game.map.Map;
import game.map.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link game.entity.MobileEnemy} class.
 *
 * Covers construction, movement, interaction with the player, and
 * respect for map obstacles.
 */
public class MobileEnemyTest {
    private MobileEnemy enemy;
    private Map map;

    @BeforeEach
    public void setUp() {
        // create a map and mobile enemy for testing
        map = new Map(10, 10);
        enemy = new MobileEnemy("mobile_enemy_test", 1, new Position(5, 5), new AStarPathfinding());
    }

    @Test
    public void testEnemyConstruction() {
        Position startPos = new Position(4, 4);
        MobileEnemy testEnemy = new MobileEnemy("mobile_enemy_test", 1, startPos, new AStarPathfinding());;

        // assert that the new player is at the startPos
        assertEquals(startPos.getRow(), testEnemy.getPosition().getRow());
        assertEquals(startPos.getCol(), testEnemy.getPosition().getCol());
    }

    @Test
    public void testDamageGetterSetter() {
        // test initial damage from construction
        assertEquals(1, enemy.getDamage());
        
        // test setting damage
        enemy.setDamage(5);
        assertEquals(5, enemy.getDamage());
        
        // test setting higher damage
        enemy.setDamage(100);
        assertEquals(100, enemy.getDamage());
    }

    @Test
    public void testOnContactSetsPlayerNotAlive() {
        Player player = new Player(new Position(5, 5));
        assertTrue(player.isAlive());
        
        // enemy contacts player, should kill the player
        enemy.onContact(player);
        
        assertFalse(player.isAlive());
    }

    @Test
    public void testMoveTo() {
        Position newPos = new Position(6, 6);
        enemy.moveTo(newPos);
        
        // assert that the position changed to the new one
        assertEquals(newPos.getRow(), enemy.getPosition().getRow());
        assertEquals(newPos.getCol(), enemy.getPosition().getCol());
    }

    @Test
    public void testDecideNextWithBlockedTile() {
        // block the tile to the right of the enemy
        Position blockedPos = new Position(5, 6);
        map.getTile(blockedPos).setBlocked(true);
        
        // check enemy is at (5, 5)
        assertEquals(5, enemy.getPosition().getRow());
        assertEquals(5, enemy.getPosition().getCol());
        
        // verify the tile is blocked
        assertTrue(map.getTile(blockedPos).isBlocked());
        
        // run decideNext multiple times, if it tries to move to blocked tile
        // the enemy should stay at current position
        for (int i = 0; i < 10; i++) {
            Position next = enemy.decideNext(map, Direction.NONE);
            
            // should not move to a blocked tile
            if (!next.equals(enemy.getPosition())) {
                assertFalse(map.getTile(next).isBlocked());
            }
        }
    }

    @Test
    public void testDecideNextAvoidsBordersWithPathfinding() {
        // place enemy at (5, 5) and player at (5, 3)
        Position enemyPos = new Position(5, 5);
        Position playerPos = new Position(5, 3);
        MobileEnemy pathfindingEnemy = new MobileEnemy("pathfinder", 1, enemyPos, new AStarPathfinding());
        
        // block the direct path at (5, 4)
        map.getTile(new Position(5, 4)).setBlocked(true);
        
        Position nextPos = pathfindingEnemy.decideNext(map, playerPos);
        
        // enemy should not move to the blocked tile
        assertNotEquals(new Position(5, 4), nextPos);
        
        // enemy should either stay in place or move to an alternative path
        assertTrue(map.isPassable(nextPos) || nextPos.equals(enemyPos));
    }
}
