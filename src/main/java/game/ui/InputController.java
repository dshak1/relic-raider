package game.ui;

import game.core.Direction;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

public class InputController {
    private Direction currentDirection = Direction.NONE;
    
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

    public Direction getDirection() {
        return currentDirection;
    }
}
