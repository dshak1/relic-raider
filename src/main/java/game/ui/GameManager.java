package game.ui;

import game.core.Direction;
import game.core.Game;
import javafx.animation.AnimationTimer;


/**
 * Manages the game loop, HUD updates, and user input for the game.ui
 * <p>
 * The GameManager is responsible for starting, pausing, and resuming
 * the game. IT updates the core Game object each tick based on the 
 * current player input and ensures the HUD reflects the latest game state.
 * </p>
 */
public class GameManager {

    /** Reference to the core Game object being managed. */
    private Game game;

    /** Reference to the HUD for displaying score, time, and messages. */
    private HUD hud;

    /** Whether the game loop is currently running. */
    private boolean running = false;

    /** The current direction input by the player. */
    private Direction currentDirection = Direction.NONE;

    /** the JavaFX AnimationTimer that drives the game loop. */
    private AnimationTimer gameLoop;
    
    /**
     * Constructs a new GameManager for the specified game and HUD.
     * <p>
     * Initializes the JavaFX AnimationTimer that handles game ticks
     * and HUD updates.
     * </p>
     * 
     * @param game the Game instance to manage
     * @param hud the HUD to update during the game loop
     */
    public GameManager (Game game, HUD hud){
        this.game = game;
        this.hud = hud;

        gameLoop = new AnimationTimer(){
            private long lastUpdate = 0;

            @Override
            public void handle(long now){
                if (lastUpdate == 0){
                    lastUpdate = now;
                    return;
                }

                double deltaTime = (now - lastUpdate)/1_000_000_000.0;

                if (deltaTime >= (1.0/GameConfig.GAME_TICK_RATE)) {
                    tick();
                    lastUpdate = now;
                }
            }

        };
    }

    /**
     * Performs a single game tick.
     * <p>
     * Updates the Game state based on the current player direction,
     * resolves collisions, checks for win/lose conditions, and updates
     * the HUD. 
     * If the game ends, a message is displayed and the game is paused.
     * </p>
     */
    private void tick() {
        if (!running) return;

        game.tick(currentDirection);

        game.resolveCollisions();
        if (game.checkWin()){
            hud.showMessage("You Win!");
            pauseGame();
        } else if (game.checkLose()){
            hud.showMessage("You Lose!");
            pauseGame();
        }

        hud.update();
    }

    /**
     * Starts the game loop and resets the game state.
     * <p>
     * Clears any existing messages from the HUD and begins processing
     * game ticks at the configured tick rate.
     * </p>
     */
    public void startGame(){
        running = true;
        game.reset();
        hud.clearMessage();
        gameLoop.start();
    }

    /**
     * Pauses the game loop.
     * <p>
     * The game state remains as is; calling {@link #resumeGame()}
     * will restart from the current state
     * </p>
     */
    public void pauseGame(){
        running = false;
    }

    /**
     * Resumes the game from the start.
     * <p>
     * Resets the game state, clears any HUD messages, and restarts the game loop.
     * </p>
     */
    public void resumeGame(){
        game.reset();
        hud.clearMessage();
        startGame();
    }

    /**
     * Sets the current player movement direction.
     * <p>
     * Resets the game state, clears HUD messages, and restarts the game loop.
     * </p>
     * 
     * @param dir the new direction input by the player
     */
    public void setCurrentDirection(Direction dir){
        this.currentDirection = dir;
    }
}
