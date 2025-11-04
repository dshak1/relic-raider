package game.ui;

import game.ui.GameConfig;
import game.ui.ResourceLoader;
import game.ui.SpriteManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TestCanvasApp extends Application {

    @Override
    public void start(Stage stage) {
        // Initialize sprites
        SpriteManager.initialize();

        // Create canvas
        int canvasWidth = 400;
        int canvasHeight = 400;
        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw a 10x10 grid of floor tiles
        Image floor = SpriteManager.getTileSprite("floor");
        int rows = 10;
        int cols = 10;
        double tileWidth = canvasWidth / (double) cols;
        double tileHeight = canvasHeight / (double) rows;
        double tileSize = Math.min(tileWidth, tileHeight);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                gc.drawImage(floor, c * tileSize, r * tileSize, tileSize, tileSize);
            }
        }

        // Setup scene
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, canvasWidth, canvasHeight);
        stage.setTitle("Floor Tile Test");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
