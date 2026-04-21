package lando.systems.ld59.game.scenes;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld59.Config;
import lando.systems.ld59.ShakeAmounts;
import lando.systems.ld59.assets.ImageType;
import lando.systems.ld59.assets.anims.AnimBaseButton;
import lando.systems.ld59.assets.anims.AnimBaseCity;
import lando.systems.ld59.assets.anims.AnimBaseTurret;
import lando.systems.ld59.assets.anims.AnimMisc;
import lando.systems.ld59.assets.EmitterType;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.Systems;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.renderable.Image;
import lando.systems.ld59.game.signals.ScreenShakeEvent;
import lando.systems.ld59.particles.effects.PetConfettiEffect;
import lando.systems.ld59.particles.effects.ExplosionEffect;
import lando.systems.ld59.particles.effects.SparkleEffect;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.screens.GameScreen;
import lando.systems.ld59.utils.FramePool;
import lando.systems.ld59.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class SceneGame extends Scene<GameScreen> implements InputProcessor {

    private final Animator cityAnimator;
    private final List<Entity> turrets = new ArrayList<>();

    public SceneGame(GameScreen screen, int numTurrets) {
        super(screen);
        var sceneContainer = Factory.createEntity();
        sceneContainer.add(new SceneContainer(this));
        engine().addEntity(sceneContainer);

        createView(Config.framebuffer_width, Config.framebuffer_height);

        // Create background anim spawner entity
        var bgAnimSpawner = Factory.createEntity();
        bgAnimSpawner.add(new BackgroundAnimSpawner());
        engine().addEntity(bgAnimSpawner);

        var worldCamWidth = screen.worldCamera.viewportWidth;
        var worldCamHeight = screen.worldCamera.viewportHeight;
        var worldCenterX = worldCamWidth / 2f;
        var worldCenterY = worldCamHeight / 2f;

        var background = Factory.background(ImageType.BACKGROUND_TITLE, new Vector2(0, 0), new Vector2(worldCamWidth, worldCamHeight));
        var base = Factory.base(worldCenterX, 0f);
        var baseComp = Components.get(base, Base.class);
        this.cityAnimator = Components.get(baseComp.city, Animator.class);

        // Buttons -----------------------------------------------------------------------------------------------------
        var baseButtonBoardHeight = 280f;
        var pinBoardToTop = worldCamHeight - baseButtonBoardHeight / 2f;
        var pinBoardToCenter = worldCenterY;
//        var boardCenterY = pinBoardToTop;
        var boardCenterY = pinBoardToCenter;
        var baseButtonBoardLeft = Factory.baseButtonBoard(AnimBaseButton.BOARD_LEFT, 0, boardCenterY);
        var baseButtonBoardRight = Factory.baseButtonBoard(AnimBaseButton.BOARD_RIGHT, worldCamWidth, boardCenterY);

        var baseButtonBoardPosition = Components.get(baseButtonBoardLeft, Position.class);
        var baseButtonBoardAnimator = Components.get(baseButtonBoardLeft, Animator.class);
        var baseButtonBoardTop = baseButtonBoardPosition.y + baseButtonBoardAnimator.size.y;

        var buttonDistY = 80f;
        var boardCenterX = 48f;
        var boardCenterYTopSlot = baseButtonBoardTop - 40f;

        // @formatter:off
        var redButton      = Factory.baseButton(BaseButton.Type.RED,      boardCenterX, boardCenterYTopSlot);
        var greenButton    = Factory.baseButton(BaseButton.Type.GREEN,    boardCenterX, boardCenterYTopSlot - buttonDistY);
        var blueButton     = Factory.baseButton(BaseButton.Type.BLUE,     boardCenterX, boardCenterYTopSlot - buttonDistY - buttonDistY);
        var triangleButton = Factory.baseButton(BaseButton.Type.TRIANGLE, worldCamWidth - boardCenterX, boardCenterYTopSlot);
        var squareButton   = Factory.baseButton(BaseButton.Type.SQUARE,   worldCamWidth - boardCenterX, boardCenterYTopSlot - buttonDistY);
        var circleButton   = Factory.baseButton(BaseButton.Type.CIRCLE,   worldCamWidth - boardCenterX, boardCenterYTopSlot - buttonDistY - buttonDistY);
        // @formatter:on

        // Turrets------------------------------------------------------------------------------------------------------
        // Place turrets on the planet, layout follows planet curve
        // - planet center is offscreen downward
        var center = FramePool.vec2(worldCenterX, -410f);
        // - planet isn't perfect circle, bends slightly more on one axis than the other
        var distFromCenter = FramePool.vec2(580f, 600);
        // - starting on the right, create a turret every 'rotatationStep' degrees along planet curve
        float rotationRange = 100f;
        float rotationStep = rotationRange / (numTurrets+1);
        for (int i = 0; i < numTurrets; i++) {
            var rotation = 90 - (rotationRange / 2f) + rotationStep * (i + 1);
            var x = center.x + MathUtils.cosDeg(rotation) * distFromCenter.x;
            var y = center.y + MathUtils.sinDeg(rotation) * distFromCenter.y;
            var turret = Factory.turret(x, y, rotation);
            engine().addEntity(turret);
            turrets.add(turret);
        }

        engine().addEntity(background);
        engine().addEntity(base);
        engine().addEntity(baseButtonBoardLeft);
        engine().addEntity(baseButtonBoardRight);
        engine().addEntity(redButton);
        engine().addEntity(greenButton);
        engine().addEntity(blueButton);
        engine().addEntity(circleButton);
        engine().addEntity(squareButton);
        engine().addEntity(triangleButton);
    }

    public void cityAnimTest() {
        var cityAnim = (AnimBaseCity) cityAnimator.type;
        cityAnimator.play(cityAnim.next());
    }

    public void turretAnimTest() {
        for (var entity : turrets) {
            var turret = Components.get(entity, Turret.class);
            var animator = Components.get(turret.cannon, Animator.class);
            var animType = (AnimBaseTurret) animator.type;
            animator.play(animType.nextBarrel());
        }
    }

    private static final Family CONNECTIONS = Family.one(Connection.class).get();
    public void shakeConnectionsTest() {
        ScreenShakeEvent.shake(ShakeAmounts.ROPE_TEST);
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

        var clickPos = new Position(touchPos.x, touchPos.y);
        var testEmitter1 = Factory.emitter(EmitterType.SPARKLE,   new SparkleEffect.Params(clickPos));
        var testEmitter2 = Factory.emitter(EmitterType.EXPLOSION, new ExplosionEffect.Params(clickPos));
        var testEmitter3 = Factory.emitter(EmitterType.CONFETTI,  new PetConfettiEffect.Params(clickPos));
        engine().addEntity(testEmitter1);
//        engine().addEntity(testEmitter2);
//        engine().addEntity(testEmitter3);
//        AudioEvent.playSound(SoundType.PLUG1, .25f);

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
