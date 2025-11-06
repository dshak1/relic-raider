package game.ui;

import javafx.scene.input.KeyEvent;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import game.core.Game;

/**
 * The {@code GameWindow} class is responsible for managing the main JavaFX window 
 * and rendering loop of the game.
 * It initializes the display, handles keyboard input events, and delegates all drawing 
 * operations to the {@link GameCanvas}.
 */
public class GameWindow {
    /** The main Stage for the game window */
    private final Stage stage;

    /** Reference to the game being displayed */
    private final Game game;

    /** Canvas used to render the game */
    private final GameCanvas canvas;

    /** Handles player input */
    private final InputController inputController;

    /** AnimationTimer for driving the game loop */
    private AnimationTimer timer;

    /** Scene containing the game canvas */
    private Scene scene;

    /** HUD associated with the game */
    private final HUD hud;

    /** Debug mode for map setup - shows full map and pauses game logic */
    private boolean setupMode = false;

    /** Reference to game manager for coordinating setup mode */
    private GameManager gameManager;
    
    /** Whether the game is currently paused */
    private boolean isPaused = false;
    
    /** Pause screen overlay */
    private PauseScreen pauseScreen;


    /**
     * Constructs a new {@code GameWindow} with the given stage and game instance
     *
     * @param stage the main JavaFX stage for the game window
     * @param game  the {@link Game} instance 
     */
    public GameWindow(Stage stage, Game game) {
        this.stage = stage;
        this.game = game;
        this.hud = new HUD(game);
        game.setHUD(hud); // optional, if Game class needs reference
        this.inputController = new InputController();

        // Configure the Canvas using GameConfig constants
        Canvas canvasfx = new Canvas(GameConfig.TILE_SIZE * game.getMap().getWidth(), GameConfig.TILE_SIZE * game.getMap().getHeight());
        this.canvas = new GameCanvas(game, canvasfx);
        setupScene(canvas);
    }

    /**
     * Sets the game manager reference for setup mode coordination.
     * 
     * @param gameManager the game manager to coordinate with
     */
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Initializes the JavaFX scene and sets up the key input listeners
     */
    private void setupScene(GameCanvas canvas) {
        BorderPane root = new BorderPane();
        Canvas canvasFX = canvas.getCanvas();

        // Put the game in the center
        root.setCenter(canvasFX);

        // Wrap HUD in an HBox with background
        HBox hudContainer = new HBox(hud);
        hudContainer.setStyle("-fx-background-color: rgba(0,0,0,0.6);"); // semi-transparent
        hudContainer.setPadding(new Insets(5));
        hudContainer.setSpacing(10);

        // Put HUD at the top
        root.setTop(hudContainer);

        // Bind canvas size to remaining space
        canvasFX.widthProperty().bind(root.widthProperty());
        canvasFX.heightProperty().bind(root.heightProperty().subtract(hudContainer.heightProperty()));

        // Create pause screen
        pauseScreen = new PauseScreen();
        pauseScreen.hide();
        
        // Set up pause screen callbacks
        pauseScreen.setOnResume(() -> {
            isPaused = false;
            pauseScreen.hide();
            if (gameManager != null) {
                game.resetTimeStamp();
                gameManager.resumeFromPause();
            }
        });
        
        pauseScreen.setOnRestart(() -> {
            isPaused = false;
            pauseScreen.hide();
            if (gameManager != null) {
                gameManager.resumeGame(); // This resets and restarts the game
            }
        });
        
        pauseScreen.setOnMenu(() -> {
            isPaused = false;
            pauseScreen.hide();
            if (gameManager != null) {
                gameManager.pauseGame(); // Stop the game
            }
            if (timer != null) {
                timer.stop(); // Stop the animation timer
            }
            // Return to main menu - this will be handled by GameApp
            stage.close();
        });

        // Use StackPane to overlay pause screen on top
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(root, pauseScreen);

        scene = new Scene(stackPane);
        stage.setScene(scene);
        stage.setResizable(true);

        // Keyboard input
        scene.setOnKeyPressed(this::handleKeyInput);
        scene.setOnKeyReleased(this::handleKeyInput);
    }

    /**
     * Returns the JavaFX {@link Scene} of the game window.
     * 
     * @return the current game {@link Scene}
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Starts the rendering and updates the time loop using an {@link AnimationTimer}.
     */
    public void start() {
        // Start a new game time
        timer = new AnimationTimer() {
            private long lastTickTime = 0;
            private final long TICK_INTERVAL_NS = 1_000_000_000L / GameConfig.GAME_TICK_RATE; // nanoseconds per tick
            
            @Override
            public void handle(long now) {
                // Update game logic only at the specified tick rate and when not in setup mode or paused
                if (!setupMode && !isPaused && now - lastTickTime >= TICK_INTERVAL_NS) {
                    game.tick(inputController.getDirection());
                    lastTickTime = now;
                }
                
                // Render every frame for smooth visuals
                canvas.draw(game, setupMode);
            }
        };
        timer.start();
        stage.show();
    }

    /**
     * Stops the rendering loop and hides the game window
     */
    public void stop() {
        // stop the timer only if it is currently going
        if (timer != null) {
            timer.stop();
        }
        stage.hide();
    }

    /**
     * Handles user keyboard input events by passing them to the {@link InputController}.
     *
     * @param event the {@link KeyEvent} triggered by the user 
     */
    public void handleKeyInput(KeyEvent event) {
        inputController.handleKeyPress(event);
        
        // Check for pause key press (P or ESCAPE)
        if (inputController.isPausePressed()) {
            isPaused = !isPaused;
            System.out.println("Game " + (isPaused ? "PAUSED" : "RESUMED"));
            
            // Show/hide pause screen and update stats
            if (isPaused) {
                pauseScreen.updateStats(game.getScore(), game.getElapsedTime());
                pauseScreen.show();
                if (gameManager != null) {
                    gameManager.pauseGame();
                }
            } else {
                pauseScreen.hide();
                // Resume without resetting (keep current state)
                // Reset timestamp to prevent time jump when resuming
                game.resetTimeStamp();
                if (gameManager != null) {
                    gameManager.resumeFromPause();
                }
            }
        }
        
        // Toggle setup mode with 'M' key
        if (event.getEventType() == KeyEvent.KEY_PRESSED && 
            event.getCode() == javafx.scene.input.KeyCode.M) {
            setupMode = !setupMode;
            System.out.println("Setup Mode: " + (setupMode ? "ON" : "OFF"));
            
            // Pause/resume game manager to freeze enemies
            if (gameManager != null) {
                if (setupMode) {
                    gameManager.pauseGame();
                } else {
                    gameManager.resumeGame();
                }
            }
        }
    }

    /**
     * Renders the current game state using the {@link GameCanvas}.
     */
    public void render() {
        canvas.draw(game, setupMode);
    }
}
