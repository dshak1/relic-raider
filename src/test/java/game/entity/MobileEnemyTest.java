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
    
    @Test
    public void testDecideNext_CallsPathfindingCorrectly() {
        map.createBorder();
        Position enemyPos = new Position(5, 5);
        Position playerPos = new Position(5, 8);
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, new AStarPathfinding());
        
        Position nextPos = enemy.decideNext(map, playerPos);
        
        // Enemy should move toward player using pathfinding
        assertNotNull(nextPos);
        assertTrue(map.inBounds(nextPos));
        assertTrue(map.isPassable(nextPos));
        // Should be closer to player or at least valid
        assertNotEquals(enemyPos, nextPos, "Enemy should move toward player");
    }
    
    @Test
    public void testDecideNext_NoPathAvailable_StaysInPlace() {
        map.createBorder();
        Position enemyPos = new Position(5, 5);
        Position playerPos = new Position(5, 8);
        
        // Block all paths to player
        map.getTile(new Position(5, 6)).setBlocked(true);
        map.getTile(new Position(5, 7)).setBlocked(true);
        map.getTile(new Position(4, 5)).setBlocked(true);
        map.getTile(new Position(4, 6)).setBlocked(true);
        map.getTile(new Position(4, 7)).setBlocked(true);
        map.getTile(new Position(4, 8)).setBlocked(true);
        map.getTile(new Position(6, 5)).setBlocked(true);
        map.getTile(new Position(6, 6)).setBlocked(true);
        map.getTile(new Position(6, 7)).setBlocked(true);
        map.getTile(new Position(6, 8)).setBlocked(true);
        
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, new AStarPathfinding());
        Position nextPos = enemy.decideNext(map, playerPos);
        
        // Enemy should stay in place when no path exists
        assertEquals(enemyPos, nextPos, "Enemy should stay in place when no path exists");
    }
    
    @Test
    public void testDecideNext_PathfindingWithNullPath_StaysInPlace() {
        map.createBorder();
        Position enemyPos = new Position(5, 5);
        Position playerPos = new Position(5, 5); // Same position
        
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, new AStarPathfinding());
        Position nextPos = enemy.decideNext(map, playerPos);
        
        // When path is null or too short, should stay in place
        assertEquals(enemyPos, nextPos, "Enemy should stay when path is null or too short");
    }
    
    @Test
    public void testDecideNext_UpdatesLastHorizontalDirection() {
        map.createBorder();
        Position enemyPos = new Position(5, 5);
        Position playerPos = new Position(5, 8); // Player to the right
        
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, new AStarPathfinding());
        Position nextPos = enemy.decideNext(map, playerPos);
        
        // If enemy moves right, direction should be RIGHT
        if (nextPos.getCol() > enemyPos.getCol()) {
            assertEquals(Direction.RIGHT, enemy.getLastHorizontalDirection(),
                "Last horizontal direction should be RIGHT when moving right");
        } else if (nextPos.getCol() < enemyPos.getCol()) {
            assertEquals(Direction.LEFT, enemy.getLastHorizontalDirection(),
                "Last horizontal direction should be LEFT when moving left");
        }
    }
    
    @Test
    public void testMoveTo_Functionality() {
        Position initialPos = new Position(5, 5);
        Position newPos = new Position(6, 6);
        
        MobileEnemy enemy = new MobileEnemy("test", 1, initialPos, new AStarPathfinding());
        enemy.moveTo(newPos);
        
        assertEquals(newPos, enemy.getPosition(), "Enemy position should update after moveTo");
    }
    
    @Test
    public void testPathfinderDependencyInjection() {
        AStarPathfinding pathfinder = new AStarPathfinding();
        Position enemyPos = new Position(5, 5);
        
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, pathfinder);
        
        assertNotNull(enemy.getPathfinder(), "Pathfinder should be set");
        assertEquals(pathfinder, enemy.getPathfinder(), "Pathfinder should be the injected instance");
    }
    
    @Test
    public void testDecideNext_MovementTowardPlayer() {
        map.createBorder();
        Position enemyPos = new Position(3, 3);
        Position playerPos = new Position(3, 7);
        
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, new AStarPathfinding());
        Position nextPos = enemy.decideNext(map, playerPos);
        
        // Enemy should move toward player (rightward in this case)
        assertTrue(nextPos.getCol() >= enemyPos.getCol(),
            "Enemy should move toward player horizontally");
        assertTrue(map.inBounds(nextPos));
        assertTrue(map.isPassable(nextPos));
    }
    
    @Test
    public void testDecideNext_MovementTowardPlayerVertical() {
        map.createBorder();
        Position enemyPos = new Position(3, 5);
        Position playerPos = new Position(7, 5);
        
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, new AStarPathfinding());
        Position nextPos = enemy.decideNext(map, playerPos);
        
        // Enemy should move toward player (downward in this case)
        assertTrue(nextPos.getRow() >= enemyPos.getRow(),
            "Enemy should move toward player vertically");
        assertTrue(map.inBounds(nextPos));
        assertTrue(map.isPassable(nextPos));
    }
    
    @Test
    public void testDecideNext_OutOfBoundsTarget_HandlesGracefully() {
        map.createBorder();
        Position enemyPos = new Position(5, 5);
        Position playerPos = new Position(-1, -1); // Out of bounds
        
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, new AStarPathfinding());
        Position nextPos = enemy.decideNext(map, playerPos);
        
        // Should handle gracefully - either stay in place or return valid position
        assertNotNull(nextPos);
        assertTrue(map.inBounds(nextPos) || nextPos.equals(enemyPos),
            "Should return valid position or stay in place");
    }
    
    @Test
    public void testDecideNext_BlockedTarget_HandlesGracefully() {
        map.createBorder();
        Position enemyPos = new Position(5, 5);
        Position playerPos = new Position(5, 6);
        
        // Block the target position
        map.getTile(playerPos).setBlocked(true);
        
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, new AStarPathfinding());
        Position nextPos = enemy.decideNext(map, playerPos);
        
        // Should handle gracefully - pathfinding should return null, enemy stays in place
        assertEquals(enemyPos, nextPos, "Enemy should stay in place when target is blocked");
    }
}
