package game.ui;

import game.core.Game;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the main menu
        MenuScreen menu = new MenuScreen();

        // Set up what happens when "Play" is clicked
        menu.setOnStartGame(() -> startGame(primaryStage));

        // Optionally set up "How to Play" and "Quit"
        menu.setOnHowToPlay(() -> System.out.println("Show instructions here"));
        menu.setOnExit(() -> primaryStage.close());

        // Show the menu in a scene
        Scene menuScene = new Scene(menu, 800, 600);
        primaryStage.setScene(menuScene);
        primaryStage.setTitle(GameConfig.GAME_TITLE);
        primaryStage.show();
    }

    /**
     * Called when the user clicks "Play" — starts the actual game.
     */
    private void startGame(Stage primaryStage) {
        SpriteManager.initialize();

        Game game = Game.createSimpleGame(GameConfig.BOARD_WIDTH_TILES, GameConfig.BOARD_HEIGHT_TILES);
        HUD hud = new HUD(game);
        game.setHUD(hud);
        GameManager gameManager = new GameManager(game, hud);
        GameWindow window = new GameWindow(primaryStage, game);

        // Switch to the game scene
        primaryStage.setScene(window.getScene());
        primaryStage.show();

        gameManager.startGame();
        window.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
