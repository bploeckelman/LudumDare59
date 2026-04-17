package lando.systems.ld59.screens;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.digital.Stringf;
import com.kotcrab.vis.ui.widget.VisLabel;
import lando.systems.ld59.Config;
import lando.systems.ld59.Flag;
import lando.systems.ld59.game.signals.SignalEvent;
import lando.systems.ld59.utils.Calc;

public class IntroScreen extends BaseScreen implements Listener<SignalEvent> {

    private static final Color BACKGROUND_COLOR = new Color(.1f, .5f, 1f, 1f);

    // TEMPORARY -----------------------------------------
    private float countdownDurationSecs = 3f;
    private float countdownTimer = countdownDurationSecs;
    private VisLabel countdownLabel;
    // TEMPORARY -----------------------------------------

    private float accum;

    public IntroScreen() {
        this.countdownLabel = new VisLabel(Stringf.format("%.1f", countdownTimer));

        initializeUI();

        // Tick the engine for one frame first to get everything initialized
        engine.update(0f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        accum += delta;

        countdownLabel.setText(Stringf.format("%.1f", countdownTimer));
        countdownTimer = Calc.clampf(countdownTimer - delta, 0, countdownDurationSecs);

        if (!transitioning && countdownTimer <= 0) {
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

        uiStage.draw();
    }

    @Override
    public void receive(Signal<SignalEvent> signal, SignalEvent object) {

    }

    @Override
    protected void initializeUI() {
        if (Flag.DEBUG_RENDER.isEnabled()) {
            var screenName = IntroScreen.class.getSimpleName();
            uiRoot.add(new VisLabel(screenName)).pad(10).top().left().row();
        }

        // TEMP: remove when we have story stuff in this screen
        uiRoot.add(countdownLabel).expand().center();
    }
}
