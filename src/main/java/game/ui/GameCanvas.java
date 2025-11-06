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
    public void draw(Game game, boolean setupMode) {
        clear();
        drawMap(game.getMap(), setupMode);
        drawEntities(game, setupMode);
    }

    /**
     * Draws the map grid using tile sprites
     *
     * @param map the {@link Map} to render
     */
    private void drawMap(Map map, boolean setupMode) {
        // Check if exit is unlocked (final reward collected)
        boolean exitUnlocked = game.isFinalRewardCollected();
        
        if (setupMode) {
            // Setup mode: show entire map
            double tileSize = Math.min(canvas.getWidth() / map.getWidth(), 
                                       canvas.getHeight() / map.getHeight());
            
            for (int row = 0; row < map.getHeight(); row++) {
                for (int col = 0; col < map.getWidth(); col++) {
                    Position pos = new Position(row, col);
                    double x = col * tileSize;
                    double y = row * tileSize;
    
                    String type;
                    if (map.isBlocked(pos)) {
                        type = "wall";
                    } else if (map.isEntry(pos)) {
                        type = "entry";
                    } else if (map.isExit(pos)) {
                        // If exit is unlocked, show open door sprite
                        // Otherwise show closed door sprite
                        if (exitUnlocked) {
                            type = "doorOpen"; // Use open door sprite when unlocked
                        } else {
                            type = "door"; // Use closed door sprite when locked
                        }
                    } else {
                        type = "floor";
                    }
    
                    Image tile = SpriteManager.getTileSprite(type);
                    gc.drawImage(tile, x, y, tileSize, tileSize);
                }
            }
        } else {
            // Normal mode: viewport centered on player (existing code)
            final int VIEWPORT_WIDTH = 15;
            final int VIEWPORT_HEIGHT = 11;
            
            double tileSize = Math.min(canvas.getWidth() / VIEWPORT_WIDTH, 
                                       canvas.getHeight() / VIEWPORT_HEIGHT);
            
            Position playerPos = game.getPlayer().getPosition();
            
            int startRow = Math.max(0, playerPos.getRow() - VIEWPORT_HEIGHT/2);
            int endRow = Math.min(map.getHeight(), startRow + VIEWPORT_HEIGHT);
            int startCol = Math.max(0, playerPos.getCol() - VIEWPORT_WIDTH/2);
            int endCol = Math.min(map.getWidth(), startCol + VIEWPORT_WIDTH);
            
            double offsetX = (canvas.getWidth() - (tileSize * VIEWPORT_WIDTH)) / 2;
            double offsetY = (canvas.getHeight() - (tileSize * VIEWPORT_HEIGHT)) / 2;
            
            for (int row = startRow; row < endRow; row++) {
                for (int col = startCol; col < endCol; col++) {
                    Position pos = new Position(row, col);
                    double x = offsetX + (col - startCol) * tileSize;
                    double y = offsetY + (row - startRow) * tileSize;
    
                    String type;
                    if (map.isBlocked(pos)) {
                        type = "wall";
                    } else if (map.isEntry(pos)) {
                        type = "entry";
                    } else if (map.isExit(pos)) {
                        // If exit is unlocked, show open door sprite
                        // Otherwise show closed door sprite
                        if (exitUnlocked) {
                            type = "doorOpen"; // Use open door sprite when unlocked
                        } else {
                            type = "door"; // Use closed door sprite when locked
                        }
                    } else {
                        type = "floor";
                    }
    
                    Image tile = SpriteManager.getTileSprite(type);
                    gc.drawImage(tile, x, y, tileSize, tileSize);
                }
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
    private void drawEntities(Game game, boolean setupMode) {
        for (Reward reward : game.getRewards()) {
            // Only draw rewards that haven't been collected
            if (!reward.isCollected()) {
                // For bonus rewards, also check if they're active
                if (reward instanceof game.reward.BonusReward) {
                    game.reward.BonusReward bonusReward = (game.reward.BonusReward) reward;
                    if (bonusReward.isActive()) {
                        drawEntity(reward, setupMode);
                    }
                } else {
                    // Draw all other rewards (BasicReward, FinalReward) statically
                    drawEntity(reward, setupMode);
                }
            }
        }
    
        for (Enemy enemy : game.getEnemies()) {
            drawEntity(enemy, setupMode);
        }
    
        drawEntity(game.getPlayer(), setupMode);
    }

    /**
     * Draws a single entity.
     *
     * @param entity the entity to draw
     */
    private void drawEntity(Entity entity, boolean setupMode) {
        Map map = game.getMap();
        
        double tileSize;
        double x, y;
        
        if (setupMode) {
            // Setup mode: use full map coordinates
            tileSize = Math.min(canvas.getWidth() / map.getWidth(), 
                               canvas.getHeight() / map.getHeight());
            x = entity.getPosition().getCol() * tileSize;
            y = entity.getPosition().getRow() * tileSize;
        } else {
            // Normal mode: use viewport coordinates (existing code)
            final int VIEWPORT_WIDTH = 15;
            final int VIEWPORT_HEIGHT = 11;
            
            tileSize = Math.min(canvas.getWidth() / VIEWPORT_WIDTH, 
                               canvas.getHeight() / VIEWPORT_HEIGHT);
            Position playerPos = game.getPlayer().getPosition();
            
            int startRow = Math.max(0, playerPos.getRow() - VIEWPORT_HEIGHT/2);
            int startCol = Math.max(0, playerPos.getCol() - VIEWPORT_WIDTH/2);
            
            double offsetX = (canvas.getWidth() - (tileSize * VIEWPORT_WIDTH)) / 2;
            double offsetY = (canvas.getHeight() - (tileSize * VIEWPORT_HEIGHT)) / 2;
            
            x = offsetX + (entity.getPosition().getCol() - startCol) * tileSize;
            y = offsetY + (entity.getPosition().getRow() - startRow) * tileSize;
        }
    
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
