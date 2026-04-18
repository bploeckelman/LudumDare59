package lando.systems.ld59.game.scenes;

import lando.systems.ld59.Config;
import lando.systems.ld59.assets.anims.AnimEnemy;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.SceneContainer;
import lando.systems.ld59.game.components.enemies.Enemy;
import lando.systems.ld59.game.components.enemies.EnemyShipBlack;
import lando.systems.ld59.screens.GameScreen;

public class SceneGame extends Scene<GameScreen> {

    public SceneGame(GameScreen screen) {
        super(screen);
        var sceneContainer = Factory.createEntity();
        sceneContainer.add(new SceneContainer(this));
        engine().addEntity(sceneContainer);

        createView(Config.framebuffer_width, Config.framebuffer_height);

        var centerX = (int) (screen.worldCamera.viewportWidth / 2);
        var topY = (int) (screen.worldCamera.viewportHeight);

        var base = Factory.base(centerX, 0);
        var turret = Factory.turret(centerX, 190);
        var enemy1 = Factory.enemyShip(Enemy.Type.RED, centerX + 50, topY, 20f, -10f);
        var enemy2 = Factory.enemyShip(Enemy.Type.BLACK, centerX - 50, topY, -20f, -10f);

        engine().addEntity(base);
        engine().addEntity(turret);
        engine().addEntity(enemy1);
        engine().addEntity(enemy2);
    }
}
