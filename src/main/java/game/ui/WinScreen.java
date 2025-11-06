package game.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Represents a pop-up UI screen shown when the player wins the game.
 * <p>
 * Displays the final score, elapsed time, and a Back button to return to the menu.
 * Styled to match the intro menu screen.
 * </p>
 */
public class WinScreen extends VBox {

    /** Text displaying the "You Win" message along with score and time */
    private Text messageText;

    /** Button to go back to the main menu */
    private Button backButton;

    /**
     * Constructs a WinScreen and initializes its UI components.
     */
    public WinScreen() {
        initializeComponents();
        styleComponents();
        layoutComponents();
    }

    /**
     * Initializes all UI components.
     */
    private void initializeComponents() {
        messageText = new Text("You Win!");
        messageText.setTextAlignment(TextAlignment.CENTER);

        backButton = new Button("Back to Menu");
    }

    /**
     * Applies styling to match the intro menu.
     */
    private void styleComponents() {
        // Background - dark semi-transparent overlay matching menu aesthetic
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");

        // Message text - large, bold, tan color matching menu
        messageText.setFont(Font.font("System", FontWeight.BOLD, 36));
        messageText.setStyle(
            "-fx-fill: #E5C9A7; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(106, 80, 54, 0.8), 8, 0, 0, 2);"
        );

        // Button - matching menu button style
        Font buttonFont = Font.font("System", FontWeight.BOLD, 20);
        backButton.setFont(buttonFont);

        String buttonStyle = 
            "-fx-background-color: #D4B896; " +
            "-fx-border-color: #6A5036; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-text-fill: #2C2C2C; " +
            "-fx-font-size: 20; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 15 40 15 40;";

        String buttonHoverStyle = 
            "-fx-background-color: #E5C9A7; " +
            "-fx-border-color: #6A5036; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-text-fill: #2C2C2C; " +
            "-fx-font-size: 20; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 15 40 15 40;";

        backButton.setStyle(buttonStyle);
        backButton.setPrefWidth(250);
        backButton.setPrefHeight(60);

        // Hover effects
        backButton.setOnMouseEntered(e -> backButton.setStyle(buttonHoverStyle));
        backButton.setOnMouseExited(e -> backButton.setStyle(buttonStyle));
    }

    /**
     * Sets up the layout and spacing of components.
     */
    private void layoutComponents() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(25);
        this.setPadding(new Insets(40));
        this.setPrefWidth(500);
        this.setPrefHeight(400);

        this.getChildren().addAll(messageText, backButton);
    }

    /**
     * Updates the screen with the player's score and elapsed time.
     * 
     * @param score the player's final score
     * @param timeSeconds the elapsed game time in seconds
     */
    public void showScore(int score, long timeSeconds) {
        long minutes = timeSeconds / 60;
        long seconds = timeSeconds % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        
        messageText.setText("You Win!\n\nScore: " + score + "\nTime: " + timeString);
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
