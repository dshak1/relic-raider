package game.ui;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GameEndScreen utility class.
 * Note: Requires JavaFX toolkit initialization.
 */
public class GameEndScreenTest {
    
    @BeforeAll
    static void initJavaFX() {
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {});
        }
    }
    
    @Test
    void testShowWin() {
        Platform.runLater(() -> {
            boolean[] callbackCalled = {false};
            Runnable backToMenu = () -> callbackCalled[0] = true;
            
            assertDoesNotThrow(() -> {
                GameEndScreen.showWin(100, 60, backToMenu);
            }, "Should show win screen without errors");
        });
    }
    
    @Test
    void testShowLose() {
        Platform.runLater(() -> {
            boolean[] callbackCalled = {false};
            Runnable backToMenu = () -> callbackCalled[0] = true;
            
            assertDoesNotThrow(() -> {
                GameEndScreen.showLose(50, 30, backToMenu);
            }, "Should show lose screen without errors");
        });
    }
}

