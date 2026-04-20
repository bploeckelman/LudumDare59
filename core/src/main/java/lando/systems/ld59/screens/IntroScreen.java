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
//    Rectangle skipButton = new Rectangle(Gdx.graphics.getWidth() - 200, 20, 180, 80);

    // TEMPORARY -----------------------------------------
    private float countdownDurationSecs = .01f;
    private float countdownTimer = countdownDurationSecs;
    private VisLabel countdownLabel;
    // TEMPORARY -----------------------------------------

    private float accum;
    private TypingLabel storyText;
    private String page1;
    private String page2;
    private String page3;
    private TypingLabel pageCounter;
    float storyAccum = 0f;
    boolean hasClicked = false;

    int currentPage = 1;
    float transitionAlpha = 0f;

    public IntroScreen() {
        this.countdownLabel = new VisLabel(Stringf.format("%.1f", countdownTimer));
        dialogBox = Main.game.assets.dialogBox;

        initializeUI();


//        typingLabel = new TypingLabel();
//        typingLabel.setWidth(Config.window_width * .8f);

        // Tick the engine for one frame first to get everything initialized
        engine.update(0f);
        AudioEvent.playMusic(MusicType.INTRO_MUSIC);



//        typingLabel = new TypingLabel(page1, FontType.HEMI_HEAD.get());
//        typingLabel.setPosition(worldCamera.viewportWidth * .05f,
//            worldCamera.viewportHeight * 0.5f);
//        typingLabel.setWidth(Config.window_width * .9f);
//        typingLabel.wrap = true;
//        typingLabel.setScale(.6f);
//        typingLabel.getFont().adjustLineHeight(1.125f);
    }

    private String getNextStory() {
        switch (currentPage) {
            case 1: currentPage ++; return page2;
            case 2: currentPage ++; return page3;
            case 3: currentPage ++; return page3;
            case 4: currentPage = 4; return page3;
            default: return page3;
        }
    }

    private String getPageCount() {
        switch (currentPage) {
            case 1: return "1/3";
            case 2: return "2/3";
            case 3: return "3/3";
            case 4: return "3/3";
            default: return "Go!";
        }
    }
    @Override
    public void update(float delta) {
        super.update(delta);
        if(currentPage < 4) {
            storyAccum -= delta;
        }
        if(hasClicked) {
            if (Gdx.input.justTouched() ) {
                storyText.setText(getNextStory());
                storyText.restart();
                pageCounter.setText(getPageCount());
                hasClicked = false;
            }
        }
        if (!transitioning && Gdx.input.justTouched()){
            storyText.skipToTheEnd();
            storyAccum = 5f;
            hasClicked = true;
        }

        if (currentPage == 4 && storyText.hasEnded() && !transitioning && Gdx.input.justTouched()) {
            transitioning = true;
            game.setScreen(new GameScreen());
        }

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

    }

    @Override
    public void receive(Signal<SignalEvent> signal, SignalEvent object) {

    }

    @Override
    protected void initializeUI() {
        page1 =
            "{COLOR=white}" +
                "The year is 2027, and things have changed.\n\n" +
//                "In this far-flung future, things are a little... different.\n\n" +
                "Long story short, Earth has been rendered mostly uninhabitable." +
                " (let's just say concerns about AI's water consumption weren't entirely overblown).\n\n" +
                "Civilization now resides entirely within a {GRADIENT=teal;grey}bubble{ENDGRADIENT}{COLOR=white} on the surface of the rocky husk that was once teeming with life. \n\n" +
                "As if that wasn't embarrassing enough, an {GRADIENT=green;grey}invading alien army{ENDGRADIENT}{COLOR=white} with absolutely zero chill " +
                "has decided to make their emotional immaturity our problem.\n\n" +
                "They're trying to burst our bubble with their bullshit, and we will not stand for it."

            ;

        page2 = "Because of some enthusiastic recent cuts to government spending however, " +
            "our only line of defense is a series of turrets. \n\n" +
            "Each turret can be plugged into one of three different firing patterns (shapes) and "  +
            "one of three {COLOR=green;}different {COLOR=magenta;}types {COLOR=white;}of {COLOR=blue;}plasma{COLOR=white;} (colors).\n\n" +
            "When a turret is receiving a complete {GRADIENT=grey;navy}signal{ENDGRADIENT}{COLOR=white} of both a plasma type and firing pattern," +
            " it's time for those alien bastards to pay!";

        page3 = "But these damn dirty extraterrestrials are crafty.\n\n" +
            "Different aliens are susceptible to some plasma types (color), and resistant to others.\n\n" +
            "With no plasma color chosen, the turrets will still fire, but their bullets will be limp and flaccid.\n\n" +
            "With the right plasma types chosen and a strategic firing pattern, the aliens will have no choice but to " +
            "kneel before you and cower as your girthy turrets spray ropey, colorful jets of freedom.";

//        if (Flag.DEBUG_RENDER.isEnabled()) {
//            var screenName = new VisLabel(getClass().getSimpleName());
//            screenName.setPosition(100, 100);
//            uiStage.addActor(screenName);
//        }

        storyText = new TypingLabel(page1, FontType.HEMI_HEAD.get());
//        storyText = new TypingLabel(page1, FontType.HEMI_HEAD.get());
        storyText.setPosition(
            Config.window_width * .06f,
            Config.window_height * .521f
        );
        storyText.setWidth(Config.window_width * .85f);
        storyText.wrap = true;
        storyText.setScale(.2f);
        storyText.getFont().adjustLineHeight(1.125f);

        pageCounter = new TypingLabel(getPageCount(), FontType.HEMI_HEAD.get());
        pageCounter.setPosition(1200, 40);
        uiStage.addActor(pageCounter);


        uiStage.addActor(storyText);
//            .expand()
//            .center();
    }
}
