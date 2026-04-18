package lando.systems.ld59.game.scenes;

import lando.systems.ld59.Config;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.SceneContainer;
import lando.systems.ld59.screens.GameScreen;

public class SceneGame extends Scene<GameScreen> {

    public SceneGame(GameScreen screen) {
        super(screen);
        var sceneContainer = Factory.createEntity();
        sceneContainer.add(new SceneContainer(this));
        engine().addEntity(sceneContainer);

        createView(Config.framebuffer_width, Config.framebuffer_height);

        var centerX = (int) (screen.worldCamera.viewportWidth / 2);

        var base = Factory.base(centerX, 0);
        var turret = Factory.turret(centerX, 190);

        engine().addEntity(base);
        engine().addEntity(turret);
    }
}
