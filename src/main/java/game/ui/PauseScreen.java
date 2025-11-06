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
 * The {@code PauseScreen} class displays the pause menu overlay.
 * <p>
 * This screen appears when the player pauses the game, providing options to resume,
 * restart, or return to the main menu. Uses a semi-transparent overlay style.
 * </p>
 */
public class PauseScreen extends VBox {
    
    /** Text displaying the pause title */
    private Text titleText;
    
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
        
        resumeButton = new Button("Resume");
        resumeButton.setOnAction(event -> onResume());
        
        restartButton = new Button("Restart");
        restartButton.setOnAction(event -> onRestart());
        
        menuButton = new Button("Main Menu");
        menuButton.setOnAction(event -> onMenu());
    }
    
    /**
     * Applies styling to the pause screen components.
     */
    private void styleComponents() {
        // Background - semi-transparent dark overlay
        this.setStyle("-fx-background-color: rgba(26, 26, 46, 0.95); -fx-text-fill: #FFFFFF;");
        
        // Title - large, bold, white with shadow
        titleText.setFont(Font.font("System", FontWeight.BOLD, 42));
        titleText.setStyle("-fx-fill: #FFFFFF; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
        
        // Buttons - styled with blue accents
        Font buttonFont = Font.font("System", FontWeight.BOLD, 18);
        resumeButton.setFont(buttonFont);
        restartButton.setFont(buttonFont);
        menuButton.setFont(buttonFont);
        
        resumeButton.setPrefWidth(200);
        resumeButton.setPrefHeight(45);
        restartButton.setPrefWidth(200);
        restartButton.setPrefHeight(45);
        menuButton.setPrefWidth(200);
        menuButton.setPrefHeight(45);
        
        resumeButton.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: #FFFFFF; -fx-background-radius: 5;");
        restartButton.setStyle("-fx-background-color: #2a2a3e; -fx-text-fill: #FFFFFF; -fx-background-radius: 5; -fx-border-color: #4A90E2; -fx-border-width: 2; -fx-border-radius: 5;");
        menuButton.setStyle("-fx-background-color: #2a2a3e; -fx-text-fill: #FFFFFF; -fx-background-radius: 5; -fx-border-color: #4A90E2; -fx-border-width: 2; -fx-border-radius: 5;");
        
        // Hover effects
        resumeButton.setOnMouseEntered(e -> resumeButton.setStyle("-fx-background-color: #6BA3E8; -fx-text-fill: #FFFFFF; -fx-background-radius: 5;"));
        resumeButton.setOnMouseExited(e -> resumeButton.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: #FFFFFF; -fx-background-radius: 5;"));
        
        restartButton.setOnMouseEntered(e -> restartButton.setStyle("-fx-background-color: #3a3a4e; -fx-text-fill: #FFFFFF; -fx-background-radius: 5; -fx-border-color: #4A90E2; -fx-border-width: 2; -fx-border-radius: 5;"));
        restartButton.setOnMouseExited(e -> restartButton.setStyle("-fx-background-color: #2a2a3e; -fx-text-fill: #FFFFFF; -fx-background-radius: 5; -fx-border-color: #4A90E2; -fx-border-width: 2; -fx-border-radius: 5;"));
        
        menuButton.setOnMouseEntered(e -> menuButton.setStyle("-fx-background-color: #3a3a4e; -fx-text-fill: #FFFFFF; -fx-background-radius: 5; -fx-border-color: #4A90E2; -fx-border-width: 2; -fx-border-radius: 5;"));
        menuButton.setOnMouseExited(e -> menuButton.setStyle("-fx-background-color: #2a2a3e; -fx-text-fill: #FFFFFF; -fx-background-radius: 5; -fx-border-color: #4A90E2; -fx-border-width: 2; -fx-border-radius: 5;"));
    }
    
    /**
     * Sets up the layout and spacing of components.
     */
    private void layoutComponents() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        this.setPadding(new Insets(40));
        this.setPrefWidth(600);
        this.setPrefHeight(400);
        
        this.getChildren().addAll(
            titleText,
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

