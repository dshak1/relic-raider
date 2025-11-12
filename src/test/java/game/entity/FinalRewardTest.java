package game.entity;

import game.map.Position;
import game.reward.FinalReward;
import game.reward.Reward;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FinalRewardTest {
    private FinalReward finalReward;
    private Player player;

    @BeforeEach
    public void setUp() {
        // reset reward counters before each test
        Reward.resetCounters();
        
        finalReward = new FinalReward(new Position(5, 5), 1000);
        player = new Player(new Position(5, 5));
    }

    @Test
    public void testFinalRewardConstruction() {
        Position pos = new Position(3, 3);
        int value = 500;
        FinalReward reward = new FinalReward(pos, value);
        
        // verify the position and values
        assertEquals(pos.getRow(), reward.getPosition().getRow());
        assertEquals(pos.getCol(), reward.getPosition().getCol());
        assertEquals(value, reward.getValue());
    }

    @Test
    public void testFinalRewardIsNotRespawnable() {
        // final reward shouldn't respawn, once collected, the level ends
        assertFalse(finalReward.isRespawnable());
    }

    @Test
    public void testFinalRewardInitiallyNotCollected() {
        assertFalse(finalReward.isCollected());
    }

    @Test
    public void testFinalRewardMarksAsCollectedOnCollection() {
        assertFalse(finalReward.isCollected());
        
        finalReward.onCollect(player);
        
        assertTrue(finalReward.isCollected());
    }

    @Test
    public void testCollectionTriggersBehavior() {
        // collection should trigger onCollect which applies the effect
        Reward.resetCounters();
        assertEquals(0, Reward.getTotalCollected());
        
        finalReward.onCollect(player);
        
        // total collected should go up by one
        assertEquals(1, Reward.getTotalCollected());
    }

    @Test
    public void testFinalRewardCanBeCollectedByPlayer() {
        // collection should not throw exceptions
        assertDoesNotThrow(() -> {
            finalReward.onCollect(player);
        });
    }

    @Test
    public void testFinalRewardRespawnStatus() {
        // final reward should not respawn
        assertFalse(finalReward.isRespawnable());
    }

}
