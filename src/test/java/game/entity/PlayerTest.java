package game.entity;

import game.core.Direction;
import game.map.Map;
import game.map.Position;
import game.reward.BasicReward;
import game.reward.BonusReward;
import game.reward.Reward;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Player class.
 * Tests cover construction, movement logic, map boundaries, reward collection, 
 * exit detection, and alive state management.
 */
public class PlayerTest {
    private Player player;
    private Map map;

    @BeforeEach
    public void setUp() {
        // create a map and player for testing
        map = new Map(10, 10);
        player = new Player(new Position(5, 5));
    }

    @Test
    public void testPlayerConstruction() {
        Position startPos = new Position(4, 4);
        Player testPlayer = new Player(startPos);

        // assert that the new player is at the startPos
        assertEquals(startPos.getRow(), testPlayer.getPosition().getRow());
        assertEquals(startPos.getCol(), testPlayer.getPosition().getCol());
    }

    @Test
    public void testPlayerStartsAlive() {
        // assert that the player is alive at the start of the game
        assertTrue(player.isAlive());
    }

    @Test
    public void testDecideNextUp() {
        Position startPos = new Position(5, 5);
        player = new Player(startPos);

        Position nextPos = player.decideNext(map, Direction.UP);

        // assert that the player's new position is 4,5
        assertEquals(4, nextPos.getRow());
        assertEquals(5, nextPos.getCol());
    }

    @Test
    public void testDecideNextDown() {
        Position startPos = new Position(5, 5);
        player = new Player(startPos);

        Position nextPos = player.decideNext(map, Direction.DOWN);

        // assert that the player's new position is 6,5
        assertEquals(6, nextPos.getRow());
        assertEquals(5, nextPos.getCol());
    }

    @Test
    public void testDecideNextLeft() {
        Position startPos = new Position(5, 5);
        player = new Player(startPos);

        Position nextPos = player.decideNext(map, Direction.LEFT);

        // assert that the player's new position is 5,4
        assertEquals(5, nextPos.getRow());
        assertEquals(4, nextPos.getCol());
    }

    @Test
    public void testDecideNextRight() {
        Position startPos = new Position(5, 5);
        player = new Player(startPos);

        Position nextPos = player.decideNext(map, Direction.RIGHT);

        // assert that the player's new position is 5,6
        assertEquals(5, nextPos.getRow());
        assertEquals(6, nextPos.getCol());
    }

    @Test
    public void testDecideNextNoInput() {
        Position startPos = new Position(5, 5);
        player = new Player(startPos);

        Position nextPos = player.decideNext(map, Direction.NONE);

        // assert that the player's position remained the same (default case in decideNex())
        assertEquals(startPos.getRow(), nextPos.getRow());
        assertEquals(startPos.getCol(), nextPos.getCol());
    }

    @Test
    public void testDecideNextBlockedTile() {
        Position startPos = new Position(5, 5);
        player = new Player(startPos);

        // block the tile above the player
        map.getTile(new Position(4, 5)).setBlocked(true);

        Position nextPos = player.decideNext(map, Direction.UP);

        // assert that the player's position remained the same, cannot move to a blocked tile
        assertEquals(startPos.getRow(), nextPos.getRow());
        assertEquals(startPos.getCol(), nextPos.getCol());
    }

    @Test
    public void testDecideNextOutOfBounds() {
        Position topPos = new Position(0, 5);
        player = new Player(topPos);

        Position nextPos = player.decideNext(map, Direction.UP);

        // assert that the player's position remained the same, cannot move outside of the map
        assertEquals(topPos.getRow(), nextPos.getRow());
        assertEquals(topPos.getCol(), nextPos.getCol());
    }
        
    @Test
    public void testCollectBasicReward() {
        BasicReward reward = new BasicReward(new Position(5, 5), 1);
        Reward.resetCounters(); // reset the collected counters to zero

        // assert that the reward has not been collected
        assertFalse(reward.isCollected());

        player.collect(reward);

		// assert that the reward has been collected
        assertTrue(reward.isCollected());
    }
    
    @Test
    public void testCollectBonusReward() {
        BonusReward reward = new BonusReward(new Position(5, 5), 1, 5, 5);
        Reward.resetCounters(); // reset the collected counters to zero

        // assert that the reward has not been collected
        assertFalse(reward.isCollected());

        player.collect(reward);

		// assert that the reward has been collected
        assertTrue(reward.isCollected());

    }
    
    @Test
    public void testMoveToValidPosition() {
        Position startPos = new Position(5, 5);
        Position targetPos = new Position(6, 6);
        player = new Player(startPos);

        player.moveTo(targetPos);

		// assert that the player has moved to the target position
        assertEquals(targetPos.getRow(), player.getPosition().getRow());
        assertEquals(targetPos.getCol(), player.getPosition().getCol());
    }

    @Test
    public void testWhenPlayerIsAtExit() {
        Position exitPos = new Position(5, 5);
        map.setExitPoint(exitPos);
        player.moveTo(exitPos);
				
		// assert that the player is at the exit
        assertTrue(player.atExit(map));
    }

    @Test
    public void testAtExitWhenPlayerIsNotAtExit() {
        Position exitPos = new Position(5, 5);
        Position playerPos = new Position(3, 3);
        map.setExitPoint(exitPos);
        player.moveTo(playerPos);

		// assert that the player is not at the exit
        assertFalse(player.atExit(map));
    }

    @Test
    public void testSetPlayerNotAlive() {
        player.setAlive(false);

		// assert that the player is not alive
        assertFalse(player.isAlive());
    }

    @Test
    public void testSetPlayerAlive() {
        player.setAlive(false);
        // assert that the player is not alive
        assertTrue(!player.isAlive());

        player.setAlive(true);

		// assert that the player is alive
        assertTrue(player.isAlive());
    }

}
