package game.entity;

import game.map.Position;
import game.reward.BasicReward;
import game.reward.Reward;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link game.reward.BasicReward} class.
 *
 * Covers construction and reward collection.
 */
public class BasicRewardTest {
    private BasicReward basicReward;
    private Player player;

    @BeforeEach
    public void setUp() {
        // reset reward counters before each test
        Reward.resetCounters();
        
        basicReward = new BasicReward(new Position(5, 5), 1);
        player = new Player(new Position(5, 5));
    }

    @Test
    public void testBasicRewardProperties() {
        assertFalse(basicReward.isBonus());
        assertFalse(basicReward.isRespawnable());
        assertEquals(1, basicReward.getValue());
    }

    @Test
    public void testOnCollect() {
        Reward.resetCounters();
        BasicReward r = new BasicReward(new Position(2,2), 1);
        assertEquals(1, Reward.getTotalRewards());
        assertEquals(0, Reward.getTotalCollected());

        r.onCollect(player);
        assertTrue(r.isCollected());
        assertEquals(1, Reward.getTotalCollected());
    }
}
