package game.ui;

import game.core.Direction;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

/**
 * The {@code InputController} class handles user keyboard input for the game.
 * It listens for key press and release events, and translates them into {@link Direction} 
 * values that control player movement.
 */
public class InputController {
    private Direction currentDirection = Direction.NONE;
    
    /**
     * Handles a keyboard input event and updates the current direction.
     * The direction is set based on the key pressed, and reset to
     * {@link Direction#NONE} when a movement key is released.
     *
     * @param event the {@link KeyEvent} triggered by user input
     */
    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();

        // Set movement direction
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            int keyCode = code.getCode();
            if (keyCode == GameConfig.KEY_UP) {
                currentDirection = Direction.UP;
            } else if (keyCode == GameConfig.KEY_DOWN) {
                currentDirection = Direction.DOWN;
            } else if (keyCode == GameConfig.KEY_LEFT) {
                currentDirection = Direction.LEFT;
            } else if (keyCode == GameConfig.KEY_RIGHT) {
                currentDirection = Direction.RIGHT;
            }
        }

        // Stop movement if key released
        else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
            if (code.getCode() == GameConfig.KEY_UP && currentDirection == Direction.UP ||
                code.getCode() == GameConfig.KEY_DOWN && currentDirection == Direction.DOWN ||
                code.getCode() == GameConfig.KEY_LEFT && currentDirection == Direction.LEFT ||
                code.getCode() == GameConfig.KEY_RIGHT && currentDirection == Direction.RIGHT) {
                currentDirection = Direction.NONE;
            }
        }
    }

    /**
     * Returns the current movement direction based on the user's last input
     *
     * @return the {@link Direction} representing the user's last input
     */
    public Direction getDirection() {
        return currentDirection;
    }
}
