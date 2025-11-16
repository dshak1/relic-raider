package game.ui;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ShowHowToPlay utility class.
 * Note: Requires JavaFX toolkit initialization.
 */
public class ShowHowToPlayTest {
    
    @BeforeAll
    static void initJavaFX() {
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {});
        }
    }
    
    @Test
    void testShow() {
        Platform.runLater(() -> {
            Stage owner = new Stage();
            assertDoesNotThrow(() -> ShowHowToPlay.show(owner),
                "Should create and show HowToPlay screen without errors");
        });
    }
}

