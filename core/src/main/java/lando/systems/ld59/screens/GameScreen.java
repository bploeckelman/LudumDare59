package lando.systems.ld59.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import lando.systems.ld59.Config;
import lando.systems.ld59.Flag;
import lando.systems.ld59.assets.EffectType;
import lando.systems.ld59.assets.MusicType;
import lando.systems.ld59.assets.anims.AnimBaseCity;
import lando.systems.ld59.assets.anims.AnimMisc;
import lando.systems.ld59.game.Systems;
import lando.systems.ld59.game.scenes.Scene;
import lando.systems.ld59.game.scenes.SceneGame;
import lando.systems.ld59.game.signals.AudioEvent;
import lando.systems.ld59.ui.SettingsUI;

public class GameScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x333333ff);

    private final SceneGame scene;
    private final SettingsUI settingsUI = new SettingsUI();

    public GameScreen() {
        this.scene = new SceneGame(this, 5);
        uiStage.addActor(settingsUI);
        initializeUI();

        game.inputMux.setProcessors(scene, uiStage);
        Gdx.input.setInputProcessor(game.inputMux);
        AudioEvent.playMusic(MusicType.MAIN_MUSIC);
    }

    @Override
    public Scene<? extends BaseScreen> scene() {
        return scene;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (settingsUI.isVisible()) {
            return;
        }
        var TEMP_CLICK_TO_TRANSITION = Gdx.input.justTouched()
                && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                ||  Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
        if (!transitioning && TEMP_CLICK_TO_TRANSITION){
            transitioning = true;
            game.setScreen(new EndingScreen(), EffectType.DREAMY);
        }

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
        ScreenUtils.clear(backgroundColor);

        // Draw scene
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            Systems.render.draw(batch);
            Systems.renderDebug.draw(shapes);
            Systems.renderDebug.drawText(batch);
        }
        batch.end();

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
             Systems.render.drawInWindowSpace(batch, windowCamera);
        }
        batch.end();

        uiStage.draw();
    }

    @Override
    protected void initializeUI() {
        if (Flag.DEBUG_RENDER.isEnabled()) {
            var screenName = new VisLabel(getClass().getSimpleName());
            screenName.setPosition(10f, windowCamera.viewportHeight - 10f - screenName.getHeight());
            uiStage.addActor(screenName);
        }

        var gearRegion = AnimMisc.GEAR.get().getKeyFrame(0f);
        var gearDrawable = new TextureRegionDrawable(gearRegion);
        var settingsButton = new VisImageButton(gearDrawable, "Settings");
        settingsButton.setSize(50, 50);
        settingsButton.setPosition(
                windowCamera.viewportWidth  - 10f - settingsButton.getWidth(),
                windowCamera.viewportHeight - 10f - settingsButton.getHeight());
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settingsUI.toggle();
            }
        });

        var cityRegion = AnimBaseCity.IDLE.get().getKeyFrame(0f);
        var cityDrawable = new TextureRegionDrawable(cityRegion);
        var cityTestButton = new VisImageButton(cityDrawable, "City Anim Test");
        cityTestButton.setSize(50, 50);
        cityTestButton.setPosition(
                windowCamera.viewportWidth  - 10f - settingsButton.getWidth() - 10f - cityTestButton.getWidth(),
                windowCamera.viewportHeight - 10f - cityTestButton.getHeight());
        cityTestButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                scene.cityAnimTest();
            }
        });

        uiStage.addActor(settingsButton);
        uiStage.addActor(cityTestButton);
    }
}
