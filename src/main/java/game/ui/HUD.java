package game.ui;

import java.time.Duration;

import game.core.Game;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class HUD extends HBox{
    private Text scoreText;
    private Text timeText;
    private Text messageText;

    private Game game;

    public HUD (Game game){
        this.game = game;

        //initialize text elements
        scoreText = new Text("Score: 0");
        timeText = new Text("Time: 0s");
        messageText = new Text("");

        scoreText.setFont(Font.font(20));
        timeText.setFont(Font.font(20));
        messageText.setFont(Font.font(24));
        
        this.getChildren().addAll(scoreText, timeText, messageText);
        this.setSpacing(20);
    }

    public void update(){
        scoreText.setText("Score: " + game.getScore());

        Duration elapsed = game.getElapsedTime();
        long seconds = elapsed.toSeconds();
        timeText.setText("Time: " + seconds + "s");
    }

    public void showMessage(String message){
        messageText.setText(message);
    }

    public void clearMessage(){
        messageText.setText("");
    }
}
