package game.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.FontWeight;

/**
 * The {@code MenuScreen} class provides the main menu interface that players see when launching the game.
 * <p>
 * This screen handles navigation to game start, instructions, or application exit. It extends
 * {@link javafx.scene.layout.VBox} to provide a vertical layout for menu components.
 * </p>
 * <p>
 * The menu includes three main options:
 * <ul>
 *   <li>Play - starts a new game</li>
 *   <li>How to Play - displays game instructions</li>
 *   <li>Quit - exits the application</li>
 * </ul>
 * </p>
 */
public class MenuScreen extends VBox {
    
    /** Button to start a new game */
    private Button playButton;
    
    /** Button to display game instructions */
    private Button howToPlayButton;
    
    /** Button to exit the application */
    private Button quitButton;
    
    /** Image displaying the game title */
    private javafx.scene.image.ImageView titleImage;
    
    /** Callback invoked when the player clicks "Play" */
    private Runnable onStartGameCallback;
    
    /** Callback invoked when the player clicks "How to Play" */
    private Runnable onHowToPlayCallback;
    
    /** Callback invoked when the player clicks "Quit" */
    private Runnable onExitCallback;
    
    /**
     * Constructs a new {@code MenuScreen}.
     * <p>
     * Initializes all UI components, applies styling, and sets up the layout.
     * Event handlers are automatically configured for all buttons.
     * </p>
     */
    public MenuScreen() {
        initializeComponents();
        styleComponents();
        layoutComponents();
    }
    
    /**
     * Makes the menu screen visible and brings it to the front.
     * <p>
     * This method should be called when transitioning back to the main menu
     * from other game screens.
     * </p>
     */
    public void show() {
        this.setVisible(true);
        this.toFront();
    }
    
    /**
     * Hides the menu screen from view.
     * <p>
     * This method should be called when transitioning away from the main menu
     * to other game screens (e.g., starting the game or showing instructions).
     * </p>
     */
    public void hide() {
        this.setVisible(false);
    }
    
    /**
     * Sets the callback to be invoked when the player clicks "Play".
     * 
     * @param callback the {@link Runnable} to execute when the Play button is clicked;
     *                 may be null to clear the callback
     */
    public void setOnStartGame(Runnable callback) {
        this.onStartGameCallback = callback;
    }
    
    /**
     * Sets the callback to be invoked when the player clicks "How to Play".
     * 
     * @param callback the {@link Runnable} to execute when the How to Play button is clicked;
     *                 may be null to clear the callback
     */
    public void setOnHowToPlay(Runnable callback) {
        this.onHowToPlayCallback = callback;
    }
    
    /**
     * Sets the callback to be invoked when the player clicks "Quit".
     * 
     * @param callback the {@link Runnable} to execute when the Quit button is clicked;
     *                 may be null to clear the callback
     */
    public void setOnExit(Runnable callback) {
        this.onExitCallback = callback;
    }
    
