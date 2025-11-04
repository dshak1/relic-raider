package game.ui;

import game.core.Game;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main entry point for the Relic Raider game.
 * This class initializes the game state, loads sprites, and sets up
 * the UI components including HUD, GameManager, and GameWindow.
 */
public class GameApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Initialize all sprites
        SpriteManager.initialize();

        // 2. Build the game state (map, player, enemies, rewards)
        Game game = Game.createSimpleGame(GameConfig.BOARD_WIDTH_TILES, GameConfig.BOARD_HEIGHT_TILES);

        // 3. Create the HUD to display score, time, and messages
        HUD hud = new HUD(game);

        // 4. Create the GameManager to handle game logic (ticks, collisions, HUD updates)
        GameManager gameManager = new GameManager(game, hud);

        // 5. Create the GameWindow to handle rendering and input
        GameWindow window = new GameWindow(primaryStage, game);

        // 6. Start the game logic and rendering
        gameManager.startGame(); // starts the internal logic loop
        window.start();          // starts rendering and input handling
    }

    public static void main(String[] args) {
        launch(args); // JavaFX entry point
    }
}
