package lando.systems.ld59.screens;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.digital.Stringf;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.VisLabel;
import lando.systems.ld59.Config;
import lando.systems.ld59.Flag;
import lando.systems.ld59.Main;
import lando.systems.ld59.assets.MusicType;
import lando.systems.ld59.game.signals.AudioEvent;
import lando.systems.ld59.game.signals.SignalEvent;
import lando.systems.ld59.utils.Calc;

public class IntroScreen extends BaseScreen implements Listener<SignalEvent> {

    NinePatch   dialogBox;
    TypingLabel typingLabel;
    String page1 = "";

    // TEMPORARY -----------------------------------------
    private float countdownDurationSecs = 3f;
    private float countdownTimer = countdownDurationSecs;
    private VisLabel countdownLabel;
    // TEMPORARY -----------------------------------------

    private float accum;

    int currentPage = 0;
    float transitionAlpha = 0f;

    public IntroScreen() {
        this.countdownLabel = new VisLabel(Stringf.format("%.1f", countdownTimer));
        dialogBox = Main.game.assets.dialogBox;

        initializeUI();

        AudioEvent.playMusic(MusicType.MAIN_MUSIC);

        typingLabel = new TypingLabel();
        typingLabel.setWidth(Config.window_width * .7f);

        // Tick the engine for one frame first to get everything initialized
        engine.update(0f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        accum += delta;

        countdownLabel.setText(Stringf.format("%.1f", countdownTimer));
        countdownTimer = Calc.clampf(countdownTimer - delta, 0, countdownDurationSecs);

            // TODO: maybe move the cleanup code to a BaseScreen method, or make abstract method in BaseScreen to keep cleanup code together
            // Cleanup ECS stuff from this screen before moving to the next screen/scene
//            SignalEvent.removeListener(this);
//            SignalEvent.removeListener(scene);
//            SignalEvent.removeListener(Systems.playerState);
//            engine.removeSystem(Systems.playerState);
//            engine.removeAllEntities();

//            AudioEvent.stopAllMusic();

        // Pause for frame-stepping if enabled
        if (Flag.FRAME_STEP.isEnabled()) {
            Config.stepped_frame = Gdx.input.isKeyJustPressed(Input.Keys.NUM_9);
            if (!Config.stepped_frame) {
                return;
            }
        }
        if (Gdx.input.justTouched() && accum > .5f) {
            accum = 0;
            var touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            worldCamera.unproject(touchPos);

            if (transitionAlpha < 1f) {
                transitionAlpha = 1f;
            } else if (!typingLabel.hasEnded()) {
                typingLabel.skipToTheEnd();
            } else {
                currentPage++;
                if (currentPage == 1) {
                    game.setScreen(new GameScreen());
                }
                if (!transitioning && countdownTimer <= 0) {
                    transitioning = true;
                }

            }
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(.0f, .0f, .1f, 1f);

        batch.enableBlending();
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, transitionAlpha);
        dialogBox.draw(batch, windowCamera.viewportWidth / 4, windowCamera.viewportHeight / 4, windowCamera.viewportWidth / 2, windowCamera.viewportHeight / 4);

        typingLabel.draw(batch, 1f);

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
            var screenName = new VisLabel(getClass().getSimpleName());
            screenName.setPosition(10, windowCamera.viewportHeight - 10 - screenName.getHeight());
            uiStage.addActor(screenName);
        }

        // TEMP: remove when we have story stuff in this screen
        uiRoot.add(countdownLabel).expand().center();
    }
}
