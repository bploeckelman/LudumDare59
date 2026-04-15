package lando.systems.ld59.game.scenes;

import lando.systems.ld59.game.systems.ViewSystem;
import lando.systems.ld59.screens.GameScreen;

public class SceneRelic2 extends Scene<GameScreen> {

    public SceneRelic2(GameScreen screen) {
        super(screen);

        createView(640, 360);
        createMap("maps/relic2.tmx");

        // Follow the player
        screen.engine.getSystem(ViewSystem.class).target(player);
    }
}
