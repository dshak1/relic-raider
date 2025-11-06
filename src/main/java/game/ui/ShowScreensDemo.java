package game.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Demo application to show the Win and Lose screens.
 * Useful for testing and demonstrating the UI screens.
 */
public class ShowScreensDemo extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Create the screens
        WinScreen winScreen = new WinScreen();
        LoseScreen loseScreen = new LoseScreen();
        PauseScreen pauseScreen = new PauseScreen();
        
        // Create demo controls
        Button showWinButton = new Button("Show Win Screen");
        Button showLoseButton = new Button("Show Lose Screen");
        Button showPauseButton = new Button("Show Pause Screen");
        Button backButton = new Button("Back to Menu");
        
        // Style buttons
        showWinButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #1a1a2e; -fx-background-radius: 5; -fx-font-size: 16; -fx-padding: 10 20;");
        showLoseButton.setStyle("-fx-background-color: #FF4444; -fx-text-fill: #FFFFFF; -fx-background-radius: 5; -fx-font-size: 16; -fx-padding: 10 20;");
        showPauseButton.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: #FFFFFF; -fx-background-radius: 5; -fx-font-size: 16; -fx-padding: 10 20;");
        backButton.setStyle("-fx-background-color: #2a2a3e; -fx-text-fill: #FFFFFF; -fx-background-radius: 5; -fx-font-size: 16; -fx-padding: 10 20; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 5;");
        
        // Set up button actions
        showWinButton.setOnAction(e -> {
            winScreen.setFinalScore(1234);
            winScreen.show();
            loseScreen.hide();
            pauseScreen.hide();
        });
        
        showLoseButton.setOnAction(e -> {
            loseScreen.setFinalScore(567);
            loseScreen.show();
            winScreen.hide();
            pauseScreen.hide();
        });
        
        showPauseButton.setOnAction(e -> {
            pauseScreen.show();
            winScreen.hide();
            loseScreen.hide();
        });
        
        backButton.setOnAction(e -> {
            winScreen.hide();
            loseScreen.hide();
            pauseScreen.hide();
        });
        
        // Create control panel
        HBox buttonPanel = new HBox(15);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(20));
        buttonPanel.getChildren().addAll(showWinButton, showLoseButton, showPauseButton, backButton);
        
        // Create root layout
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1a1a2e;");
        root.getChildren().add(buttonPanel);
        
        // Create StackPane to overlay screens
        javafx.scene.layout.StackPane stackPane = new javafx.scene.layout.StackPane();
        stackPane.getChildren().add(root);
        stackPane.getChildren().add(winScreen);
        stackPane.getChildren().add(loseScreen);
        stackPane.getChildren().add(pauseScreen);
        
        // Initially hide all screens
        winScreen.hide();
        loseScreen.hide();
        pauseScreen.hide();
        
        // Create scene
        Scene scene = new Scene(stackPane, 800, 600);
        primaryStage.setTitle("Win/Lose/Pause Screens Demo - Relic Raider");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

