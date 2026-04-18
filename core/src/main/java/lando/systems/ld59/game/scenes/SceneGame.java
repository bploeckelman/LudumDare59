package lando.systems.ld59.game.scenes;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.Config;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.BaseButton;
import lando.systems.ld59.game.components.EnemyTag;
import lando.systems.ld59.game.components.EnergyColor;
import lando.systems.ld59.game.components.SceneContainer;
import lando.systems.ld59.screens.GameScreen;

public class SceneGame extends Scene<GameScreen> {

    public SceneGame(GameScreen screen, int turrets) {
        super(screen);
        var sceneContainer = Factory.createEntity();
        sceneContainer.add(new SceneContainer(this));
        engine().addEntity(sceneContainer);

        createView(Config.framebuffer_width, Config.framebuffer_height);

        var centerX = screen.worldCamera.viewportWidth / 2f;
        var topY = screen.worldCamera.viewportHeight;
        var buttonY = 30f;

        var base = Factory.base(centerX, 0f);
        var enemy1 = Factory.enemyShip(EnemyTag.EnemyType.getRandom(), EnergyColor.Type.getRandom(), centerX + 150, topY, 10f, -10f);
        var enemy2 = Factory.enemyShip(EnemyTag.EnemyType.getRandom(), EnergyColor.Type.getRandom(), centerX - 150, topY, -10f, -10f);
        var blueButton = Factory.baseButton(BaseButton.Type.BLUE, centerX - 125, buttonY);
        var triangleButton = Factory.baseButton(BaseButton.Type.TRIANGLE, centerX + 125, buttonY);

        float rotationRange = 120f;
        float deltaRotation = rotationRange / (turrets+1);
        for (int i = 0; i < turrets; i++) {
            var rotation = -rotationRange / 2f + deltaRotation * (i+1);
            var x = centerX + MathUtils.sinDeg(rotation) * 600f;
            var y = -410f + MathUtils.cosDeg(rotation) * 620f;
            var turret = Factory.turret(x, y, rotation);
            engine().addEntity(turret);
        }

        engine().addEntity(base);
        engine().addEntity(enemy1);
        engine().addEntity(enemy2);
        engine().addEntity(blueButton);
        engine().addEntity(triangleButton);
    }
}
