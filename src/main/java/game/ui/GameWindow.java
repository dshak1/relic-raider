package game.ui;

import javafx.scene.input.KeyEvent;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
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
    private final Stage stage;
    private final Game game;
    private final GameCanvas canvas;
    private final InputController inputController;
    private AnimationTimer timer;

    /**
     * Constructs a new {@code GameWindow} with the given stage and game instance
     *
     * @param stage the main JavaFX stage for the game window
     * @param game  the {@link Game} instance 
     */
    public GameWindow(Stage stage, Game game) {
        this.stage = stage;
        this.game = game;
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
        StackPane root = new StackPane();
        Scene scene = new Scene(root);

        root.getChildren().add(canvas.getCanvas());

        // Bind canvas size to root size (resizable)
        canvas.getCanvas().widthProperty().bind(root.widthProperty());
        canvas.getCanvas().heightProperty().bind(root.heightProperty());

        // Keyboard input
        scene.setOnKeyPressed(this::handleKeyInput);
        scene.setOnKeyReleased(this::handleKeyInput);

        stage.setTitle(GameConfig.GAME_TITLE);
        stage.setScene(scene);
        stage.setResizable(true);
    }


    /**
     * Starts the rendering and updates the time loop using an {@link AnimationTimer}.
     */
    public void start() {
        // Start a new game time
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // get the player's direction for each tick of the game
                game.tick(inputController.getDirection());
                // render the game with each tick
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
