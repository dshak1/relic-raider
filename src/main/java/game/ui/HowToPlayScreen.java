package game.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * The {@code HowToPlayScreen} class displays game instructions
 * <p>
 * This screen provides players with all the information they need to understand the game
 * mechanics, win/lose conditions, and gameplay rules. It extends {@link javafx.scene.layout.VBox}
 * to provide a vertical layout for instruction components.
 * </p>
 * <p>
 * The instructions include:
 * <ul>
 *   <li>Game controls and movement</li>
 *   <li>Win and lose conditions</li>
 *   <li>Enemy types and behaviors</li>
 *   <li>Reward types and values</li>
 * </ul>
 * </p>
 */
public class HowToPlayScreen extends VBox {
    
    /** Text displaying the title of the instructions screen */
    private Text titleText;
    
    /** Text displaying game control instructions */
    private Text controlsText;
    
    /** Text displaying game objectives and win/lose conditions */
    private Text objectivesText;
    
    /** Text displaying information about enemy types */
    private Text enemiesText;
    
    /** Text displaying information about reward types */
    private Text rewardsText;
    
    /** Button to return to the main menu */
    private Button backButton;
    
    /** ScrollPane containing all instruction content */
    private ScrollPane contentScrollPane;
    
    /** Callback invoked when the player clicks "Back" */
    private Runnable onBackCallback;
    
    /**
     * Constructs a new {@code HowToPlayScreen}.
     * <p>
     * Initializes all UI components displaying controls and rules,
     * applies styling, and sets up the layout.
     * </p>
     */
    public HowToPlayScreen() {
        initializeComponents();
        styleComponents();
        layoutComponents();
    }
    
    /**
     * Makes the instructions screen visible and brings it to the front.
     * <p>
     * This method should be called when transitioning to the instructions screen
     * from the main menu.
     * </p>
     */
    public void show() {
        this.setVisible(true);
        this.toFront();
    }
    
    /**
     * Hides the instructions screen from view.
     * <p>
     * This method should be called when transitioning away from the instructions
     * screen back to the main menu.
     * </p>
     */
    public void hide() {
        this.setVisible(false);
    }
    
    /**
     * Sets the callback to be invoked when the player clicks "Back".
     * 
     * @param callback the {@link Runnable} to execute when the Back button is clicked;
     *                 may be null to clear the callback
     */
    public void setOnBack(Runnable callback) {
        this.onBackCallback = callback;
    }
    
    /**
     * Initializes all UI components (title, instruction text sections, back button).
     * <p>
     * Creates the title text, all instruction sections using helper methods,
     * wraps them in a ScrollPane, and initializes the back button with its event handler.
     * </p>
     */
    private void initializeComponents() {
        // Initialize title text with game name from configuration
        titleText = new Text("How to Play " + GameConfig.GAME_TITLE);
        titleText.setTextAlignment(TextAlignment.CENTER);
        
        // Create instruction sections
        controlsText = createControlsSection();
        objectivesText = createObjectivesSection();
        enemiesText = createEnemiesSection();
        rewardsText = createRewardsSection();
        
        // Create container for scrollable content
        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.TOP_LEFT);
        contentBox.setPadding(new Insets(10));
        contentBox.getChildren().addAll(
            controlsText,
            objectivesText,
            enemiesText,
            rewardsText
        );
        
        // Wrap content in ScrollPane for long text
        contentScrollPane = new ScrollPane(contentBox);
        contentScrollPane.setFitToWidth(true);
        contentScrollPane.setPrefHeight(400);
        contentScrollPane.setStyle("-fx-background-color: transparent;");
        
