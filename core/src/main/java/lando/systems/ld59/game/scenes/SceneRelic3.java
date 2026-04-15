package lando.systems.ld59.game.scenes;

import lando.systems.ld59.game.systems.ViewSystem;
import lando.systems.ld59.screens.GameScreen;

public class SceneRelic3 extends Scene<GameScreen> {

    public SceneRelic3(GameScreen screen) {
        super(screen);

        createView(640, 360);
        createMap("maps/relic3.tmx");

        // Follow the player
        screen.engine.getSystem(ViewSystem.class).target(player);
    }
}
