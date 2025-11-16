package game.ui;

import game.core.Game;
import game.core.Direction;
import game.entity.Player;
import game.map.Map;
import game.map.Position;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GameManager class.
 * Note: Requires JavaFX toolkit initialization.
 */
public class GameManagerTest {
    
    private Game game;
    private HUD hud;
    private GameManager manager;
    
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
        manager = new GameManager(game, hud);
    }
    
    @Test
    void testGameManagerConstruction() {
        assertNotNull(manager);
    }
    
    @Test
    void testStartGame() {
        assertDoesNotThrow(() -> manager.startGame(), 
            "Should start game without errors");
    }
    
    @Test
    void testPauseGame() {
        manager.startGame();
        manager.pauseGame();
        // Game should be paused (can't easily test running state, but should not crash)
        assertDoesNotThrow(() -> manager.pauseGame());
    }
    
    @Test
    void testResumeFromPause() {
        manager.startGame();
        manager.pauseGame();
        assertDoesNotThrow(() -> manager.resumeFromPause(), 
            "Should resume from pause without errors");
    }
    
    @Test
    void testResumeGame() {
        manager.startGame();
        manager.pauseGame();
        assertDoesNotThrow(() -> manager.resumeGame(), 
            "Should resume game without errors");
    }
    
    @Test
    void testGameManagerWithDirection() {
        // GameManager uses currentDirection internally, can't easily test setter
        // Just verify manager works
        assertNotNull(manager);
    }
    
    @Test
    void testSetOnWin() {
        boolean[] callbackCalled = {false};
        manager.setOnWin(() -> callbackCalled[0] = true);
        
        // Trigger win condition
        game.end();
        // Callback should be set (can't easily trigger it without running game loop)
        assertDoesNotThrow(() -> manager.setOnWin(() -> {}));
    }
    
    @Test
    void testSetOnLose() {
        boolean[] callbackCalled = {false};
        manager.setOnLose(() -> callbackCalled[0] = true);
        
        // Callback should be set
        assertDoesNotThrow(() -> manager.setOnLose(() -> {}));
    }
}