        // Initialize back button
        backButton = new Button("Back to Menu");
        backButton.setOnAction(event -> onBack());
    }
    
    /**
     * Creates and returns formatted text content explaining game controls.
     * 
     * @return Text object containing controls information
     */
    private Text createControlsSection() {
        StringBuilder controls = new StringBuilder();
        controls.append("═══ CONTROLS ═══\n\n");
        controls.append("Use ARROW KEYS to move your character:\n");
        controls.append("  ↑ ").append(GameConfig.getKeyName(GameConfig.KEY_UP)).append(" - Move Up\n");
        controls.append("  ↓ ").append(GameConfig.getKeyName(GameConfig.KEY_DOWN)).append(" - Move Down\n");
        controls.append("  ← ").append(GameConfig.getKeyName(GameConfig.KEY_LEFT)).append(" - Move Left\n");
        controls.append("  → ").append(GameConfig.getKeyName(GameConfig.KEY_RIGHT)).append(" - Move Right\n\n");
        controls.append("Additional Controls:\n");
        controls.append("  ").append(GameConfig.getKeyName(GameConfig.KEY_PAUSE)).append(" - Pause Game\n");
        controls.append("  ").append(GameConfig.getKeyName(GameConfig.KEY_RESTART)).append(" - Restart Game\n");
        controls.append("  ").append(GameConfig.getKeyName(GameConfig.KEY_MENU)).append(" - Return to Menu\n");
        
        Text text = new Text(controls.toString());
        text.setFont(Font.font("Monospaced", 14));
        return text;
    }
    
    /**
     * Creates and returns formatted text content explaining win/lose conditions.
     * 
     * @return Text object containing objectives information
     */
    private Text createObjectivesSection() {
        StringBuilder objectives = new StringBuilder();
        objectives.append("\n═══ OBJECTIVES ═══\n\n");
        objectives.append("HOW TO WIN:\n");
        objectives.append("  1. Collect ALL regular rewards (coins) scattered throughout the temple\n");
        objectives.append("  2. Navigate to the EXIT after collecting all required rewards\n");
        objectives.append("  3. Avoid enemies and minimize damage from traps\n\n");
        objectives.append("HOW TO LOSE:\n");
        objectives.append("  • Collision with a moving enemy (instant defeat)\n");
        objectives.append("  • Your score drops below ").append(GameConfig.MIN_SCORE_THRESHOLD).append(" due to trap damage\n");
        
        Text text = new Text(objectives.toString());
        text.setFont(Font.font("Monospaced", 14));
        return text;
    }
    
    /**
     * Creates and returns formatted text content explaining enemy types.
     * 
     * @return Text object containing enemy information
     */
    private Text createEnemiesSection() {
        StringBuilder enemies = new StringBuilder();
        enemies.append("\n═══ ENEMIES ═══\n\n");
        enemies.append("MOVING ENEMIES (Skeletons):\n");
        enemies.append("  • Move one cell per game tick\n");
        enemies.append("  • Use intelligent pathfinding (A* algorithm) to chase you\n");
        enemies.append("  • Cannot pass through walls or barriers\n");
        enemies.append("  • Collision results in INSTANT DEFEAT\n");
        enemies.append("  • Stay alert and plan your route carefully!\n\n");
        enemies.append("STATIONARY HAZARDS (Spike Traps):\n");
        enemies.append("  • Fixed positions throughout the temple\n");
        enemies.append("  • Penalty: -").append(GameConfig.SPIKE_TRAP_PENALTY).append(" points when stepped on\n");
        enemies.append("  • Removed from the board after triggering\n");
        enemies.append("  • If your total score becomes negative, you lose!\n");
        
        Text text = new Text(enemies.toString());
        text.setFont(Font.font("Monospaced", 14));
        return text;
    }
    
    /**
     * Creates and returns formatted text content explaining reward types.
     * 
     * @return Text object containing rewards information
     */
    private Text createRewardsSection() {
        StringBuilder rewards = new StringBuilder();
        rewards.append("\n═══ REWARDS ═══\n\n");
        rewards.append("REGULAR REWARDS (Golden Coins):\n");
        rewards.append("  • Value: +").append(GameConfig.REGULAR_REWARD_VALUE).append(" points each\n");
        rewards.append("  • REQUIRED: Must collect ALL to unlock the exit\n");
        rewards.append("  • Permanent - remain until collected\n");
        rewards.append("  • Collected automatically when you move onto their cell\n\n");
        rewards.append("BONUS REWARDS (Golden Totems):\n");
        rewards.append("  • Value: +").append(GameConfig.BONUS_REWARD_VALUE).append(" points each\n");
        rewards.append("  • OPTIONAL: Not required to win, but boost your score\n");
        rewards.append("  • Appear randomly during gameplay\n");
        rewards.append("  • Disappear after ").append(GameConfig.BONUS_REWARD_DURATION_TICKS).append(" game ticks\n");
        rewards.append("  • Act quickly to claim them!\n");
        
        Text text = new Text(rewards.toString());
        text.setFont(Font.font("Monospaced", 14));
        return text;
    }
    
    /**
     * Applies styling to the instruction screen components.
     * <p>
     * Sets fonts for the title and back button, and ensures consistent sizing
     * for a polished appearance.
     * </p>
     */
    private void styleComponents() {
        // Title
        titleText.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleText.setStyle("-fx-fill: #ffcc33;"); // bright golden title
        
        // Section text (body)
        String bodyColor = "#f1d36b"; // soft gold for readability
        controlsText.setStyle("-fx-fill: " + bodyColor + ";");
        objectivesText.setStyle("-fx-fill: " + bodyColor + ";");
        enemiesText.setStyle("-fx-fill: " + bodyColor + ";");
        rewardsText.setStyle("-fx-fill: " + bodyColor + ";");
        
        // Back button styling
        backButton.setFont(Font.font(16));
        backButton.setPrefWidth(200);
        backButton.setPrefHeight(40);
        backButton.setStyle(
            "-fx-background-color: #c9a66b;" + // muted gold button
            "-fx-text-fill: #37373aff;" +       // dark text for contrast
            "-fx-background-radius: 10;"
        );
        
        // Overall screen background
        this.setStyle("-fx-background-color: #532c11ff;"); // deep dark background
        
        // ScrollPane background
        contentScrollPane.setStyle(
            "-fx-background: #37373aff;" + 
            "-fx-background-color: #37373aff;"
        );
    }
    
    /**
     * Sets up the layout and spacing of instruction components.
     * <p>
     * Configures the VBox container to center-align components and
     * adds appropriate spacing and padding. Components are added
     * in the following order: title, scrollable content, back button.
     * </p>
     */
    private void layoutComponents() {
        // Configure VBox layout properties
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.setPrefWidth(700);
        this.setPrefHeight(600);
        
        // Add all components to the VBox in display order
        this.getChildren().addAll(
            titleText,
            contentScrollPane,
            backButton
        );
    }
    
    /**
     * Triggered when the Back button is clicked.
     * <p>
     * Invokes the registered back callback if one has been set via
     * {@link #setOnBack(Runnable)}.
     * </p>
     */
    private void onBack() {
        if (onBackCallback != null) {
            onBackCallback.run();
        }
    }
}