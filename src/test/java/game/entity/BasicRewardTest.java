package game.entity;

import game.map.Position;
import game.reward.BasicReward;
import game.reward.Reward;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}
