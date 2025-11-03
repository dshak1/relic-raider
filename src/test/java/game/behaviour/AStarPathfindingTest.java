package game.behaviour;

import game.map.Map;
import game.map.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for the A* pathfinding algorithm.
 * Tests pathfinding in various scenarios including obstacles, impossible paths, and edge cases.
 */
public class AStarPathfindingTest {
    
    private AStarPathfinding pathfinder;
    private Map map;
    
    @BeforeEach
    void setUp() {
        pathfinder = new AStarPathfinding();
        map = new Map(10, 10);
        map.createBorder();
    }
    
    @Test
    void testFindPathSimple() {
        // Simple path: start to target with no obstacles
        Position start = new Position(2, 2);
        Position target = new Position(2, 5);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(path.size() - 1));
    }
    
    @Test
    void testFindPathDiagonal() {
        // Test diagonal path (though only cardinal moves are allowed)
        Position start = new Position(2, 2);
        Position target = new Position(5, 5);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        assertFalse(path.isEmpty());
        // Path should be valid (steps should be adjacent)
        for (int i = 0; i < path.size() - 1; i++) {
            Position current = path.get(i);
            Position next = path.get(i + 1);
            
            // Positions should be adjacent (1 unit apart)
            int rowDiff = Math.abs(current.getRow() - next.getRow());
            int colDiff = Math.abs(current.getCol() - next.getCol());
            assertTrue(rowDiff + colDiff == 1, 
                "Path steps should be adjacent");
        }
    }
    
    @Test
    void testFindPathWithObstacle() {
        // Create obstacles in the map
        map.setTile(new Position(2, 3), map.getTile(new Position(2, 3)));
        map.getTile(new Position(2, 3)).setBlocked(true);
        map.getTile(new Position(3, 3)).setBlocked(true);
        
        Position start = new Position(2, 2);
        Position target = new Position(2, 5);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        assertFalse(path.isEmpty());
        
        // Verify path doesn't go through blocked tiles
        for (Position pos : path) {
            assertFalse(map.isBlocked(pos), 
                "Path should not go through blocked tiles");
        }
    }
    
    @Test
    void testFindPathImpossible() {
        // Create a wall between start and target
        for (int row = 1; row < 9; row++) {
            map.getTile(new Position(row, 5)).setBlocked(true);
        }
        
        Position start = new Position(2, 2);
        Position target = new Position(2, 8);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNull(path, "Should return null when no path exists");
    }
    
    @Test
    void testFindPathSamePosition() {
        Position pos = new Position(3, 3);
        List<Position> path = pathfinder.findPath(map, pos, pos);
        
        assertNotNull(path);
        assertEquals(1, path.size());
        assertEquals(pos, path.get(0));
    }
    
    @Test
    void testFindPathNullInputs() {
        Position start = new Position(2, 2);
        Position target = new Position(5, 5);
        
        // Null map
        assertNull(pathfinder.findPath(null, start, target));
        
        // Null start
        assertNull(pathfinder.findPath(map, null, target));
        
        // Null target
        assertNull(pathfinder.findPath(map, start, null));
    }
    
    @Test
    void testFindPathOutOfBounds() {
        Position start = new Position(-1, -1);
        Position target = new Position(20, 20);
        
        assertNull(pathfinder.findPath(map, start, target));
    }
    
    @Test
    void testFindPathBlockedStart() {
        Position start = new Position(2, 2);
        Position target = new Position(5, 5);
        
        // Block the start position
        map.getTile(start).setBlocked(true);
        
        assertNull(pathfinder.findPath(map, start, target));
    }
    
    @Test
    void testFindPathBlockedTarget() {
        Position start = new Position(2, 2);
        Position target = new Position(5, 5);
        
        // Block the target position
        map.getTile(target).setBlocked(true);
        
        assertNull(pathfinder.findPath(map, start, target));
    }
    
    @Test
    void testFindPathOptimal() {
        // Test that A* finds an optimal path
        Position start = new Position(1, 1);
        Position target = new Position(1, 8);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        
        // Optimal path should be 7 steps (1 to 8 = 7 moves)
        assertEquals(8, path.size(), "Path should be optimal (no unnecessary detours)");
    }
    
    @Test
    void testFindPathComplexMaze() {
        // Create a complex maze
        map.getTile(new Position(1, 4)).setBlocked(true);
        map.getTile(new Position(2, 4)).setBlocked(true);
        map.getTile(new Position(3, 4)).setBlocked(true);
        map.getTile(new Position(4, 4)).setBlocked(true);
        map.getTile(new Position(5, 4)).setBlocked(true);
        
        Position start = new Position(1, 1);
        Position target = new Position(1, 8);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        assertFalse(path.isEmpty());
        
        // Verify path is valid
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(path.size() - 1));
        
        // All positions should be passable
        for (Position pos : path) {
            assertTrue(map.isPassable(pos), 
                "All path positions should be passable");
        }
    }
    
    @Test
    void testFindPathAroundObstacle() {
        // Single obstacle in the way
        map.getTile(new Position(3, 3)).setBlocked(true);
        
        Position start = new Position(3, 1);
        Position target = new Position(3, 5);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        
        // Path should go around the obstacle
        boolean hasObstacle = path.contains(new Position(3, 3));
        assertFalse(hasObstacle, "Path should not include the blocked position");
    }
    
    @Test
    void testFindPathPerformance() {
        // Test that algorithm completes in reasonable time
        Position start = new Position(1, 1);
        Position target = new Position(8, 8);
        
        long startTime = System.currentTimeMillis();
        List<Position> path = pathfinder.findPath(map, start, target);
        long endTime = System.currentTimeMillis();
        
        assertNotNull(path);
        assertTrue(endTime - startTime < 1000, // Should complete in under 1 second
            "Pathfinding should be efficient");
    }
    
    @Test
    void testPathContinuity() {
        Position start = new Position(2, 2);
        Position target = new Position(7, 7);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        
        // Verify that consecutive positions are adjacent
        for (int i = 0; i < path.size() - 1; i++) {
            Position current = path.get(i);
            Position next = path.get(i + 1);
            
            int rowDiff = Math.abs(current.getRow() - next.getRow());
            int colDiff = Math.abs(current.getCol() - next.getCol());
            
            assertTrue(rowDiff + colDiff == 1, 
                "Path should be continuous (adjacent positions)");
        }
    }
}
