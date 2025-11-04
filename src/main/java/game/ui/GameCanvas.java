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
        // Calculate tile size to fit the canvas, keeping tiles square
        double tileSize = Math.min(canvas.getWidth() / map.getWidth(), canvas.getHeight() / map.getHeight());
        double offsetX = (canvas.getWidth() - (tileSize * map.getWidth())) / 2;
        double offsetY = (canvas.getHeight() - (tileSize * map.getHeight())) / 2;

        for (int row = 0; row < map.getHeight(); row++) {
            for (int col = 0; col < map.getWidth(); col++) {
                Position pos = new Position(row, col);
                double x = offsetX + col * tileSize;
                double y = offsetY + row * tileSize;

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
        Map map = game.getMap();  // <-- add this
        double tileSize = Math.min(canvas.getWidth() / map.getWidth(), canvas.getHeight() / map.getHeight());
        double offsetX = (canvas.getWidth() - (tileSize * map.getWidth())) / 2;
        double offsetY = (canvas.getHeight() - (tileSize * map.getHeight())) / 2;

        double x = offsetX + entity.getPosition().getCol() * tileSize;
        double y = offsetY + entity.getPosition().getRow() * tileSize;

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
