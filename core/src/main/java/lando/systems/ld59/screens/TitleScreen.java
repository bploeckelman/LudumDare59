package lando.systems.ld59.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Quint;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.digital.Stringf;
import com.github.tommyettinger.textra.TypingButton;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import lando.systems.ld59.Flag;
import lando.systems.ld59.assets.EffectType;
import lando.systems.ld59.assets.FontType;
import lando.systems.ld59.assets.ImageType;
import lando.systems.ld59.assets.ShaderType;
import lando.systems.ld59.game.components.EnergyColor;
import lando.systems.ld59.game.components.renderable.CableShaderRenderable;
import lando.systems.ld59.utils.RopePath;
import lando.systems.ld59.utils.Util;
import lando.systems.ld59.utils.accessors.ColorAccessor;
import lando.systems.ld59.utils.accessors.Vector2Accessor;

import java.util.ArrayList;
import java.util.List;

public class TitleScreen extends BaseScreen {

    private static final Color BACKGROUND_COLOR = new Color(0x121212ff);

    private final TextureRegion pixel;
    private final Texture background;
    private final List<Elem> underCableElements;
    private final List<Elem> overCableElements;
    private final List<CableShaderRenderable> cables;

    private CableShaderRenderable cableRed;
    private CableShaderRenderable cableGreen;
    private CableShaderRenderable cableBlue;
    private CableShaderRenderable cableCircle;
    private CableShaderRenderable cableSquare;
    private CableShaderRenderable cableTriangle;

    private TypingButton nextScreenButton;
    private boolean connectionsCreated = false;
    private float accum = 0f;
    private float jostleTimer = 2f;

    // @formatter:off
    private enum Elem {
          BUTTON_BOARD_LEFT           (1, "buttons/button-board-left")
        , BUTTON_BOARD_RIGHT          (1, "buttons/button-board-right")
        , BUTTONS_SET_LEFT            (1, "buttons/button-set-left")
        , BUTTONS_SET_RIGHT           (1, "buttons/button-set-right")
        , CITY                        (6, "city/city")
        , PLANET                      (8, "planet/planet-idle", 0.5f, Animation.PlayMode.LOOP_PINGPONG)
        , SHIPS                       (1, "ships/ships-arrange-1", 0.5f, Animation.PlayMode.LOOP_PINGPONG)
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
        this.underCableElements = List.of(
                  Elem.PLANET
                , Elem.TURRET_1, Elem.TURRET_2, Elem.TURRET_3
                , Elem.CITY
                , Elem.SHIPS // animation seems a little weird for this since new ones just pop in between frames
        );
        this.overCableElements = List.of(
                  Elem.BUTTON_BOARD_LEFT, Elem.BUTTON_BOARD_RIGHT
                , Elem.BUTTONS_SET_LEFT, Elem.BUTTONS_SET_RIGHT
                , Elem.TEXT_PLUG_N_PLAY_DROPSHADOW
                , Elem.TEXT_PLUG_N_PLAY
                , Elem.TEXT_MIXED_SIGNALS
        );
        this.cables = new ArrayList<>();
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
        accum += delta;

        if (connectionsCreated) {
            if (jostleTimer <= 0f) {
                jostleTimer = MathUtils.random(0.5f, 2f);
                for (var cable : cables) {
                    cable.path.jostle(MathUtils.random(10f, 20f));
                }
            }
            jostleTimer -= delta;

            for (var cable : cables) {
                cable.path.update(delta);
            }
        }

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

            for (var elem : underCableElements) {
                var pos = elem.position;
                var col = elem.color;
                var anim = elem.animation;
                var keyframe = anim.getKeyFrame(elem.stateTime);

                batch.setColor(col);
                batch.draw(keyframe, pos.x, pos.y);
                batch.setColor(1, 1, 1, 1);
            }
        }
        batch.end();

        if (connectionsCreated) {
            var shader = ShaderType.CABLE.get();
            shader.bind();
            shader.setUniformMatrix("u_projTrans", windowCamera.combined);
            shader.setUniformf("u_time", accum);
            shader.setUniformi("u_texture", 0);
            shader.setUniformf("u_edgeColor", Color.BLACK);
            ImageType.NOISE.get().bind(0);
            for (var cable : cables) {
                cable.render();
            }
        }

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            for (var elem : overCableElements) {
                var pos = elem.position;
                var col = elem.color;
                var anim = elem.animation;
                var keyframe = anim.getKeyFrame(elem.stateTime);

                batch.setColor(col);
                batch.draw(keyframe, pos.x, pos.y);
                batch.setColor(1, 1, 1, 1);
            }
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
        var isTitleScreen = true;

        var xx = windowCamera.viewportWidth - 45;
        var pointsRed      = Util.generateStraightPath(new Vector2(45, 520), new Vector2(375, 310)); pointsRed     .add(new Vector2(375, 310));
        var pointsGreen    = Util.generateStraightPath(new Vector2(45, 600), new Vector2(590, 235)); pointsGreen   .add(new Vector2(590, 235));
        var pointsBlue     = Util.generateStraightPath(new Vector2(45, 678), new Vector2(775, 120)); pointsBlue    .add(new Vector2(775, 120));
        var pointsCircle   = Util.generateStraightPath(new Vector2(xx,  80), new Vector2(375, 315)); pointsCircle  .add(new Vector2(375, 315));
        var pointsSquare   = Util.generateStraightPath(new Vector2(xx, 160), new Vector2(590, 235)); pointsSquare  .add(new Vector2(590, 235));
        var pointsTriangle = Util.generateStraightPath(new Vector2(xx, 240), new Vector2(770, 120)); pointsTriangle.add(new Vector2(775, 120));

        var pathRed      = new RopePath(isTitleScreen, pointsRed);
        var pathGreen    = new RopePath(isTitleScreen, pointsGreen);
        var pathBlue     = new RopePath(isTitleScreen, pointsBlue);
        var pathCircle   = new RopePath(isTitleScreen, pointsCircle);
        var pathSquare   = new RopePath(isTitleScreen, pointsSquare);
        var pathTriangle = new RopePath(isTitleScreen, pointsTriangle);

        cableRed      = new CableShaderRenderable(isTitleScreen, EnergyColor.COLOR_RED,   null, pathRed);
        cableGreen    = new CableShaderRenderable(isTitleScreen, EnergyColor.COLOR_GREEN, null, pathGreen);
        cableBlue     = new CableShaderRenderable(isTitleScreen, EnergyColor.COLOR_BLUE,  null, pathBlue);
        cableCircle   = new CableShaderRenderable(isTitleScreen, EnergyColor.COLOR_RED,   null, pathCircle);
        cableSquare   = new CableShaderRenderable(isTitleScreen, EnergyColor.COLOR_GREEN, null, pathSquare);
        cableTriangle = new CableShaderRenderable(isTitleScreen, EnergyColor.COLOR_BLUE,  null, pathTriangle);

        cables.add(cableRed);
        cables.add(cableGreen);
        cables.add(cableBlue);
        cables.add(cableCircle);
        cables.add(cableSquare);
        cables.add(cableTriangle);

        connectionsCreated = true;
    }
}
