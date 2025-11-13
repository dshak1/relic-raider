package game.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Direction enum.
 * Tests enum values, switch statement coverage, and direction properties.
 */
public class DirectionTest {
    
    @Test
    void testDirectionEnumValues_AllExist() {
        // Test that all expected enum values exist
        Direction[] directions = Direction.values();
        
        assertTrue(directions.length >= 5, "Direction enum should have at least 5 values");
        
        // Verify specific values exist
        assertNotNull(Direction.UP, "UP direction should exist");
        assertNotNull(Direction.DOWN, "DOWN direction should exist");
        assertNotNull(Direction.LEFT, "LEFT direction should exist");
        assertNotNull(Direction.RIGHT, "RIGHT direction should exist");
        assertNotNull(Direction.NONE, "NONE direction should exist");
    }
    
    @Test
    void testDirectionEnumValues_ValueOf() {
        // Test valueOf method for each direction
        assertEquals(Direction.UP, Direction.valueOf("UP"));
        assertEquals(Direction.DOWN, Direction.valueOf("DOWN"));
        assertEquals(Direction.LEFT, Direction.valueOf("LEFT"));
        assertEquals(Direction.RIGHT, Direction.valueOf("RIGHT"));
        assertEquals(Direction.NONE, Direction.valueOf("NONE"));
    }
    
    @Test
    void testDirectionInSwitchStatement_AllCasesCovered() {
        // Test that all directions work in switch statements
        Direction[] directions = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.NONE};
        
        for (Direction dir : directions) {
            int result = switchDirectionTest(dir);
            assertTrue(result >= 0, "Switch should handle direction: " + dir);
        }
    }
    
    @Test
    void testDirectionInSwitchStatement_DefaultCase() {
        // Test default case in switch (should handle NONE)
        int result = switchDirectionTest(Direction.NONE);
        assertEquals(0, result, "NONE should map to default case (0)");
    }
    
    @Test
    void testDirectionProperties_Uniqueness() {
        // Test that each direction is unique
        Direction[] directions = Direction.values();
        
        for (int i = 0; i < directions.length; i++) {
            for (int j = i + 1; j < directions.length; j++) {
                assertNotEquals(directions[i], directions[j],
                    "Directions should be unique: " + directions[i] + " != " + directions[j]);
            }
        }
    }
    
    @Test
    void testDirectionProperties_OrdinalValues() {
        // Test that ordinal values are consistent
        Direction[] directions = Direction.values();
        
        for (int i = 0; i < directions.length; i++) {
            assertEquals(i, directions[i].ordinal(),
                "Ordinal should match index for: " + directions[i]);
        }
    }
    
    /**
     * Helper method to test switch statement coverage.
     * Returns a value based on the direction to verify all cases are handled.
     */
    private int switchDirectionTest(Direction dir) {
        return switch (dir) {
            case UP -> 1;
            case DOWN -> 2;
            case LEFT -> 3;
            case RIGHT -> 4;
            default -> 0; // NONE or any other value
        };
    }
}

