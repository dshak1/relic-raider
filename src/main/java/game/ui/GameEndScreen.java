package game.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Utility class for showing end-of-game screens (Win or Lose).
 * <p>
 * Provides static methods to create pop-up windows for winning or losing a game,
 * displaying score and elapsed time, and handling the back-to-menu callback.
 * </p>
 */
public class GameEndScreen {
    
    /**
     * Shows a "You Win" pop-up window with score and elapsed time.
     * 
     * @param score the player's final score
     * @param elapsedTimeSeconds the total elapsed game time in seconds
     * @param backToMenu a {@link Runnable} invoked when the player wants to return to the main menu
     */
    public static void showWin(long score, long elapsedTimeSeconds, Runnable backToMenu) {
        Stage winStage = new Stage();
        WinScreen winScreen = new WinScreen();
        winScreen.showScore((int) score, elapsedTimeSeconds);

        winScreen.setOnBack(() -> {
            winStage.close();
            backToMenu.run();
        });

        Scene scene = new Scene(winScreen, 500, 400);
        winStage.setTitle("You Win!");
        winStage.setScene(scene);
        winStage.show();
    }

    /**
     * Shows a "You Lose" pop-up window with score and elapsed time.
     * 
     * @param score the player's final score
     * @param elapsedTimeSeconds the total elapsed game time in seconds
     * @param backToMenu a {@link Runnable} invoked when the player wants to return to the main menu
     */
    public static void showLose(long score, long elapsedTimeSeconds, Runnable backToMenu) {
        Stage loseStage = new Stage();
        LoseScreen loseScreen = new LoseScreen();
        loseScreen.showScore((int) score, elapsedTimeSeconds);

        loseScreen.setOnBack(() -> {
            loseStage.close();
            backToMenu.run();
        });

        Scene scene = new Scene(loseScreen, 500, 400);
        loseStage.setTitle("You Lose!");
        loseStage.setScene(scene);
        loseStage.show();
    }
}
