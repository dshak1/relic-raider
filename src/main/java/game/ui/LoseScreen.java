package game.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class LoseScreen extends VBox {
    private Text messageText;
    private Button backButton;
    private Runnable onBackToMenu;


    public LoseScreen() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);

        messageText = new Text();
        messageText.setFont(Font.font(24));

        backButton = new Button("Back to Menu");

        this.getChildren().addAll(messageText, backButton);
    }

    public void showScore(int score, long timeSeconds) {
        messageText.setText("You Lose!\nScore: " + score + "\nTime: " + timeSeconds + "s");
    }

    public void setOnBack(Runnable callback) {
        backButton.setOnAction(e -> callback.run());
    }


    public void show() {
        this.setVisible(true);
        this.toFront();
    }

    public void hide() {
        this.setVisible(false);
    }
}
