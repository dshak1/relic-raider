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
    private boolean pausePressed = false;
    private boolean pauseKeyPressedThisFrame = false;
    
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
            if (code == KeyCode.UP || code == KeyCode.W) {
                currentDirection = Direction.UP;
            } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                currentDirection = Direction.DOWN;
            } else if (code == KeyCode.LEFT || code == KeyCode.A) {
                currentDirection = Direction.LEFT;
            } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                currentDirection = Direction.RIGHT;
            } else if (code == KeyCode.P || code == KeyCode.ESCAPE) {
                // Pause key - only trigger once per press
                if (!pausePressed) {
                    pauseKeyPressedThisFrame = true;
                    pausePressed = true;
                }
            }
        }

        // Stop movement if key released
        else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
            if ((code == KeyCode.UP || code == KeyCode.W) && currentDirection == Direction.UP ||
                (code == KeyCode.DOWN || code == KeyCode.S) && currentDirection == Direction.DOWN ||
                (code == KeyCode.LEFT || code == KeyCode.A) && currentDirection == Direction.LEFT ||
                (code == KeyCode.RIGHT || code == KeyCode.D) && currentDirection == Direction.RIGHT) {
                currentDirection = Direction.NONE;
            } else if (code == KeyCode.P || code == KeyCode.ESCAPE) {
                pausePressed = false;
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
    
    /**
     * Checks if the pause key was pressed this frame.
     * Call this once per frame and it will return true only once per press.
     * 
     * @return true if pause key was pressed this frame
     */
    public boolean isPausePressed() {
        if (pauseKeyPressedThisFrame) {
            pauseKeyPressedThisFrame = false;
            return true;
        }
        return false;
    }
    
    /**
     * Resets the pause key state.
     */
    public void resetPause() {
        pausePressed = false;
        pauseKeyPressedThisFrame = false;
    }
}
