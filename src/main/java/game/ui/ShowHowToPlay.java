package game.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utility class to show the "How to Play" screen in a pop-up window.
 * <p>
 * Opens a new Stage with the {@link HowToPlayScreen} and handles the back
 * button callback to close the window.
 * </p>
 */
public class ShowHowToPlay {

    /**
     * Shows the "How to Play" screen as a modal window.
     * 
     * @param owner the parent {@link Stage} that owns this pop-up; can be used to block input to it
     */
    public static void show(Stage owner) {
        Stage stage = new Stage();
        HowToPlayScreen screen = new HowToPlayScreen();
        
        // Close the pop-up when Back is clicked
        screen.setOnBack(() -> stage.close());

        Scene scene = new Scene(screen, 800, 600);
        stage.setScene(scene);
        stage.setTitle("How to Play " + GameConfig.GAME_TITLE);
        stage.initOwner(owner); // optional, keeps it modal
        stage.show();
    }
}
