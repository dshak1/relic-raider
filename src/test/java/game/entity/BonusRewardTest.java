package game.entity;

import game.map.Position;
import game.reward.BonusReward;
import game.reward.Reward;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    
}
