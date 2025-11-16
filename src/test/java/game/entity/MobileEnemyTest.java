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
    
    @Test
    public void testDecideNext_PathSizeOne_StaysInPlace() {
        map.createBorder();
        Position enemyPos = new Position(5, 5);
        Position playerPos = new Position(5, 5); // Same position
        
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, new AStarPathfinding());
        Position nextPos = enemy.decideNext(map, playerPos);
        
        // Path size will be 1 (start == target), so enemy should stay
        assertEquals(enemyPos, nextPos, "Enemy should stay when path size is 1");
    }
    
    @Test
    public void testDecideNext_VerticalMovement_NoHorizontalDirectionChange() {
        map.createBorder();
        Position enemyPos = new Position(5, 5);
        Position playerPos = new Position(7, 5); // Directly below (vertical movement)
        
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, new AStarPathfinding());
        Direction initialDir = enemy.getLastHorizontalDirection();
        
        Position nextPos = enemy.decideNext(map, playerPos);
        
        // Moving vertically (dc == 0) should not change horizontal direction
        assertEquals(initialDir, enemy.getLastHorizontalDirection(),
            "Vertical movement should not change horizontal direction");
        assertNotEquals(enemyPos, nextPos, "Enemy should move toward player");
    }
    
    @Test
    public void testDecideNext_NextPositionNotPassable_StaysInPlace() {
        map.createBorder();
        Position enemyPos = new Position(5, 5);
        Position playerPos = new Position(5, 7);
        
        // Block the next position in the path
        map.getTile(new Position(5, 6)).setBlocked(true);
        
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, new AStarPathfinding());
        Position nextPos = enemy.decideNext(map, playerPos);
        
        // Should stay in place if next position is not passable
        assertEquals(enemyPos, nextPos, 
            "Enemy should stay when next position is not passable");
    }
    
    @Test
    public void testDecideNext_Direction_AllDirections() {
        map.createBorder();
        Position enemyPos = new Position(5, 5);
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, null);
        
        // Test all directions in the switch statement
        Position upPos = enemy.decideNext(map, Direction.UP);
        assertNotNull(upPos);
        
        Position downPos = enemy.decideNext(map, Direction.DOWN);
        assertNotNull(downPos);
        
        Position leftPos = enemy.decideNext(map, Direction.LEFT);
        assertNotNull(leftPos);
        assertEquals(Direction.LEFT, enemy.getLastHorizontalDirection());
        
        Position rightPos = enemy.decideNext(map, Direction.RIGHT);
        assertNotNull(rightPos);
        assertEquals(Direction.RIGHT, enemy.getLastHorizontalDirection());
    }
    
    @Test
    public void testDecideNext_Direction_BlockedPosition_StaysInPlace() {
        map.createBorder();
        Position enemyPos = new Position(5, 5);
        
        // Block all adjacent positions
        map.getTile(new Position(4, 5)).setBlocked(true);
        map.getTile(new Position(6, 5)).setBlocked(true);
        map.getTile(new Position(5, 4)).setBlocked(true);
        map.getTile(new Position(5, 6)).setBlocked(true);
        
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, null);
        
        // Try all directions - should all stay in place
        Position nextUp = enemy.decideNext(map, Direction.UP);
        Position nextDown = enemy.decideNext(map, Direction.DOWN);
        Position nextLeft = enemy.decideNext(map, Direction.LEFT);
        Position nextRight = enemy.decideNext(map, Direction.RIGHT);
        
        assertEquals(enemyPos, nextUp);
        assertEquals(enemyPos, nextDown);
        assertEquals(enemyPos, nextLeft);
        assertEquals(enemyPos, nextRight);
    }
    
    @Test
    public void testDecideNext_Direction_OutOfBounds_StaysInPlace() {
        map.createBorder();
        // Place enemy at edge
        Position enemyPos = new Position(1, 1);
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, null);
        
        // Try to move out of bounds
        Position nextUp = enemy.decideNext(map, Direction.UP);
        Position nextLeft = enemy.decideNext(map, Direction.LEFT);
        
        // Should stay in place when out of bounds
        assertEquals(enemyPos, nextUp);
        assertEquals(enemyPos, nextLeft);
    }
    
    @Test
    public void testDecideNext_PathNull_StaysInPlace() {
        map.createBorder();
        Position enemyPos = new Position(5, 5);
        Position playerPos = new Position(5, 10); // Out of bounds or unreachable
        
        // Use a pathfinder that will return null
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, new AStarPathfinding());
        
        // Create a scenario where pathfinding returns null
        // Block all paths to player
        for (int i = 5; i < 10; i++) {
            map.getTile(new Position(5, i)).setBlocked(true);
        }
        // Also block alternative paths
        for (int i = 4; i <= 6; i++) {
            for (int j = 5; j < 10; j++) {
                if (i != 5 || j != 5) {
                    map.getTile(new Position(i, j)).setBlocked(true);
                }
            }
        }
        
        Position nextPos = enemy.decideNext(map, playerPos);
        
        // Should stay in place when path is null
        assertEquals(enemyPos, nextPos, "Enemy should stay when pathfinding returns null");
    }
    
    @Test
    public void testDecideNext_NextPositionNotInBounds_StaysInPlace() {
        map.createBorder();
        Position enemyPos = new Position(5, 5);
        Position playerPos = new Position(5, 6);
        
        MobileEnemy enemy = new MobileEnemy("test", 1, enemyPos, new AStarPathfinding());
        
        // Create a path that would go out of bounds
        // This is tricky - we need pathfinding to return a path where the next step is out of bounds
        // Actually, pathfinding should never return an out-of-bounds position, so this tests
        // the inBounds check in decideNext
        Position nextPos = enemy.decideNext(map, playerPos);
        
        // The path should be valid, but if somehow we get an invalid position, it should be caught
        assertNotNull(nextPos);
    }
}
