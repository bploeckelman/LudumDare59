package lando.systems.ld59.game.scenes;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.Config;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.Systems;
import lando.systems.ld59.game.components.BaseButton;
import lando.systems.ld59.game.components.EnemyTag;
import lando.systems.ld59.game.components.EnergyColor;
import lando.systems.ld59.game.components.SceneContainer;
import lando.systems.ld59.screens.GameScreen;
import lando.systems.ld59.utils.FramePool;

public class SceneGame extends Scene<GameScreen> implements InputProcessor {

    public SceneGame(GameScreen screen, int turrets) {
        super(screen);
        var sceneContainer = Factory.createEntity();
        sceneContainer.add(new SceneContainer(this));
        engine().addEntity(sceneContainer);

        createView(Config.framebuffer_width, Config.framebuffer_height);

        var centerX = screen.worldCamera.viewportWidth / 2f;
        var topY = screen.worldCamera.viewportHeight;
        var buttonY = BaseButton.SIZE - 10;

        var base = Factory.base(centerX, 0f);
        var enemy1 = Factory.enemyShip(EnemyTag.EnemyType.getRandom(), EnergyColor.Type.getRandom(), centerX + 150, topY, 10f, -10f);
        var enemy2 = Factory.enemyShip(EnemyTag.EnemyType.getRandom(), EnergyColor.Type.getRandom(), centerX - 150, topY, -10f, -10f);

        // @formatter:off
        var redButton      = Factory.baseButton(BaseButton.Type.RED,      centerX - 125,           buttonY);
        var greenButton    = Factory.baseButton(BaseButton.Type.GREEN,    centerX - 125 - 80,      buttonY);
        var blueButton     = Factory.baseButton(BaseButton.Type.BLUE,     centerX - 125 - 80 - 80, buttonY);
        var circleButton   = Factory.baseButton(BaseButton.Type.CIRCLE,   centerX + 125,           buttonY);
        var squareButton   = Factory.baseButton(BaseButton.Type.SQUARE,   centerX + 125 + 80,      buttonY);
        var triangleButton = Factory.baseButton(BaseButton.Type.TRIANGLE, centerX + 125 + 80 + 80, buttonY);
        // @formatter:on

        float rotationRange = 120f;
        float deltaRotation = rotationRange / (turrets+1);
        for (int i = 0; i < turrets; i++) {
            var rotation = 90 + -rotationRange / 2f + deltaRotation * (i + 1);
            var x = centerX + MathUtils.cosDeg(rotation) * 600f;
            var y = -410f + MathUtils.sinDeg(rotation) * 620f;
            var turret = Factory.turret(x, y, rotation);
            engine().addEntity(turret);
        }

        for (int i = 0; i < 6; i++) {
            var x = Config.window_width / 8f * i + Config.window_width / 8f;
            var y = topY;
            var spawner = Factory.enemySpawner(x, y);
            engine().addEntity(spawner);
        }

        engine().addEntity(base);
        engine().addEntity(enemy1);
        engine().addEntity(enemy2);
        engine().addEntity(redButton);
        engine().addEntity(greenButton);
        engine().addEntity(blueButton);
        engine().addEntity(circleButton);
        engine().addEntity(squareButton);
        engine().addEntity(triangleButton);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        var touchPos = FramePool.vec3(screenX, screenY, 0);
        screen.worldCamera.unproject(touchPos);

        var baseButtonHandled = Systems.baseButtons.handleTouchUp(touchPos.x, touchPos.y, pointer, button);
        if (baseButtonHandled) return true;

        var turretHandled = Systems.turret.handleTouchUp(touchPos.x, touchPos.y, pointer, button);
        if (turretHandled) return true;

        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
