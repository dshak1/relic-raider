package game.core;

import java.time.Duration;
import java.util.List;

import game.entity.Enemy;
import game.entity.Player;
import game.map.Map;

public class Game {
    private int score;
    private boolean isGameOver;
    private Duration elapsedTime;
    private int basicToCollect;
    private int basicCollected;
    private Map map;
    private Player player;
    private List<Enemy> enemies;
    private List<Reward> rewards;

    public Game(int score, boolean isGameOver, Duration elapsedTime, int basicToCollect, int basicCollected, Map map,
            Player player, List<Enemy> enemies, List<Reward> rewards) {
        this.score = score;
        this.isGameOver = isGameOver;
        this.elapsedTime = elapsedTime;
        this.basicToCollect = basicToCollect;
        this.basicCollected = basicCollected;
        this.map = map;
        this.player = player;
        this.enemies = enemies;
        this.rewards = rewards;
    }

    public void start() {
        
    }

    public void tick(){

    }

    public void resolveCollisions(){

    }

    public boolean checkWin(){

    }

    public boolean checkLose(){

    }

    public void end(){


    }

    public void addReward(){

    }

    public void removeReward(){

    }

    public void shouldRespawn(){

    }
}
