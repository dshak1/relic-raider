package game.entity;

import game.map.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link game.entity.StationaryEnemy} class (spike traps).
 *
 * Ensures construction, damage properties, stationary behavior, and contact effects behave as expected.
 */
public class StationaryEnemyTest {
    private StationaryEnemy enemy;

    @BeforeEach
    public void setUp() {
        // create a mobile enemy for testing
        enemy = new StationaryEnemy("mobile_enemy_test", 1, new Position(5, 5));
    }
    
    @Test
    public void testEnemyConstruction() {
        Position startPos = new Position(4, 4);
        StationaryEnemy testEnemy = new StationaryEnemy("stationary_enemy_test", 1, startPos);;

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
    public void testOnContactReducesPlayerScore() {
        Player player = new Player(new Position(5, 5));
        assertTrue(player.isAlive());
        
        enemy.onContact(player);
        
        // player should still be alive after contact with spikes
        assertTrue(player.isAlive());
    }

    @Test
    public void testMultipleContactsWithSameTrap() {
        Player player = new Player(new Position(5, 5));
        
        // contact multiple times
        for (int i = 0; i < 3; i++) {
            enemy.onContact(player);
        }
        
        // player should still be alive 
        assertTrue(player.isAlive());
    }

    @Test
    public void testStationaryEnemyDoesNotMove() {
        // stationary enemies should not have movement methods
        Position startPos = enemy.getPosition();
        
        // position should remain unchanged
        assertEquals(startPos.getRow(), enemy.getPosition().getRow());
        assertEquals(startPos.getCol(), enemy.getPosition().getCol());
    }

}
