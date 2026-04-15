package lando.systems.ld59.screens;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.widget.VisTable;
import lando.systems.ld59.Config;
import lando.systems.ld59.Flag;
import lando.systems.ld59.game.signals.SignalEvent;
import lando.systems.ld59.utils.FramePool;

public class IntroScreen extends BaseScreen implements Listener<SignalEvent> {

    private static final Color BACKGROUND_COLOR = new Color(.1f, .5f, 1f, 1f);

    private float accum;

    public IntroScreen() {
        initializeUI();

        // Tick the engine for one frame first to get everything initialized
        engine.update(0f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        accum += delta;

        if (!transitioning) {
            transitioning = true;

            // TODO: maybe move the cleanup code to a BaseScreen method, or make abstract method in BaseScreen to keep cleanup code together
            // Cleanup ECS stuff from this screen before moving to the next screen/scene
//            SignalEvent.removeListener(this);
//            SignalEvent.removeListener(scene);
//            SignalEvent.removeListener(Systems.playerState);
//            engine.removeSystem(Systems.playerState);
//            engine.removeAllEntities();

//            AudioEvent.stopAllMusic();

            game.setScreen(new GameScreen());
        }

        // Pause for frame-stepping if enabled
        if (Flag.FRAME_STEP.isEnabled()) {
            Config.stepped_frame = Gdx.input.isKeyJustPressed(Input.Keys.NUM_9);
            if (!Config.stepped_frame) {
                return;
            }
        }

        engine.update(delta);
        uiStage.act(delta);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(BACKGROUND_COLOR);

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
        }
        batch.end();
        batch.setShader(null);

        // Draw ui / dialog / story stuff
        uiStage.draw();

        // Screen name overlay
        if (Flag.DEBUG_RENDER.isEnabled()) {
            batch.setProjectionMatrix(windowCamera.combined);
            batch.begin();
            var pos = FramePool.vec2(
                (windowCamera.viewportWidth - layout.getWidth()) / 2f,
                windowCamera.viewportHeight - layout.getHeight());
            font.drawGlyphs(batch, layout, pos.x, pos.y);
            batch.end();
        }
    }

    @Override
    public void initializeUI() {
        var root = new VisTable();
        root.setFillParent(true);
        uiStage.addActor(root);
    }

    @Override
    public void receive(Signal<SignalEvent> signal, SignalEvent object) {

    }
}
