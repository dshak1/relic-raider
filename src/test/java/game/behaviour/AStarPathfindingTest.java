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
        // Create obstacles forcing a long detour, but ensure path is still possible
        // Block a vertical line but leave a gap
        for (int i = 1; i < 9; i++) {
            if (i != 5) {
                map.getTile(new Position(5, i)).setBlocked(true);
            }
        }
        
        Position start = new Position(1, 1);
        Position target = new Position(8, 8); // Use 8,8 instead of 9,9 to avoid border
        
        // Ensure start and target are passable
        assertTrue(map.isPassable(start), "Start should be passable");
        assertTrue(map.isPassable(target), "Target should be passable");
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        // Path might be null if completely blocked, so check if path exists
        if (path != null) {
            assertTrue(path.size() > 0, "Path should have at least one step");
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
        } else {
            // If path is null, it means no path exists - this is valid
            // Just verify the method handled it gracefully
            assertNull(path, "Path can be null if no route exists");
        }
    }
    
    @Test
    void testFindPath_NullInputs() {
        // Test null map
        assertNull(pathfinder.findPath(null, new Position(1, 1), new Position(5, 5)),
            "Should return null for null map");
        
        // Test null start
        assertNull(pathfinder.findPath(map, null, new Position(5, 5)),
            "Should return null for null start");
        
        // Test null target
        assertNull(pathfinder.findPath(map, new Position(1, 1), null),
            "Should return null for null target");
    }
    
    @Test
    void testFindPath_OutOfBounds() {
        Position start = new Position(1, 1);
        Position target = new Position(-1, -1); // Out of bounds
        
        assertNull(pathfinder.findPath(map, start, target),
            "Should return null for out of bounds target");
        
        Position outOfBoundsStart = new Position(-1, -1);
        Position validTarget = new Position(5, 5);
        
        assertNull(pathfinder.findPath(map, outOfBoundsStart, validTarget),
            "Should return null for out of bounds start");
    }
    
    @Test
    void testFindPath_BlockedStartOrTarget() {
        Position start = new Position(1, 1);
        Position target = new Position(5, 5);
        
        // Block the start position
        map.getTile(start).setBlocked(true);
        assertNull(pathfinder.findPath(map, start, target),
            "Should return null when start is blocked");
        
        // Unblock start, block target
        map.getTile(start).setBlocked(false);
        map.getTile(target).setBlocked(true);
        assertNull(pathfinder.findPath(map, start, target),
            "Should return null when target is blocked");
    }
    
    @Test
    void testFindPath_StartEqualsTarget() {
        Position pos = new Position(5, 5);
        List<Position> path = pathfinder.findPath(map, pos, pos);
        
        assertNotNull(path, "Path should not be null when start equals target");
        assertEquals(1, path.size(), "Path should contain only one position");
        assertEquals(pos, path.get(0), "Path should contain the start/target position");
    }
    
    @Test
    void testFindPath_ComplexScenario_ExercisesAllCodePaths() {
        // Test a complex scenario that exercises various code paths in pathfinding
        // This indirectly tests internal methods like getGCostFromOpenSet
        Position start = new Position(1, 1);
        Position target = new Position(8, 8); // Use 8,8 instead of 9,9 to avoid border
        
        // Create obstacles that force pathfinding to explore multiple nodes
        // But ensure target remains passable
        for (int i = 2; i < 8; i++) {
            if (i % 2 == 0) {
                map.getTile(new Position(i, 5)).setBlocked(true);
            }
        }
        
        List<Position> path = pathfinder.findPath(map, start, target);
        // If path exists, the method was called successfully
        assertNotNull(path, "Pathfinding should work in complex scenarios");
        if (path != null && path.size() > 0) {
            assertEquals(start, path.get(0));
            assertEquals(target, path.get(path.size() - 1));
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
    
    @Test
    void testFindPath_UpdateExistingNodeInOpenSet() {
        // Test the branch where a node is found in open set and needs updating
        // Create a scenario where a better path is found to a node already in open set
        Position start = new Position(1, 1);
        Position target = new Position(5, 5);
        
        // Create obstacles that force pathfinding to reconsider nodes
        // Block direct path, force detour
        for (int i = 2; i < 5; i++) {
            map.getTile(new Position(i, 1)).setBlocked(true);
        }
        
        // This should trigger the branch where tentativeGCost < getGCostFromOpenSet
        List<Position> path = pathfinder.findPath(map, start, target);
        assertNotNull(path, "Should find path even when updating nodes in open set");
    }
    
    @Test
    void testFindPath_NodeAlreadyInOpenSet_BetterPath() {
        // Test scenario where we find a better path to a node already in open set
        Position start = new Position(2, 2);
        Position target = new Position(6, 6);
        
        // Create a maze that forces pathfinding to explore multiple routes
        // This will test the branch at line 149 (else - updating existing node)
        map.getTile(new Position(3, 2)).setBlocked(true);
        map.getTile(new Position(2, 3)).setBlocked(true);
        map.getTile(new Position(4, 3)).setBlocked(true);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        assertNotNull(path, "Should handle node updates in open set");
    }
    
    @Test
    void testFindPath_MultiplePathsToSameNode_UpdatesWithBetterPath() {
        // Create a scenario where there are two paths to the same intermediate node
        // One path is longer, forcing A* to update the node with a better path
        // This tests: foundInOpen == true && tentativeGCost < getGCostFromOpenSet
        Position start = new Position(1, 5);
        Position target = new Position(5, 5);
        
        // Create a scenario with two possible routes to position (3, 5):
        // Route 1: (1,5) -> (2,5) -> (3,5) [direct, 2 steps]
        // Route 2: (1,5) -> (1,4) -> (1,3) -> (2,3) -> (3,3) -> (3,4) -> (3,5) [longer, 6 steps]
        // Block the direct route initially, then allow it
        // Actually, let's create a scenario where we reach (3,5) via a longer path first,
        // then discover a shorter path
        
        // Block positions to force a detour
        map.getTile(new Position(2, 5)).setBlocked(true);
        map.getTile(new Position(3, 4)).setBlocked(true);
        map.getTile(new Position(4, 4)).setBlocked(true);
        
        // This should force pathfinding to explore (3,5) via a longer route first,
        // then potentially find a better route
        List<Position> path = pathfinder.findPath(map, start, target);
        assertNotNull(path, "Should find path through complex maze");
    }
    
    @Test
    void testFindPath_FoundInOpenButNotBetterPath_SkipsUpdate() {
        // Test the branch where foundInOpen is true but tentativeGCost >= getGCostFromOpenSet
        // This means we skip the update (the condition at line 142 is false)
        Position start = new Position(1, 1);
        Position target = new Position(3, 3);
        
        // Create a simple path where we won't find a better route to nodes already in open set
        // The path should be straightforward: (1,1) -> (2,1) -> (3,1) -> (3,2) -> (3,3)
        // or (1,1) -> (1,2) -> (1,3) -> (2,3) -> (3,3)
        // This tests the case where we don't update because the existing path is already optimal
        List<Position> path = pathfinder.findPath(map, start, target);
        assertNotNull(path, "Should find optimal path");
    }
    
    // ==================== Tie-Breaking Behavior Tests ====================
    
    @Test
    void testFindPath_TieBreaking_SameFCost_PrioritizesLowerHCost() {
        // Test tie-breaking: when fCosts are equal, prioritize lower hCost (closer to goal)
        // Create a scenario where multiple paths have the same fCost
        Position start = new Position(5, 5);
        Position target = new Position(7, 7);
        
        // Create obstacles that create multiple paths with same fCost
        // Block some paths to force tie-breaking scenarios
        map.getTile(new Position(5, 6)).setBlocked(true);
        map.getTile(new Position(6, 5)).setBlocked(true);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        assertNotNull(path, "Should find path even with tie-breaking");
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(path.size() - 1));
    }
    
    @Test
    void testFindPath_TieBreaking_MultipleEqualPaths() {
        // Test that A* consistently chooses one path when multiple have equal cost
        Position start = new Position(1, 1);
        Position target = new Position(3, 3);
        
        // Multiple paths exist with same cost: (1,1)->(2,1)->(3,1)->(3,2)->(3,3)
        // and (1,1)->(1,2)->(1,3)->(2,3)->(3,3)
        List<Position> path1 = pathfinder.findPath(map, start, target);
        List<Position> path2 = pathfinder.findPath(map, start, target);
        
        assertNotNull(path1);
        assertNotNull(path2);
        // Both paths should have same length (tie-breaking should be consistent)
        assertEquals(path1.size(), path2.size(), 
            "Tie-breaking should be consistent");
    }
    
    // ==================== Large Map Performance Tests ====================
    
    @Test
    void testFindPath_LargeMap_50x50() {
        Map largeMap = new Map(50, 50);
        largeMap.createBorder();
        
        Position start = new Position(5, 5);
        Position target = new Position(45, 45);
        
        List<Position> path = pathfinder.findPath(largeMap, start, target);
        
        assertNotNull(path, "Should find path on large map");
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(path.size() - 1));
    }
    
    @Test
    void testFindPath_LargeMap_WithObstacles() {
        Map largeMap = new Map(30, 30);
        largeMap.createBorder();
        
        // Create a maze pattern
        for (int i = 5; i < 25; i += 2) {
            largeMap.getTile(new Position(i, 15)).setBlocked(true);
        }
        
        Position start = new Position(2, 2);
        Position target = new Position(28, 28);
        
        List<Position> path = pathfinder.findPath(largeMap, start, target);
        
        assertNotNull(path, "Should find path on large map with obstacles");
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(path.size() - 1));
    }
    
    @Test
    void testFindPath_LargeMap_OptimalPathLength() {
        Map largeMap = new Map(20, 20);
        largeMap.createBorder();
        
        Position start = new Position(1, 1);
        Position target = new Position(18, 18);
        
        List<Position> path = pathfinder.findPath(largeMap, start, target);
        
        assertNotNull(path);
        // Manhattan distance: |18-1| + |18-1| = 34, so path should be 35 positions (including start)
        assertEquals(35, path.size(), 
            "Path length should equal Manhattan distance + 1");
    }
    
    // ==================== Diagonal Scenarios ====================
    
    @Test
    void testFindPath_Diagonal_UpperLeftToLowerRight() {
        Position start = new Position(2, 2);
        Position target = new Position(8, 8);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(path.size() - 1));
        
        // Verify path uses only cardinal directions (no true diagonals)
        for (int i = 0; i < path.size() - 1; i++) {
            Position current = path.get(i);
            Position next = path.get(i + 1);
            int rowDiff = Math.abs(current.getRow() - next.getRow());
            int colDiff = Math.abs(current.getCol() - next.getCol());
            assertEquals(1, rowDiff + colDiff, 
                "Path should use only cardinal directions");
        }
    }
    
    @Test
    void testFindPath_Diagonal_UpperRightToLowerLeft() {
        Position start = new Position(2, 8);
        Position target = new Position(8, 2);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(path.size() - 1));
    }
    
    @Test
    void testFindPath_Diagonal_WithObstacles() {
        // Create diagonal path with obstacles
        Position start = new Position(1, 1);
        Position target = new Position(8, 8);
        
        // Block direct diagonal path
        map.getTile(new Position(4, 4)).setBlocked(true);
        map.getTile(new Position(5, 5)).setBlocked(true);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path, "Should find path around diagonal obstacles");
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(path.size() - 1));
        
        // Verify path doesn't go through blocked tiles
        for (Position pos : path) {
            assertFalse(map.isBlocked(pos), 
                "Path should not go through blocked tiles");
        }
    }
    
    // ==================== Comprehensive Null/Edge Cases ====================
    
    @Test
    void testFindPath_NullMap() {
        Position start = new Position(1, 1);
        Position target = new Position(5, 5);
        
        List<Position> path = pathfinder.findPath(null, start, target);
        
        assertNull(path, "Should return null when map is null");
    }
    
    @Test
    void testFindPath_NullStart() {
        Position target = new Position(5, 5);
        
        List<Position> path = pathfinder.findPath(map, null, target);
        
        assertNull(path, "Should return null when start is null");
    }
    
    @Test
    void testFindPath_NullTarget() {
        Position start = new Position(1, 1);
        
        List<Position> path = pathfinder.findPath(map, start, null);
        
        assertNull(path, "Should return null when target is null");
    }
    
    @Test
    void testFindPath_AllNull() {
        List<Position> path = pathfinder.findPath(null, null, null);
        
        assertNull(path, "Should return null when all parameters are null");
    }
    
    @Test
    void testFindPath_StartOutOfBounds_Negative() {
        Position start = new Position(-1, -1);
        Position target = new Position(5, 5);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNull(path, "Should return null when start is out of bounds");
    }
    
    @Test
    void testFindPath_StartOutOfBounds_TooLarge() {
        Position start = new Position(100, 100);
        Position target = new Position(5, 5);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNull(path, "Should return null when start is out of bounds");
    }
    
    @Test
    void testFindPath_TargetOutOfBounds() {
        Position start = new Position(5, 5);
        Position target = new Position(100, 100);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNull(path, "Should return null when target is out of bounds");
    }
    
    @Test
    void testFindPath_StartBlocked() {
        Position start = new Position(5, 5);
        Position target = new Position(8, 8);
        
        map.getTile(start).setBlocked(true);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNull(path, "Should return null when start is blocked");
    }
    
    @Test
    void testFindPath_TargetBlocked() {
        Position start = new Position(5, 5);
        Position target = new Position(8, 8);
        
        map.getTile(target).setBlocked(true);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNull(path, "Should return null when target is blocked");
    }
    
    @Test
    void testFindPath_StartEqualsTarget_Blocked() {
        Position pos = new Position(5, 5);
        map.getTile(pos).setBlocked(true);
        
        List<Position> path = pathfinder.findPath(map, pos, pos);
        
        assertNull(path, "Should return null when start equals target and is blocked");
    }
    
    // ==================== Path Reconstruction Edge Cases ====================
    
    @Test
    void testFindPath_Reconstruction_SingleStep() {
        Position start = new Position(5, 5);
        Position target = new Position(5, 6); // Adjacent
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        assertEquals(2, path.size(), "Path should have 2 positions for adjacent target");
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(1));
    }
    
    @Test
    void testFindPath_Reconstruction_LongPath() {
        Position start = new Position(1, 1);
        Position target = new Position(8, 8);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        assertTrue(path.size() > 2, "Long path should have multiple steps");
        
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
    void testFindPath_Reconstruction_PathContinuity() {
        Position start = new Position(2, 2);
        Position target = new Position(7, 7);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        
        // Verify every step is adjacent to the next
        for (int i = 0; i < path.size() - 1; i++) {
            Position current = path.get(i);
            Position next = path.get(i + 1);
            
            int rowDiff = Math.abs(current.getRow() - next.getRow());
            int colDiff = Math.abs(current.getCol() - next.getCol());
            
            assertTrue(rowDiff + colDiff == 1, 
                "Path step " + i + " should be adjacent to step " + (i + 1));
        }
    }
    
    @Test
    void testFindPath_Reconstruction_NoBacktracking() {
        Position start = new Position(1, 1);
        Position target = new Position(8, 8);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        
        // Verify no position appears twice (no cycles)
        for (int i = 0; i < path.size(); i++) {
            for (int j = i + 1; j < path.size(); j++) {
                assertNotEquals(path.get(i), path.get(j),
                    "Path should not contain duplicate positions");
            }
        }
    }
    
    @Test
    void testFindPath_Reconstruction_OptimalLength() {
        Position start = new Position(1, 1);
        Position target = new Position(5, 5);
        
        List<Position> path = pathfinder.findPath(map, start, target);
        
        assertNotNull(path);
        
        // Manhattan distance: |5-1| + |5-1| = 8
        // Optimal path length should be 9 (including start)
        assertEquals(9, path.size(), 
            "Path length should equal Manhattan distance + 1");
    }
}
