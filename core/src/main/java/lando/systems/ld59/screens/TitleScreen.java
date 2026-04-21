package lando.systems.ld59.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;import aurelienribon.tweenengine.equations.Quint;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.digital.Stringf;
import com.github.tommyettinger.textra.TypingButton;import com.kotcrab.vis.ui.VisUI;import com.kotcrab.vis.ui.widget.VisLabel;
import lando.systems.ld59.Flag;
import lando.systems.ld59.assets.EffectType;
import lando.systems.ld59.assets.FontType;import lando.systems.ld59.assets.ImageType;
import lando.systems.ld59.utils.accessors.ColorAccessor;import lando.systems.ld59.utils.accessors.Vector2Accessor;

import java.util.List;

public class TitleScreen extends BaseScreen {

    private static final Color BACKGROUND_COLOR = new Color(0x121212ff);

    private final TextureRegion pixel;
    private final Texture background;
    private final List<Elem> orderedElements;

    private TypingButton nextScreenButton;
    private boolean connectionsCreated = false;
    private boolean drawUI = true;

    // @formatter:off
    private enum Elem {
          BUTTON_BOARD_LEFT           (1, "buttons/button-board-left")
        , BUTTON_BOARD_RIGHT          (1, "buttons/button-board-right")
        , BUTTONS_SET_LEFT            (1, "buttons/button-set-left")
        , BUTTONS_SET_RIGHT           (1, "buttons/button-set-right")
        , CITY                        (6, "city/city")
        , PLANET                      (8, "planet/planet-idle", 0.5f, Animation.PlayMode.LOOP_PINGPONG)
        , SHIPS                       (4, "ships/ships-arrange-1", 0.5f, Animation.PlayMode.LOOP_PINGPONG)
        , TEXT_MIXED_SIGNALS          (8, "text-mixed-signals/text-mixed-signals", 0.1f, Animation.PlayMode.LOOP_PINGPONG)
        , TEXT_PLUG_N_PLAY_DROPSHADOW (1, "text-plug-n-play/text-plug-n-play-dropshadow")
        , TEXT_PLUG_N_PLAY            (1, "text-plug-n-play/text-plug-n-play")
        , TURRET_1                    (4, "turret-1/turret-1", 0.2f, Animation.PlayMode.LOOP_PINGPONG)
        , TURRET_2                    (4, "turret-2/turret-2", 0.2f, Animation.PlayMode.LOOP_PINGPONG)
        , TURRET_3                    (4, "turret-3/turret-3", 0.2f, Animation.PlayMode.LOOP_PINGPONG)
        ;

        final int count;
        final Vector2 position;
        final Color color;
        final String baseFileName;
        final Array<Texture> textures;
        final Animation<Texture> animation;

        float stateTime;

        Elem(int count, String baseFileName) {
            this(count, baseFileName, 0.1f, Animation.PlayMode.LOOP);
        }

        Elem(int count, String baseFileName, float frameDuration, Animation.PlayMode playMode) {
            var prefix = "images/title/";

            this.count = count;
            this.position = new Vector2(0, 0); // Everything is 0,0 by default, setup tweens in screen ctor for moving stuff
            this.color = new Color(1f, 1f, 1f, 1f);
            this.baseFileName = baseFileName;
            this.textures = new Array<>(count);
            for (int i = 0; i < count; i++) {
                var suffix = Stringf.format("_%02d.png", i);
                var texture = new Texture(prefix + baseFileName + suffix);
                textures.add(texture);
            }
            this.animation = new Animation<>(frameDuration, textures, playMode);
            this.stateTime = 0f;
        }
    }
    // @formatter:on

    public TitleScreen() {
        this.pixel = assets.pixelRegion;
        this.background = ImageType.BACKGROUND_TITLE.get();
        this.orderedElements = List.of(
                  Elem.PLANET
                , Elem.TURRET_1, Elem.TURRET_2, Elem.TURRET_3
                , Elem.CITY
//                , Elem.SHIPS // animation seems a little weird for this since new ones just pop in between frames
                , Elem.BUTTON_BOARD_LEFT, Elem.BUTTON_BOARD_RIGHT
                , Elem.BUTTONS_SET_LEFT, Elem.BUTTONS_SET_RIGHT
                , Elem.TEXT_PLUG_N_PLAY_DROPSHADOW
                , Elem.TEXT_PLUG_N_PLAY
                , Elem.TEXT_MIXED_SIGNALS
        );
        initializeTweens();
        initializeUI();
    }

