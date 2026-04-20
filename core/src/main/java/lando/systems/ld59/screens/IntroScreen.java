package lando.systems.ld59.screens;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.digital.Stringf;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.VisLabel;
import lando.systems.ld59.Config;
import lando.systems.ld59.Flag;
import lando.systems.ld59.Main;
import lando.systems.ld59.assets.FontType;
import lando.systems.ld59.assets.MusicType;
import lando.systems.ld59.game.signals.AudioEvent;
import lando.systems.ld59.game.signals.SignalEvent;
import lando.systems.ld59.utils.Calc;

public class IntroScreen extends BaseScreen implements Listener<SignalEvent> {

    NinePatch   dialogBox;
    TypingLabel typingLabel;
    String page1 = "";
//    Rectangle skipButton = new Rectangle(Gdx.graphics.getWidth() - 200, 20, 180, 80);

    // TEMPORARY -----------------------------------------
    private float countdownDurationSecs = .01f;
    private float countdownTimer = countdownDurationSecs;
    private VisLabel countdownLabel;
    // TEMPORARY -----------------------------------------

    private float accum;

    int currentPage = 0;
    float transitionAlpha = 0f;

    public IntroScreen() {
        this.countdownLabel = new VisLabel(Stringf.format("%.1f", countdownTimer));
        dialogBox = Main.game.assets.dialogBox;

//        initializeUI();


//        typingLabel = new TypingLabel();
//        typingLabel.setWidth(Config.window_width * .8f);

        // Tick the engine for one frame first to get everything initialized
        engine.update(0f);
        AudioEvent.playMusic(MusicType.INTRO_MUSIC);

        String page1 =
            "{COLOR=white}" +
                //"A 0123456789-01234567890-1234567890-1234567890-0123456789-B" +
                "The year is 2027.\n\n" +
                "In this far-flung future, some things have changed.\n\n " +
                "Notably, all of earth's civilization now lives in a bubble in the center of the planet"

            ;

//        typingLabel = new TypingLabel(page1, FontType.HEMI_HEAD.get());
//        typingLabel.setPosition(worldCamera.viewportWidth * .05f,
//            worldCamera.viewportHeight * 0.5f);
//        typingLabel.setWidth(Config.window_width * .9f);
//        typingLabel.wrap = true;
//        typingLabel.setScale(.6f);
//        typingLabel.getFont().adjustLineHeight(1.125f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (!transitioning && Gdx.input.justTouched()){
            transitioning = true;
            game.setScreen(new TitleScreen());
        }
//        if (Gdx.input.justTouched() && accum > .015f) {
//            accum = 0;
//            var touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
//            worldCamera.unproject(touchPos);
//
//            if (transitionAlpha < 1f) {
//                transitionAlpha = 1f;
//            }
//            else if (!typingLabel.hasEnded()) {
//                typingLabel.skipToTheEnd();
//            }
//            else {
//                currentPage++;
//                if (currentPage == 1) {
//                    game.setScreen(new GameScreen());
//                }
////                if (!transitioning && countdownTimer <= 0) {
////                    transitioning = true;
////                }
//            }
//        }
    }

    @Override
    public void render(float delta) {

//        ScreenUtils.clear(Color.DARK_GRAY);
        ScreenUtils.clear(.0f, .0f, .1f, 1f);
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            // ...
        }
        batch.end();

        uiStage.draw();

        //        batch.enableBlending();
        //        batch.setProjectionMatrix(windowCamera.combined);
        //        batch.begin();
        //        batch.setColor(1f, 1f, 1f, transitionAlpha);
        ////        dialogBox.draw(batch, windowCamera.viewportWidth / 4, windowCamera.viewportHeight / 4, windowCamera.viewportWidth / 2, windowCamera.viewportHeight / 4);
        //
        ////        typingLabel.draw(batch, 1f);
        //
        //        batch.end();
        //        batch.setShader(null);
        //
        //        uiStage.draw();
    }

    @Override
    public void receive(Signal<SignalEvent> signal, SignalEvent object) {

    }

    @Override
    protected void initializeUI() {
        String page1 =
            "{COLOR=white}" +
                //"A 0123456789-01234567890-1234567890-1234567890-0123456789-B" +
                "The year is 2027.\n\n" +
                "In this far-flung future, some things have changed.\n\n " +
                "Notably, all of earth's civilization now lives in a bubble in the center of the planet"

            ;

        if (Flag.DEBUG_RENDER.isEnabled()) {
            var screenName = new VisLabel(getClass().getSimpleName());
            screenName.setPosition(10, windowCamera.viewportHeight - 10 - screenName.getHeight());
            uiStage.addActor(screenName);
        }

        var clickToBegin = new TypingLabel(page1, FontType.HEMI_HEAD.get());
        uiRoot.add(clickToBegin)
            .expand()
            .center();
    }
}
