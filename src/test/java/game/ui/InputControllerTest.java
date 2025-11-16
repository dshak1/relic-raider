package game.ui;

import game.core.Direction;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for InputController class.
 * Note: Requires JavaFX toolkit initialization.
 */
public class InputControllerTest {
    
    private InputController controller;
    private Scene scene;
    
    @BeforeAll
    static void initJavaFX() {
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {});
        }
    }
    
    @BeforeEach
    void setUp() {
        controller = new InputController();
        // Create a scene to fire events
        Pane root = new Pane();
        scene = new Scene(root, 100, 100);
    }
    
    @Test
    void testInitialDirection() {
        assertEquals(Direction.NONE, controller.getDirection(), 
            "Initial direction should be NONE");
    }
    
    @Test
    void testGetDirection() {
        assertEquals(Direction.NONE, controller.getDirection());
    }
    
    @Test
    void testIsPausePressed_InitiallyFalse() {
        assertFalse(controller.isPausePressed(), 
            "Pause should not be pressed initially");
    }
    
    @Test
    void testResetPause() {
        controller.resetPause();
        assertFalse(controller.isPausePressed(), "Pause should be reset");
    }
    
    @Test
    void testInputControllerConstruction() {
        assertNotNull(controller);
        assertEquals(Direction.NONE, controller.getDirection());
    }
}

