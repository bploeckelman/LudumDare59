package lando.systems.ld59.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.digital.Stringf;
import com.kotcrab.vis.ui.widget.VisLabel;
import lando.systems.ld59.Flag;
import lando.systems.ld59.assets.EffectType;
import lando.systems.ld59.assets.ImageType;

import java.util.List;

public class TitleScreen extends BaseScreen {

    private static final Color BACKGROUND_COLOR = new Color(0x121212ff);

    private final TextureRegion pixel;
    private final Texture background;
    private final List<Elem> orderedElements;

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

    private float animTime = 0;
    private boolean drawUI = true;

    public TitleScreen() {
        this.pixel = assets.pixelRegion;
        this.background = ImageType.BACKGROUND_TITLE.get();
        this.orderedElements = List.of(
                  Elem.PLANET
                , Elem.TURRET_1, Elem.TURRET_2, Elem.TURRET_3
                , Elem.CITY
                , Elem.SHIPS // animation seems a little weird for this since new ones just pop in between frames
                , Elem.BUTTON_BOARD_LEFT, Elem.BUTTON_BOARD_RIGHT
                , Elem.BUTTONS_SET_LEFT, Elem.BUTTONS_SET_RIGHT
                , Elem.TEXT_PLUG_N_PLAY_DROPSHADOW
                , Elem.TEXT_PLUG_N_PLAY
                , Elem.TEXT_MIXED_SIGNALS
        );

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
        if (!transitioning && Gdx.input.justTouched()){
            transitioning = true;
//            AudioEvent.stopAllMusic();
            game.setScreen(new IntroScreen(), EffectType.DREAMY);
        }

        uiRoot.setVisible(drawUI);

        for (var elem : Elem.values()) {
            elem.stateTime += delta;
        }

        animTime += delta;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(BACKGROUND_COLOR);

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            batch.draw(background, 0, 0);

            for (var elem : orderedElements) {
                var keyframe = elem.animation.getKeyFrame(elem.stateTime);
                batch.draw(keyframe, 0, 0);
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
    }
}
