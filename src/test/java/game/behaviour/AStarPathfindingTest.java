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
    
    @Test
    void testFindPathTieBreakingBehavior() {
        // Test that when multiple paths have same cost, algorithm chooses consistently
        // Create a scenario with multiple equal-cost paths
        Position start = new Position(1, 1);
        Position target = new Position(1, 5);
        
        // Create obstacles that force path around, but multiple routes exist
        map.getTile(new Position(1, 3)).setBlocked(true);
        
        List<Position> path1 = pathfinder.findPath(map, start, target);
        List<Position> path2 = pathfinder.findPath(map, start, target);
        
        assertNotNull(path1);
        assertNotNull(path2);
        // Both paths should be valid and reach the target
        assertEquals(target, path1.get(path1.size() - 1));
        assertEquals(target, path2.get(path2.size() - 1));
    }
    
    @Test
    void testFindPathLargeMapPerformance() {
        // Test performance on a larger map
        Map largeMap = new Map(50, 50);
        largeMap.createBorder();
        
        Position start = new Position(1, 1);
        Position target = new Position(48, 48);
        
        long startTime = System.currentTimeMillis();
        List<Position> path = pathfinder.findPath(largeMap, start, target);
        long endTime = System.currentTimeMillis();
        
        assertNotNull(path);
        assertTrue(endTime - startTime < 2000, 
            "Pathfinding on large map should complete in under 2 seconds");
        assertEquals(target, path.get(path.size() - 1));
    }
    
    @Test
    void testFindPathLargeMapWithObstacles() {
        // Test large map with many obstacles
        Map largeMap = new Map(30, 30);
        largeMap.createBorder();
        
        // Create a maze-like pattern
        for (int i = 2; i < 28; i += 3) {
            for (int j = 2; j < 28; j++) {
                if (j % 3 != 0) {
                    largeMap.getTile(new Position(i, j)).setBlocked(true);
                }
            }
        }
        
        Position start = new Position(1, 1);
        Position target = new Position(28, 28);
        
        List<Position> path = pathfinder.findPath(largeMap, start, target);
        
        assertNotNull(path);
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(path.size() - 1));
        
        // Verify path doesn't go through blocked tiles
        for (Position pos : path) {
            assertTrue(largeMap.isPassable(pos), 
                "Path should not contain blocked tiles");
        }
    }
    
    @Test
    void testFindPathDiagonalScenario() {
        // Test pathfinding when target is diagonally positioned
        // Even though only cardinal moves are allowed, should find valid path
        Position start = new Position(2, 2);
        Position target = new Position(8, 8);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(path.size() - 1));
        
        // Path should alternate between row and column movements
        // Verify all steps are valid cardinal moves
        for (int i = 0; i < path.size() - 1; i++) {
            Position current = path.get(i);
            Position next = path.get(i + 1);
            
            int rowDiff = Math.abs(current.getRow() - next.getRow());
            int colDiff = Math.abs(current.getCol() - next.getCol());
            
            assertTrue((rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1),
                "Path should only use cardinal directions");
        }
    }
    
    @Test
    void testFindPathMultipleDiagonalPaths() {
        // Test multiple diagonal scenarios
        Position[] starts = {
            new Position(1, 1),
            new Position(1, 8),
            new Position(8, 1),
            new Position(8, 8)
        };
        Position[] targets = {
            new Position(8, 8),
            new Position(8, 1),
            new Position(1, 8),
            new Position(1, 1)
        };
        
        for (int i = 0; i < starts.length; i++) {
            List<Position> path = pathfinder.findPath(map, starts[i], targets[i]);
            assertNotNull(path, "Path should exist for diagonal scenario " + i);
            assertEquals(starts[i], path.get(0));
            assertEquals(targets[i], path.get(path.size() - 1));
        }
    }
    
    @Test
    void testFindPathNullMapEdgeCase() {
        Position start = new Position(2, 2);
        Position target = new Position(5, 5);
        
        assertNull(pathfinder.findPath(null, start, target),
            "Should return null when map is null");
    }
    
    @Test
    void testFindPathNullStartEdgeCase() {
        Position target = new Position(5, 5);
        
        assertNull(pathfinder.findPath(map, null, target),
            "Should return null when start is null");
    }
    
    @Test
    void testFindPathNullTargetEdgeCase() {
        Position start = new Position(2, 2);
        
        assertNull(pathfinder.findPath(map, start, null),
            "Should return null when target is null");
    }
    
    @Test
    void testFindPathAllNullInputs() {
        assertNull(pathfinder.findPath(null, null, null),
            "Should return null when all inputs are null");
    }
    
    @Test
    void testFindPathNegativeCoordinates() {
        Position start = new Position(-5, -5);
        Position target = new Position(5, 5);
        
        assertNull(pathfinder.findPath(map, start, target),
            "Should return null for negative coordinates");
    }
    
    @Test
    void testFindPathExtremeCoordinates() {
        Position start = new Position(1000, 1000);
        Position target = new Position(2000, 2000);
        
        assertNull(pathfinder.findPath(map, start, target),
            "Should return null for coordinates far out of bounds");
    }
    
    @Test
    void testFindPathReconstructionEdgeCase_SingleStep() {
        // Test path reconstruction when start and target are adjacent
        Position start = new Position(2, 2);
        Position target = new Position(2, 3);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        assertEquals(2, path.size(), "Adjacent positions should have 2-step path");
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(1));
    }
    
    @Test
    void testFindPathReconstructionEdgeCase_ThreeSteps() {
        // Test path reconstruction for a 3-step path
        Position start = new Position(2, 2);
        Position target = new Position(2, 4);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        assertEquals(3, path.size(), "Three-step path should have 3 positions");
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(path.size() - 1));
    }
    
    @Test
    void testFindPathReconstructionEdgeCase_ComplexPath() {
        // Test path reconstruction through a complex route
        // Create obstacles forcing a long detour
        for (int i = 1; i < 9; i++) {
            if (i != 5) {
                map.getTile(new Position(5, i)).setBlocked(true);
            }
        }
        
        Position start = new Position(1, 1);
        Position target = new Position(9, 9);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        assertTrue(path.size() > 10, "Complex path should have many steps");
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(path.size() - 1));
        
        // Verify path continuity
        for (int i = 0; i < path.size() - 1; i++) {
            Position current = path.get(i);
            Position next = path.get(i + 1);
            int rowDiff = Math.abs(current.getRow() - next.getRow());
            int colDiff = Math.abs(current.getCol() - next.getCol());
            assertEquals(1, rowDiff + colDiff, 
                "Path should be continuous at step " + i);
        }
    }
    
    @Test
    void testFindPathUShapeObstacle() {
        // Test pathfinding around a U-shaped obstacle
        // Create U shape: block tiles around (5,5) except one opening
        map.getTile(new Position(4, 4)).setBlocked(true);
        map.getTile(new Position(4, 5)).setBlocked(true);
        map.getTile(new Position(4, 6)).setBlocked(true);
        map.getTile(new Position(5, 4)).setBlocked(true);
        map.getTile(new Position(5, 6)).setBlocked(true);
        map.getTile(new Position(6, 4)).setBlocked(true);
        map.getTile(new Position(6, 5)).setBlocked(true);
        map.getTile(new Position(6, 6)).setBlocked(true);
        // Leave (5,5) and one side open
        
        Position start = new Position(4, 3);
        Position target = new Position(6, 3);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(path.size() - 1));
    }
    
    @Test
    void testFindPathNarrowCorridor() {
        // Test pathfinding through a narrow corridor
        // Create a narrow passage
        for (int i = 1; i < 9; i++) {
            if (i != 5) {
                map.getTile(new Position(i, 3)).setBlocked(true);
                map.getTile(new Position(i, 5)).setBlocked(true);
            }
        }
        
        Position start = new Position(1, 4);
        Position target = new Position(8, 4);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(path.size() - 1));
        
        // Path should stay in the corridor (column 4)
        for (Position pos : path) {
            assertEquals(4, pos.getCol(), 
                "Path should stay in narrow corridor");
        }
    }
    
    @Test
    void testFindPathOptimalityComparison() {
        // Test that A* finds optimal (shortest) path
        Position start = new Position(1, 1);
        Position target = new Position(1, 8);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        // Optimal path should be exactly 8 steps (7 moves from 1 to 8)
        assertEquals(8, path.size(), 
            "A* should find optimal path length");
    }
}
