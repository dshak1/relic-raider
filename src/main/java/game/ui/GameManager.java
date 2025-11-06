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

    /** Callback invoked when the player wins */
    private Runnable onWin;
    
    /** Callback invoked when the player loses */
    private Runnable onLose;
    
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

        if (game.checkWin()) {
            pauseGame();
            hud.showMessage("You Win!");
            if (onWin != null) onWin.run(); // <-- UI handles showing WinScreen
        } else if (game.checkLose()) {
            pauseGame();
            hud.showMessage("You Lose!");
            if (onLose != null) onLose.run(); // <-- UI handles showing LoseScreen
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
     * The game state remains as is; calling {@link #resumeFromPause()}
     * will resume from the current state without resetting.
     * </p>
     */
    public void pauseGame(){
        running = false;
    }

    /**
     * Resumes the game from where it was paused.
     * <p>
     * Does not reset the game state - just resumes the game loop.
     * </p>
     */
    public void resumeFromPause(){
        running = true;
        // gameLoop is already running, just need to set running flag
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

    /**
     * Sets the callback to execute when the player loses.
     * 
     * @param callback a {@link Runnable} invoked on a loss
     */
    public void setOnWin(Runnable callback) {
        this.onWin = callback;
    }

    /**
     * Sets the callback to execute when the player loses.
     * 
     * @param callback a {@link Runnable} invoked on a loss
     */
    public void setOnLose(Runnable callback) {
        this.onLose = callback;
    }
}
