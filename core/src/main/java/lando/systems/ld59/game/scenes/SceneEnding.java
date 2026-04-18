package lando.systems.ld59.game.scenes;

import lando.systems.ld59.game.systems.ViewSystem;
import lando.systems.ld59.screens.GameScreen;

public class SceneEnding extends Scene<GameScreen> {

    public SceneEnding(GameScreen screen) {
        super(screen);

        createView(640, 360);
        createMap("maps/finale.tmx");

        // Follow the player
        screen.engine.getSystem(ViewSystem.class).target(player);
    }
}