    // NOTE: called for TitleScreen only in Transition.update() when done with transition
    @Override
    public void dispose() {
        for (var elem : Elem.values()) {
            for (int i = 0; i < elem.textures.size; i++) {
                var texture = elem.textures.get(i);
                texture.dispose();
            }
            elem.textures.clear();
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
//        if (!transitioning && Gdx.input.justTouched()){
//            transitioning = true;
////            AudioEvent.stopAllMusic();
//            game.setScreen(new IntroScreen(), EffectType.DREAMY);
//        }

        uiRoot.setVisible(drawUI);

        for (var elem : Elem.values()) {
            elem.stateTime += delta;
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(BACKGROUND_COLOR);

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            batch.draw(background, 0, 0);

            for (var elem : orderedElements) {
                var pos = elem.position;
                var col = elem.color;
                var anim = elem.animation;
                var keyframe = anim.getKeyFrame(elem.stateTime);

                batch.setColor(col);
                batch.draw(keyframe, pos.x, pos.y);
                batch.setColor(1, 1, 1, 1);
            }

            // Draw black overlap that fades off
//            batch.setColor(0, 0, 0, pixelOverlayAlpha.floatValue());
//            batch.draw(pixel, 0, 0, winWidth, winHeight);
//            batch.setColor(Color.WHITE);
        }
        batch.end();

        uiStage.draw();
    }

    @Override
    protected void initializeUI() {
        if (Flag.DEBUG_RENDER.isEnabled()) {
            var screenName = new VisLabel(getClass().getSimpleName());
            screenName.setPosition(10, windowCamera.viewportHeight - 10 - screenName.getHeight());
            uiStage.addActor(screenName);
        }

        nextScreenButton = new TypingButton("{RAINBOW}Start Game{ENDRAINBOW}", VisUI.getSkin(), FontType.ROUNDABOUT_LARGE.get());
        nextScreenButton.setPosition(750f, 300f);
        nextScreenButton.setSize(250f, 50f);
        nextScreenButton.setVisible(false);
        nextScreenButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!transitioning) {
                    transitioning = true;
                    game.setScreen(new IntroScreen(), EffectType.DREAMY);
                }
            }
        });

        // force a white font color in the relevant states (default is black)
        var style = nextScreenButton.getStyle();
        style.fontColor.set(1, 1, 1, 1);
        style.downFontColor.set(1, 1, 1, 1);
        style.overFontColor.set(1, 1, 1, 1);
        // fudge the string positioning a bit for better vertical centering
        style.up.setTopHeight(40);
        style.down.setTopHeight(40);
        style.over.setTopHeight(20);

        uiStage.addActor(nextScreenButton);

        // not laying text out correctly before first hover, force a layout like this...
        nextScreenButton.invalidateHierarchy();
        nextScreenButton.layout();
    }

    private void initializeTweens() {
        var width = windowCamera.viewportWidth;
        var height = windowCamera.viewportHeight;

        // Start 'plug n play', buttons offscreen in order to slide on
        Elem.TEXT_PLUG_N_PLAY.position.set(0, height);
        Elem.TEXT_PLUG_N_PLAY_DROPSHADOW.position.set(0, height);
        Elem.BUTTON_BOARD_LEFT.position.set(-width, 0);
        Elem.BUTTONS_SET_LEFT.position.set(-width, 0);
        Elem.BUTTON_BOARD_RIGHT.position.set(width, 0);
        Elem.BUTTONS_SET_RIGHT.position.set(width, 0);

        // Start 'mixed signals' text transparent
        Elem.TEXT_MIXED_SIGNALS.color.a = 0;

        // @formatter:off
        Timeline.createSequence()
                // Slide in 'plug n play' text from above
                .push(Timeline.createParallel()
                        .push(Tween.to(Elem.TEXT_PLUG_N_PLAY.position,            Vector2Accessor.Y, 2f).target(0).ease(Bounce.OUT))
                        .push(Tween.to(Elem.TEXT_PLUG_N_PLAY_DROPSHADOW.position, Vector2Accessor.Y, 2f).target(0).ease(Bounce.OUT)))
                .pushPause(0.2f)
                // Slide in buttons from sides
                .push(Timeline.createParallel()
                        .push(Tween.to(Elem.BUTTON_BOARD_LEFT.position,  Vector2Accessor.X, 1f).target(0).ease(Quint.OUT))
                        .push(Tween.to(Elem.BUTTON_BOARD_RIGHT.position, Vector2Accessor.X, 1f).target(0).ease(Quint.OUT))
                        .push(Tween.to(Elem.BUTTONS_SET_LEFT.position,   Vector2Accessor.X, 1f).target(0).ease(Quint.OUT))
                        .push(Tween.to(Elem.BUTTONS_SET_RIGHT.position,  Vector2Accessor.X, 1f).target(0).ease(Quint.OUT)))
                .pushPause(0.2f)
                // Trigger creation of jiggly cables
                .push(Tween.call((type, source) -> createConnections()))
                .pushPause(0.3f)
                // Fade in 'mixed signals' text and enable play button at the same time
                .push(Timeline.createParallel()
                        .push(Tween.to(Elem.TEXT_MIXED_SIGNALS.color, ColorAccessor.A, 2f).target(1f).ease(Circ.OUT))
                        .push(Tween.call((type, source) -> nextScreenButton.setVisible(true))))
                .start(tween);
        // @formatter:on
    }

    private void createConnections() {
        // TODO: ... need ECS Scene, maybe? can we just do it live? ...
        connectionsCreated = true;
    }
}
