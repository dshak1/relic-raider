package game.ui;

import java.time.Duration;

import game.core.Game;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Heads-Up Display for the game.
 * <p>
 * This class displays the player's score, elapsed time, and optional messages
 * such as "Game Over", "Score Increase", or any kind of alert above the game
 * canvas.
 * It is implemented as an HBox containing text nods for each display element.
 * The HUD is updated by the GameManger after each game tick.
 * </p>
 */

public class HUD extends HBox{
    /** Text element for displaying the player's current score. */
    private Text scoreText;
    /** Text element for displaying the elapsed game time. */
    private Text timeText;
    /** Text element for displaying messages. */
    private Text messageText;

    /** Reference to the core Game object to read score and time. */
    private Game game;


    /**
     * Constructs a new HUD for the given game instance.
     * Initializes score, time, and message display elements.
     * 
     * @param game the game instance to display information from
     */
    public HUD (Game game){
        this.game = game;

        //initialize text elements
        scoreText = new Text("Score: 0");
        timeText = new Text("Time: 0s");
        messageText = new Text("");

        scoreText.setFont(Font.font(20));
        timeText.setFont(Font.font(20));
        messageText.setFont(Font.font(24));
        
        //add text elements to the HBox layout.
        this.getChildren().addAll(scoreText, timeText, messageText);
        this.setSpacing(20);
    }

    /**
     * Updates the HUD display
     * <p>
     * This method refreshes the score and elapsed time based on the
     * current state of the Game object. Should be called after each
     * game tick to keep the display current.
     * </p>
     */
    public void update(){
        scoreText.setText("Score: " + game.getScore());

        Duration elapsed = game.getElapsedTime();
        long seconds = elapsed.toSeconds();
        timeText.setText("Time: " + seconds + "s");
    }


    /**
     * Displays a message on the HUD.
     * <p>
     * Can be used for notifications and messages to the player.
     * </p>
     * 
     * @param message
     */
    public void showMessage(String message){
        messageText.setText(message);
    }


    /**
     * Clears any message currently displayed on screen.
     */
    public void clearMessage(){
        messageText.setText("");
    }
}