    /**
     * Initializes all UI components (buttons, title text).
     * <p>
     * Creates the title text using the game title from {@link GameConfig},
     * initializes all menu buttons, and sets up their event handlers to call
     * the corresponding callback methods.
     * </p>
     */
    private void initializeComponents() {
        // Initialize title image with error checking
        try {
            javafx.scene.image.Image titleImg = ResourceLoader.loadImage("title.png");
            if (titleImg != null) {
                System.out.println("Title image loaded successfully");
                System.out.println("Image width: " + titleImg.getWidth());
                System.out.println("Image height: " + titleImg.getHeight());
            } else {
                System.out.println("ERROR: Title image is null");
            }
            titleImage = new javafx.scene.image.ImageView(titleImg);
            titleImage.setPreserveRatio(true);
            titleImage.setFitWidth(350);
            
            // Add visible border to debug
            titleImage.setStyle("-fx-border-color: red; -fx-border-width: 3;");
            
        } catch (Exception e) {
            System.out.println("ERROR loading title image: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Initialize menu buttons
        playButton = new Button("Play");
        howToPlayButton = new Button("How to Play");
        quitButton = new Button("Quit");
        
        // Set up event handlers for button clicks
        playButton.setOnAction(event -> onStartGame());
        howToPlayButton.setOnAction(event -> onHowToPlay());
        quitButton.setOnAction(event -> onExit());
    }
    
    /**
     * Applies styling to the menu screen components.
     * <p>
     * Sets fonts for the title and buttons, and ensures consistent sizing
     * for all buttons to create a polished, uniform appearance.
     * </p>
     */
    private void styleComponents() {
        
        // Style buttons with custom appearance
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
        
        playButton.setStyle(buttonStyle);
        howToPlayButton.setStyle(buttonStyle);
        quitButton.setStyle(buttonStyle);
        
        // Add hover effects
        playButton.setOnMouseEntered(e -> playButton.setStyle(buttonHoverStyle));
        playButton.setOnMouseExited(e -> playButton.setStyle(buttonStyle));
        
        howToPlayButton.setOnMouseEntered(e -> howToPlayButton.setStyle(buttonHoverStyle));
        howToPlayButton.setOnMouseExited(e -> howToPlayButton.setStyle(buttonStyle));
        
        quitButton.setOnMouseEntered(e -> quitButton.setStyle(buttonHoverStyle));
        quitButton.setOnMouseExited(e -> quitButton.setStyle(buttonStyle));
        
        // Set consistent button dimensions
        double buttonWidth = 250;
        double buttonHeight = 60;
        
        playButton.setPrefWidth(buttonWidth);
        howToPlayButton.setPrefWidth(buttonWidth);
        quitButton.setPrefWidth(buttonWidth);
        
        playButton.setPrefHeight(buttonHeight);
        howToPlayButton.setPrefHeight(buttonHeight);
        quitButton.setPrefHeight(buttonHeight);
    }
    
    /**
     * Sets up the layout and spacing of menu components.
     * <p>
     * Configures the VBox container to center-align all components and
     * adds appropriate spacing between elements. Components are added
     * in the following order: title, Play button, How to Play button, Quit button.
     * </p>
     */
    private void layoutComponents() {
        // Configure main VBox layout properties
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(20));
        
        // Set fixed size to match scene dimensions
        this.setPrefSize(800, 600);
        this.setMinSize(800, 600);
        this.setMaxSize(800, 600);
        
        // Add background image with proper sizing
        this.setStyle(
            "-fx-background-color: black;" +
            "-fx-background-image: url('/assets/sprites/menu_background.png');" +
            "-fx-background-size: 100% auto;" +
            "-fx-background-position: center;" +
            "-fx-background-repeat: no-repeat;"
        );
        
        // Create a separate VBox for the buttons with tight spacing
        VBox buttonGroup = new VBox(15);  // 15 pixels between buttons
        buttonGroup.setAlignment(Pos.CENTER);
        buttonGroup.getChildren().addAll(
            playButton,
            howToPlayButton,
            quitButton
        );
        
        // Add title and button group to main VBox with custom spacing
        this.setSpacing(30);  // 40 pixels between title and button group
        this.getChildren().addAll(
            titleImage,
            buttonGroup
        );
        
        // Debug output
        System.out.println("Children added to VBox");
        System.out.println("Number of children: " + this.getChildren().size());
        System.out.println("Title image visible: " + titleImage.isVisible());
        System.out.println("Title image managed: " + titleImage.isManaged());
        System.out.println("Title image fit width: " + titleImage.getFitWidth());
    }
    
    /**
     * Triggered when the Play button is clicked.
     * <p>
     * Invokes the registered start game callback if one has been set via
     * {@link #setOnStartGame(Runnable)}.
     * </p>
     */
    private void onStartGame() {
        if (onStartGameCallback != null) {
            onStartGameCallback.run();
        }
    }
    
    /**
     * Triggered when the How to Play button is clicked.
     * <p>
     * Invokes the registered how-to-play callback if one has been set via
     * {@link #setOnHowToPlay(Runnable)}.
     * </p>
     */
    private void onHowToPlay() {
        if (onHowToPlayCallback != null) {
            onHowToPlayCallback.run();
        }
    }
    
    /**
     * Triggered when the Quit button is clicked.
     * <p>
     * Invokes the registered exit callback if one has been set via
     * {@link #setOnExit(Runnable)}.
     * </p>
     */
    private void onExit() {
        if (onExitCallback != null) {
            onExitCallback.run();
        }
    }
}