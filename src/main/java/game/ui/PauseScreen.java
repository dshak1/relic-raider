package game.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.time.Duration;

/**
 * The {@code PauseScreen} class displays the pause menu overlay.
 * <p>
 * This screen appears when the player pauses the game, showing controls, current score,
 * and elapsed time. Provides options to resume, restart, or return to the main menu.
 * Uses styling matching the intro menu screen.
 * </p>
 */
public class PauseScreen extends VBox {
    
    /** Text displaying the pause title */
    private Text titleText;
    
    /** Text displaying game controls */
    private Text controlsText;
    
    /** Text displaying current score */
    private Text scoreText;
    
    /** Text displaying elapsed time */
    private Text timeText;
    
    /** Button to resume the game */
    private Button resumeButton;
    
    /** Button to restart the game */
    private Button restartButton;
    
    /** Button to return to main menu */
    private Button menuButton;
    
    /** Callback invoked when the player clicks "Resume" */
    private Runnable onResumeCallback;
    
    /** Callback invoked when the player clicks "Restart" */
    private Runnable onRestartCallback;
    
    /** Callback invoked when the player clicks "Main Menu" */
    private Runnable onMenuCallback;
    
    /**
     * Constructs a new {@code PauseScreen}.
     * <p>
     * Initializes all UI components, applies styling, and sets up the layout.
     * </p>
     */
    public PauseScreen() {
        initializeComponents();
        styleComponents();
        layoutComponents();
    }
    
    /**
     * Makes the pause screen visible and brings it to the front.
     */
    public void show() {
        this.setVisible(true);
        this.toFront();
    }
    
    /**
     * Hides the pause screen from view.
     */
    public void hide() {
        this.setVisible(false);
    }
    
    /**
     * Updates the displayed score and time.
     * 
     * @param score the current game score
     * @param elapsedTime the elapsed game time
     */
    public void updateStats(int score, Duration elapsedTime) {
        scoreText.setText("Score: " + score);
        
        long totalSeconds = elapsedTime.getSeconds();
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        timeText.setText(String.format("Time: %02d:%02d", minutes, seconds));
    }
    
    /**
     * Sets the callback to be invoked when the player clicks "Resume".
     * 
     * @param callback the {@link Runnable} to execute when Resume is clicked
     */
    public void setOnResume(Runnable callback) {
        this.onResumeCallback = callback;
    }
    
    /**
     * Sets the callback to be invoked when the player clicks "Restart".
     * 
     * @param callback the {@link Runnable} to execute when Restart is clicked
     */
    public void setOnRestart(Runnable callback) {
        this.onRestartCallback = callback;
    }
    
    /**
     * Sets the callback to be invoked when the player clicks "Main Menu".
     * 
     * @param callback the {@link Runnable} to execute when Main Menu is clicked
     */
    public void setOnMenu(Runnable callback) {
        this.onMenuCallback = callback;
    }
    
    /**
     * Initializes all UI components.
     */
    private void initializeComponents() {
        titleText = new Text("PAUSED");
        titleText.setTextAlignment(TextAlignment.CENTER);
        
        controlsText = new Text("Controls:\nWASD or Arrow Keys - Move\nP or ESC - Pause/Resume");
        controlsText.setTextAlignment(TextAlignment.CENTER);
        
        scoreText = new Text("Score: 0");
        scoreText.setTextAlignment(TextAlignment.CENTER);
        
        timeText = new Text("Time: 00:00");
        timeText.setTextAlignment(TextAlignment.CENTER);
        
        resumeButton = new Button("Resume");
        resumeButton.setOnAction(event -> onResume());
        
        restartButton = new Button("Restart");
        restartButton.setOnAction(event -> onRestart());
        
        menuButton = new Button("Main Menu");
        menuButton.setOnAction(event -> onMenu());
    }
    
    /**
     * Applies styling to the pause screen components to match the intro menu.
     */
    private void styleComponents() {
        // Background - dark semi-transparent overlay matching menu aesthetic
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
        
        // Title - large, bold, tan color matching menu
        titleText.setFont(Font.font("System", FontWeight.BOLD, 48));
        titleText.setStyle(
            "-fx-fill: #E5C9A7; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(106, 80, 54, 0.8), 8, 0, 0, 2);"
        );
        
        // Info text (controls, score, time) - tan color
        Font infoFont = Font.font("System", FontWeight.NORMAL, 16);
        controlsText.setFont(infoFont);
        scoreText.setFont(infoFont);
        timeText.setFont(infoFont);
        
        controlsText.setStyle("-fx-fill: #D4B896;");
        scoreText.setStyle("-fx-fill: #D4B896;");
        timeText.setStyle("-fx-fill: #D4B896;");
        
        // Buttons - matching menu button style
        Font buttonFont = Font.font("System", FontWeight.BOLD, 20);
        resumeButton.setFont(buttonFont);
        restartButton.setFont(buttonFont);
        menuButton.setFont(buttonFont);
        
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
        
        resumeButton.setStyle(buttonStyle);
        restartButton.setStyle(buttonStyle);
        menuButton.setStyle(buttonStyle);
        
        resumeButton.setPrefWidth(250);
        resumeButton.setPrefHeight(60);
        restartButton.setPrefWidth(250);
        restartButton.setPrefHeight(60);
        menuButton.setPrefWidth(250);
        menuButton.setPrefHeight(60);
        
        // Hover effects
        resumeButton.setOnMouseEntered(e -> resumeButton.setStyle(buttonHoverStyle));
        resumeButton.setOnMouseExited(e -> resumeButton.setStyle(buttonStyle));
        
        restartButton.setOnMouseEntered(e -> restartButton.setStyle(buttonHoverStyle));
        restartButton.setOnMouseExited(e -> restartButton.setStyle(buttonStyle));
        
        menuButton.setOnMouseEntered(e -> menuButton.setStyle(buttonHoverStyle));
        menuButton.setOnMouseExited(e -> menuButton.setStyle(buttonStyle));
    }
    
    /**
     * Sets up the layout and spacing of components.
     */
    private void layoutComponents() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(15);
        this.setPadding(new Insets(40));
        this.setPrefWidth(600);
        this.setPrefHeight(500);
        
        this.getChildren().addAll(
            titleText,
            controlsText,
            scoreText,
            timeText,
            resumeButton,
            restartButton,
            menuButton
        );
    }
    
    /**
     * Triggered when the Resume button is clicked.
     */
    private void onResume() {
        if (onResumeCallback != null) {
            onResumeCallback.run();
        }
    }
    
    /**
     * Triggered when the Restart button is clicked.
     */
    private void onRestart() {
        if (onRestartCallback != null) {
            onRestartCallback.run();
        }
    }
    
    /**
     * Triggered when the Main Menu button is clicked.
     */
    private void onMenu() {
        if (onMenuCallback != null) {
            onMenuCallback.run();
        }
    }
}
