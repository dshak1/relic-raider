package game.ui;

import javafx.scene.input.KeyEvent;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

        scene = new Scene(root);
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
                // Update game logic only at the specified tick rate
                if (now - lastTickTime >= TICK_INTERVAL_NS) {
                    game.tick(inputController.getDirection());
                    lastTickTime = now;
                }
                
                // Render every frame for smooth visuals
                render();
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
    }

    /**
     * Renders the current game state using the {@link GameCanvas}.
     */
    public void render() {
        canvas.draw(game);
    }
}
