package game.ui;

import javafx.scene.input.KeyEvent;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import game.core.Game;

public class GameWindow {
    private final Stage stage;
    private final Game game;
    private final GameCanvas canvas;
    private final InputController inputController;
    private AnimationTimer timer;

    public GameWindow(Stage stage, Game game) {
        this.stage = stage;
        this.game = game;
        this.inputController = new InputController();

        // Configure the Canvas using GameConfig constants
        Canvas canvasfx = new Canvas(GameConfig.TILE_SIZE * game.getMap().getWidth(), GameConfig.TILE_SIZE * game.getMap().getHeight());
        this.canvas = new GameCanvas(game, canvasfx);
        setupScene(canvas);
    }

    private void setupScene(GameCanvas canvas) {
        StackPane root = new StackPane(); // for layering elements in a back-to-front stack
        Scene scene = new Scene(root); 

        // Set up keyboard input handling
        scene.setOnKeyPressed(this::handleKeyInput);
        scene.setOnKeyReleased(this::handleKeyInput);

        stage.setTitle(GameConfig.GAME_TITLE);
        stage.setScene(scene);
        stage.setResizable(false);
        // more to add -- drawing game canvas?
    }

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

    public void stop() {
        // stop the timer only if it is currently going
        if (timer != null) {
            timer.stop();
        }
        stage.hide();
    }

    public void handleKeyInput(KeyEvent event) {
        inputController.handleKeyPress(event);
    }

    public void render() {
        canvas.draw(game);
    }
}
