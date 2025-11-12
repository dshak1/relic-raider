package game.entity;

import game.map.Position;
import game.map.Map;
import game.reward.BonusReward;
import game.reward.Reward;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link game.reward.BonusReward} class.
 *
 * Covers construction, active/respawn behavior and reward collection
 */
public class BonusRewardTest {
    private BonusReward bonusReward;
    private Player player;

    @BeforeEach
    public void setUp() {
        // reset reward counters before each test
        Reward.resetCounters();
        
        bonusReward = new BonusReward(new Position(5, 5), 1, 5, 5);
        player = new Player(new Position(5, 5));
    }
    
    @Test
    public void testBonusRewardProperties() {
        assertTrue(bonusReward.isBonus());
        assertTrue(bonusReward.isRespawnable());
        assertTrue(bonusReward.isActive());
        assertEquals(1, BonusReward.getTotalBonusRewards());
    }

    @Test
    public void testDisappearsAfterMaxActiveTicks() {
        // create a short-lived bonus reward
        BonusReward shortLived = new BonusReward(new Position(2, 2), 1, 2, 1);
        // tick twice, reward should disappear
        shortLived.tick(new Map(5,5), java.util.Collections.emptyList(), java.util.Collections.emptyList());
        shortLived.tick(new Map(5,5), java.util.Collections.emptyList(), java.util.Collections.emptyList());
        assertFalse(shortLived.isActive());
    }

    @Test
    public void testOnCollect() {
        // reset collected reward counters
        Reward.resetCounters();
        BonusReward reward = new BonusReward(new Position(3,3), 1, 5, 2);
        assertEquals(1, BonusReward.getTotalBonusRewards());
        assertEquals(0, BonusReward.getCollectedBonusRewards());

        // collect the bonus
        reward.onCollect(player);
        assertTrue(reward.isCollected());
        assertEquals(1, BonusReward.getCollectedBonusRewards());
        assertFalse(reward.isActive()); 
    }

    @Test
    public void testRespawnAfterDelay() {
        Map testMap = new Map(6,6);
        BonusReward respawning = new BonusReward(new Position(2,2), 1, 1, 1);
        // after one tick the reward should disappear
        respawning.tick(testMap, java.util.Collections.emptyList(), java.util.Collections.emptyList());
        assertFalse(respawning.isActive());
        // after one more tick (respawn delay) the reward should attempt to respawn and become active
        respawning.tick(testMap, java.util.Collections.emptyList(), java.util.Collections.emptyList());
        assertTrue(respawning.isActive());
    }
    
}
