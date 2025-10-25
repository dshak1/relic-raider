package game.core;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;

import game.entity.Enemy;
import game.entity.Player;
import game.map.Map;

/**
 * The {@code Game} class represents the main controller for gameplay.
 * It manages the map, player, enemies, rewards, score, and win/loss state.
 * 
 * <p>This class encapsulates the game loop logic and collision handling
 * between the player, enemies, and rewards.</p>
 */
public class Game {
     /** Current total score accumulated by the player. */
    private int score;

     /** True if the game is currently over (win or loss). */
    private boolean isGameOver;

    /** Duration of time since the game started. */
    private Duration elapsedTime;

    /** Total number of basic rewards that must be collected to win. */
    private int basicToCollect;

    /** Number of basic rewards collected so far. */
    private int basicCollected;

    /** The current game map containing tiles and positions. */
    private Map map;

    /** The player character controlled by the user. */
    private Player player;

    /** List of enemies active in the game. */
    private List<Enemy> enemies;

    /** List of rewards placed on the map. */
    private List<Reward> rewards;

    /**
     * Constructs a new {@code Game} instance with the provided map and player.
     *
     * @param map    the map used in this game session
     * @param player the player character instance
     */
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

    /**
     * Starts or restarts the game, resetting timers and status flags.
     */
    public void start() {
        System.out.println("Game Started!");
        isGameOver=false;
        elapsedTime=Duration.ZERO;
    }

    /**
     * Advances the game by one logical step (or frame). 
     * Processes player actions, collisions, and win/loss conditions.
     *
     * @param input optional player input or direction for this frame
     */
    public void tick(Direction input){
        if(isGameOver) return;

        // Process player decision and movement
        player.decideNext(map, input);
        player.moveTo(player.getPosition());

        // Handle entity interactions
        resolveCollisions();

        // Evaluate win/lose conditions
        if (checkWin()){
            end();
            System.out.println("You win!");
        } else if (checkLose()){
            end();
            System.out.println("You lose!");
        }

        // Update time (roughly one frame at 60 FPS)
        elapsedTime = elapsedTime.plusMillis(16);

    }

    /**
     * Handles all collision checks between player, rewards, and enemies.
     * Updates score and state accordingly.
     */
    public void resolveCollisions(){
        //Player-Reward collisions
        Iterator<Reward> iterator = rewards.iterator();
        while (iterator.hasNext()){
            Reward r = iterator.next();
            if (player.getPosition().equals(r.getPosition())){ //dot rule issue, proposed solution to group
                player.collect(r);
                score += r.getValue();
                r.onCollect(player);
            }
        }

        for (Enemy e : enemies){
            if (player.getPosition().equals(e.getPosition())){//another dot rule issue, haven't brought up to team yet.
                e.onContact(player);
            }
        }
    }

    /**
     * Checks whether the player has satisfied all win conditions.
     *
     * @return true if the player reached the exit and collected all required rewards
     */
    public boolean checkWin(){
        return player.atExit(map) && (basicCollected >= basicToCollect);
    }

    /**
     * Checks whether the player has lost the game.
     *
     * @return true if the game is over due to defeat
     */
    public boolean checkLose(){
        return isGameOver;
    }

    /**
     * Ends the current game session, printing a summary message.
     */
    public void end(){
        isGameOver = true;
        System.out.println("Game ended. Final score: " + score);
    }

    /**
     * Adds a reward to the map.
     *
     * @param r the reward to add
     */
    public void addReward(Reward r){
        rewards.add(r);
        if (!r.isBonus()) basicToCollect++;
    }

    /**
     * Removes a reward from the map.
     *
     * @param r the reward to remove
     */
    public void removeReward(Reward r){
        rewards.remove(r);
    }

    public boolean shouldRespawn(Reward r){
        return r.isRespawnable();
    }

    public int getScore() {
        return score;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public Duration getElapsedTime() {
        return elapsedTime;
    }

    public Map getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }

}
