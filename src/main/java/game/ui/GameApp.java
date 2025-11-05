package game.ui;

import game.core.Game;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX application class that sets up the game, main menu, and screens.
 */
public class GameApp extends Application {

     /**
     * Called when the JavaFX application starts.
     * Sets up the main menu and its button callbacks.
     * 
     * @param primaryStage the main stage for the application
     */
    @Override
    public void start(Stage primaryStage) {
        // Create the main menu
        MenuScreen menu = new MenuScreen();

        // Set up what happens when "Play" is clicked
        menu.setOnStartGame(() -> startGame(primaryStage));

        // Optionally set up "How to Play" and "Quit"
        menu.setOnHowToPlay(() -> ShowHowToPlay.show(primaryStage));
        menu.setOnExit(() -> primaryStage.close());

        // Show the menu in a scene
        Scene menuScene = new Scene(menu, 800, 600);
        primaryStage.setScene(menuScene);
        primaryStage.setTitle(GameConfig.GAME_TITLE);
        primaryStage.show();
    }

    /**
     * Starts a new game when "Play" is clicked from the menu.
     * 
     * @param primaryStage the stage where the game scene will be shown
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

        gameManager.setOnWin(() -> {
            WinScreen winScreen = new WinScreen();
            winScreen.showScore(game.getScore(), game.getElapsedTime().toSeconds());
            Stage popup = new Stage();
            popup.setScene(new Scene(winScreen, 400, 300));
            popup.show();
            winScreen.setOnBack(() -> {
                popup.close();
                showMainMenu(primaryStage); // a method to show MenuScreen again
            });
        });

        gameManager.setOnLose(() -> {
            LoseScreen loseScreen = new LoseScreen();
            loseScreen.showScore(game.getScore(), game.getElapsedTime().toSeconds());
            Stage popup = new Stage();
            popup.setScene(new Scene(loseScreen, 400, 300));
            popup.show();
            loseScreen.setOnBack(() -> {
                popup.close();
                showMainMenu(primaryStage);
            });
        });

        gameManager.startGame();
        window.start();
    }

    /**
     * Shows the main menu on the given stage.
     * 
     * @param stage the stage to display the menu
     */
    private void showMainMenu(Stage stage) {
        MenuScreen menu = new MenuScreen();

        menu.setOnStartGame(() -> startGame(stage));
        menu.setOnHowToPlay(() -> showHowToPlay(stage));
        menu.setOnExit(() -> stage.close());

        Scene menuScene = new Scene(menu, 800, 600);
        stage.setScene(menuScene);
        stage.show();
    }

    /**
     * Shows the HowToPlay screen and handles back navigation.
     * 
     * @param stage the stage to display the instructions
     */
    private void showHowToPlay(Stage stage) {
        HowToPlayScreen howToPlay = new HowToPlayScreen();

        howToPlay.setOnBack(() -> showMainMenu(stage)); // back button goes to menu

        Scene scene = new Scene(howToPlay, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    /** Application entry point. */
    public static void main(String[] args) {
        launch(args);
    }
}
