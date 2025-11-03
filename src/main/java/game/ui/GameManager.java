package game.ui;

import game.core.Direction;
import game.core.Game;
import javafx.animation.AnimationTimer;

public class GameManager {
    private Game game;
    private HUD hud;

    private boolean running = false;
    private Direction currentDirection = Direction.NONE;

    private AnimationTimer gameLoop;
    

    public GameManager (Game game, HUD hud){
        this.game = game;
        this.hud = hud;

        gameLoop = new AnimationTimer(){
            private long lastUpdate = 0;

            @Override
            public void handle(long now){
                if (lastUpdate == 0){
                    lastUpdate = now;
                    return;
                }

                double deltaTime = (now - lastUpdate)/1_000_000_000.0;

                if (deltaTime >= (1.0/GameConfig.GAME_TICK_RATE)) {
                    tick();
                    lastUpdate = now;
                }
            }

        };
    }

    private void tick() {
        if (!running) return;

        game.tick(currentDirection);

        game.resolveCollisions();
        if (game.checkWin()){
            hud.showMessage("You Win!");
            pauseGame();
        } else if (game.checkLose()){
            hud.showMessage("You Lose!");
            pauseGame();
        }

        hud.update();
    }

    public void startGame(){
        running = true;
        game.reset();
        hud.clearMessage();
        gameLoop.start();
    }

    public void pauseGame(){
        running = false;
    }

    public void resumeGame(){
        game.reset();
        hud.clearMessage();
        startGame();
    }

    public void setCurrentDirection(Direction dir){
        this.currentDirection = dir;
    }
}
