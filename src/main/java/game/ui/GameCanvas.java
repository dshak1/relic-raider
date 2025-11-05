package game.ui;

import game.core.Game;
import game.entity.Enemy;
import game.entity.Entity;
import game.map.Map;
import game.map.Position;
import game.reward.Reward;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * The {@code GameCanvas} class is responsible for rendering the game; it draws the map, 
 * entities, and HUD onto a JavaFX {@link Canvas}.
 */
public class GameCanvas {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Game game;

    /**
     * Constructs a new {@code GameCanvas} with the given JavaFX canvas
     *
     * @param canvas the JavaFX {@link Canvas} to use
     * @param game   the current {@link Game} instance
     */
    public GameCanvas(Game game, Canvas canvas) {
        this.game = game;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    /**
     * Renders a full frame of the game, including the map, entities, and HUD
     *
     * @param game the current {@link Game} instance 
     */
    public void draw(Game game) {
        clear();
        drawMap(game.getMap());
        drawEntities(game);
    }

    /**
     * Draws the map grid using tile sprites
     *
     * @param map the {@link Map} to render
     */
    private void drawMap(Map map) {
        // Define the viewport size (number of tiles visible)
        final int VIEWPORT_WIDTH = 15;  // Visible tiles horizontally
        final int VIEWPORT_HEIGHT = 11; // Visible tiles vertically
        
        // Calculate tile size based on viewport
        double tileSize = Math.min(canvas.getWidth() / VIEWPORT_WIDTH, canvas.getHeight() / VIEWPORT_HEIGHT);
        
        // Get player position for centering the viewport
        Position playerPos = game.getPlayer().getPosition();
        
        // Calculate viewport boundaries
        int startRow = Math.max(0, playerPos.getRow() - VIEWPORT_HEIGHT/2);
        int endRow = Math.min(map.getHeight(), startRow + VIEWPORT_HEIGHT);
        int startCol = Math.max(0, playerPos.getCol() - VIEWPORT_WIDTH/2);
        int endCol = Math.min(map.getWidth(), startCol + VIEWPORT_WIDTH);
        
        // Adjust offset to center the viewport
        double offsetX = (canvas.getWidth() - (tileSize * VIEWPORT_WIDTH)) / 2;
        double offsetY = (canvas.getHeight() - (tileSize * VIEWPORT_HEIGHT)) / 2;
        
        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                Position pos = new Position(row, col);
                double x = offsetX + (col - startCol) * tileSize;
                double y = offsetY + (row - startRow) * tileSize;

                String type;
                if (map.isBlocked(pos)) type = "wall";
                else if (map.isEntry(pos)) type = "entry";
                else if (map.isExit(pos)) type = "exit";
                else type = "floor";

                Image tile = SpriteManager.getTileSprite(type);
                gc.drawImage(tile, x, y, tileSize, tileSize);
            }
        }
    }

    /**
     * Returns the underlying JavaFX {@link Canvas} used for drawing.
     *
     * @return the canvas
     */
    public Canvas getCanvas() {
        return canvas;
    }


    /**
     * Draws all active entities in the game (rewards, enemies, player).
     *
     * @param game the current {@link Game} instance 
     */
    private void drawEntities(Game game) {
        // draw all of the rewards
        for (Reward reward : game.getRewards()) {
            // Only draw rewards that haven't been collected
            if (!reward.isCollected()) {
                drawEntity(reward);
            }
        }

        // draw all of the enemies
        for (Enemy enemy : game.getEnemies()) {
            drawEntity(enemy);
        }

        // draw the player
        drawEntity(game.getPlayer()); 
    }

    /**
     * Draws a single entity.
     *
     * @param entity the entity to draw
     */
    private void drawEntity(Entity entity) {
        Map map = game.getMap();
        final int VIEWPORT_WIDTH = 15;
        final int VIEWPORT_HEIGHT = 11;
        
        double tileSize = Math.min(canvas.getWidth() / VIEWPORT_WIDTH, canvas.getHeight() / VIEWPORT_HEIGHT);
        Position playerPos = game.getPlayer().getPosition();
        
        // Calculate viewport boundaries
        int startRow = Math.max(0, playerPos.getRow() - VIEWPORT_HEIGHT/2);
        int startCol = Math.max(0, playerPos.getCol() - VIEWPORT_WIDTH/2);
        
        // Calculate entity position relative to viewport
        double offsetX = (canvas.getWidth() - (tileSize * VIEWPORT_WIDTH)) / 2;
        double offsetY = (canvas.getHeight() - (tileSize * VIEWPORT_HEIGHT)) / 2;
        
        double x = offsetX + (entity.getPosition().getCol() - startCol) * tileSize;
        double y = offsetY + (entity.getPosition().getRow() - startRow) * tileSize;

        Image sprite = SpriteManager.getSprite(entity);
        gc.drawImage(sprite, x, y, tileSize, tileSize);
    }

    /**
     * Helper method to draw an image tile at a given coordinate
     */
    private void drawTile(Image image, double x, double y, int tileSize) {
        if (image != null) {
            gc.drawImage(image, x, y, tileSize, tileSize);
        } else {
            // prototype fallback behaviour
            gc.setFill(Color.WHITE);
            gc.fillRect(x, y, tileSize, tileSize);
        }
    }

    /**
     * Clears the canvas before redrawing a new frame - prototype behaviour
     */
    public void clear() {
        // initial code for clearing the canvas before a new frame (just set to black)
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
