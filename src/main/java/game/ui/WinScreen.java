package game.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Represents a pop-up UI screen shown when the player wins the game.
 * <p>
 * Displays the final score, elapsed time, and a Back button to return to the menu.
 * </p>
 */
public class WinScreen extends VBox {

    /** Text displaying the "You Win" message along with score and time */
    private Text messageText;

    /** Button to go back to the main menu */
    private Button backButton;

    /** Callback invoked when Back-to-Menu action is requested */
    private Runnable onBackToMenu;

    /**
     * Constructs a WinScreen and initializes its UI components.
     */
    public WinScreen() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);

        messageText = new Text();
        messageText.setFont(Font.font(24));

        backButton = new Button("Back to Menu");

        this.getChildren().addAll(messageText, backButton);
    }

    /**
     * Updates the screen with the player's score and elapsed time.
     * 
     * @param score the player's final score
     * @param timeSeconds the elapsed game time in seconds
     */
    public void showScore(int score, long timeSeconds) {
        messageText.setText("You Win!\nScore: " + score + "\nTime: " + timeSeconds + "s");
    }

    /**
     * Sets the final score displayed on the screen.
     * Uses a default time of 0 seconds.
     * 
     * @param score the player's final score
     */
    public void setFinalScore(int score) {
        showScore(score, 0);
    }

    /**
     * Sets the callback to be invoked when the Back button is clicked.
     * 
     * @param callback a {@link Runnable} to execute when Back is pressed
     */
    public void setOnBack(Runnable callback) {
        backButton.setOnAction(e -> callback.run());
    }

    /**
     * Makes the screen visible and brings it to the front.
     */
    public void show() {
        this.setVisible(true);
        this.toFront();
    }

    /**
     * Hides the screen.
     */
    public void hide() {
        this.setVisible(false);
    }
}
