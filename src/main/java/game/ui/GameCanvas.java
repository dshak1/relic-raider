package game.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import game.core.Game;
import game.entity.Entity;
import game.entity.Player;
import game.entity.Enemy;
import game.reward.Reward;
import game.map.Map;
import game.map.Position;
import game.ui.HUD;

/**
 * The {@code GameCanvas} class is responsible for rendering the game; it draws the map, 
 * entities, and HUD onto a JavaFX {@link Canvas}.
 */
public class GameCanvas {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Game game;
    private final HUD hud;

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
        this.hud = new HUD(this.game);
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
        drawHUD(game);
    }

    /**
     * Draws the map grid using tile sprites
     *
     * @param map the {@link Map} to render
     */
    private void drawMap(Map map) {
        int tileSize = GameConfig.TILE_SIZE;

        for (int row = 0; row < map.getHeight(); row++) {
            for (int col = 0; col < map.getWidth(); col++) {
                Position pos = new Position(row, col);
                double x = col * tileSize;
                double y = row * tileSize;
                
                // Render tile appearances - need constants in GameConfig for sprites
                if (map.isBlocked(pos)) {
                    drawTile(ResourceLoader.loadImage(GameConfig.IMAGE_WALL), x, y, tileSize);
                } else if (map.isEntry(pos)) {
                    drawTile(ResourceLoader.loadImage(GameConfig.IMAGE_ENTRY), x, y, tileSize);
                } else if (map.isExit(pos)) {
                    drawTile(ResourceLoader.loadImage(GameConfig.IMAGE_EXIT), x, y, tileSize);
                } else {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(x, y, tileSize, tileSize);
                } 
                
            }
        }
    }

    /**
     * Draws all active entities in the game (rewards, enemies, player).
     *
     * @param game the current {@link Game} instance 
     */
    private void drawEntities(Game game) {
        // draw all of the rewards
        for (Reward reward : game.getRewards()) {
            drawEntity(reward);
        }

        // draw all of the enemies
        for (Enemy enemy : game.getEnemies()) {
            drawEntity(enemy);
        }

        // draw the player
        drawEntity(game.getPlayer()); 
    }

    /**
     * Draws a single entity using its sprite 
     *
     * @param entity the {@link Entity} to draw
     */
    public void drawEntity(Entity e) {
        int tileSize = GameConfig.TILE_SIZE;
        double x = e.getPosition().getCol() * tileSize;
        double y = e.getPosition().getRow() * tileSize;

        // *** Add a SpriteManager class to map entity classes to their sprites (to keep logic decoupled)
        Image sprite = SpriteManager.getSprite(e);
        if (sprite != null) {
            gc.drawImage(sprite, x, y, tileSize, tileSize);
        } else {
            // placeholder as a fallback
            gc.setFill(Color.WHITE);
            gc.fillRect(x, y, tileSize, tileSize);
        }
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
     * Draws the HUD overlay (score, timer)
     *
     * @param game the current {@link Game} instance
     */
    public void drawHUD(Game game) {
        // update the HUD (score & time)
        hud.update();
        hud.showMessage(game.isGameOver() ? "Game Over" : "");
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
