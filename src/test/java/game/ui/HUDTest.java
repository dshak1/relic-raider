package game.ui;

import game.core.Game;
import game.entity.Player;
import game.map.Map;
import game.map.Position;
import javafx.application.Platform;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import game.core.Direction;
import java.time.Duration;

/**
 * Tests for HUD class.
 * Note: Requires JavaFX toolkit initialization.
 */
public class HUDTest {
    
    private Game game;
    private HUD hud;
    
    @BeforeAll
    static void initJavaFX() {
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {});
        }
    }
    
    @BeforeEach
    void setUp() {
        Map map = new Map(10, 10);
        Player player = new Player(new Position(1, 1));
        
        game = Game.builder()
            .setMap(map)
            .setPlayer(player)
            .build();
        
        game.start();
        hud = new HUD(game);
    }
    
    @Test
    void testHUDConstruction() {
        assertNotNull(hud);
        assertEquals(3, hud.getChildren().size(), "HUD should have 3 text elements");
    }
    
    @Test
    void testUpdate() {
        game.setScore(100);
        game.tick(Direction.NONE);
        
        hud.update();
        
        // Verify score text is updated
        Text scoreText = (Text) hud.getChildren().get(0);
        assertTrue(scoreText.getText().contains("100"), 
            "Score text should contain updated score");
    }
    
    @Test
    void testShowMessage() {
        String message = "Test Message";
        hud.showMessage(message);
        
        Text messageText = (Text) hud.getChildren().get(2);
        assertEquals(message, messageText.getText(), 
            "Message text should be set");
    }
    
    @Test
    void testClearMessage() {
        hud.showMessage("Test");
        hud.clearMessage();
        
        Text messageText = (Text) hud.getChildren().get(2);
        assertEquals("", messageText.getText(), 
            "Message should be cleared");
    }
    
    @Test
    void testUpdateTime() {
        // Wait a bit to ensure time passes
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        game.tick(Direction.NONE);
        hud.update();
        
        Text timeText = (Text) hud.getChildren().get(1);
        assertNotNull(timeText.getText());
        assertTrue(timeText.getText().contains("Time:"), 
            "Time text should contain 'Time:'");
    }
}

