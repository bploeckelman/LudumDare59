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
import com.badlogic.ashley.core.Family;
import lando.systems.ld59.Config;
import lando.systems.ld59.Flag;
import lando.systems.ld59.assets.EffectType;
import lando.systems.ld59.assets.MusicType;
import lando.systems.ld59.assets.anims.AnimBaseCity;
import lando.systems.ld59.assets.anims.AnimBaseTurret;
import lando.systems.ld59.assets.anims.AnimMisc;
import lando.systems.ld59.game.Systems;
import lando.systems.ld59.game.components.EnemyTag;
import lando.systems.ld59.game.components.EnergyColor;
import lando.systems.ld59.game.scenes.Scene;
import lando.systems.ld59.game.scenes.SceneGame;
import lando.systems.ld59.game.signals.AudioEvent;
import lando.systems.ld59.ui.SettingsUI;
import lando.systems.ld59.utils.FramePool;

public class GameScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x333333ff);

    private final SceneGame scene;
    private final SettingsUI settingsUI = new SettingsUI();

    private VisLabel redEnemyCountLabel;
    private VisLabel greenEnemyCountLabel;
    private VisLabel blueEnemyCountLabel;

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
        updateEnemyCounts();
        engine.update(delta);
    }

    private void updateEnemyCounts() {
        var enemies = engine.getEntitiesFor(Family.all(EnemyTag.class, EnergyColor.class).get());

        int redCount = 0;
        int greenCount = 0;
        int blueCount = 0;

        for (var entity : enemies) {
            var energyColor = entity.getComponent(EnergyColor.class);
            if (energyColor != null) {
                switch (energyColor.type) {
                    case RED:
                        redCount++;
                        break;
                    case GREEN:
                        greenCount++;
                        break;
                    case BLUE:
                        blueCount++;
                        break;
                }
            }
        }

        redEnemyCountLabel.setText("Red: " + redCount);
        greenEnemyCountLabel.setText("Green: " + greenCount);
        blueEnemyCountLabel.setText("Blue: " + blueCount);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(backgroundColor);

        // Draw scene
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            Systems.render.draw(batch, worldCamera);
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

        // Add enemy count labels
        redEnemyCountLabel = new VisLabel("Red: 0");
        redEnemyCountLabel.setColor(EnergyColor.RED);
        greenEnemyCountLabel = new VisLabel("Green: 0");
        greenEnemyCountLabel.setColor(EnergyColor.GREEN);
        blueEnemyCountLabel = new VisLabel("Blue: 0");
        blueEnemyCountLabel.setColor(EnergyColor.BLUE);

        var margin = 10f;
        var labelStartX = margin;
        var labelY = windowCamera.viewportHeight - margin - 20f;

        redEnemyCountLabel.setPosition(labelStartX, labelY);
        greenEnemyCountLabel.setPosition(labelStartX + 90f, labelY);
        blueEnemyCountLabel.setPosition(labelStartX + 200f, labelY);

        uiStage.addActor(redEnemyCountLabel);
        uiStage.addActor(greenEnemyCountLabel);
        uiStage.addActor(blueEnemyCountLabel);

        var buttonSize  = 50f;
        var buttonPosY  = windowCamera.viewportHeight - margin - buttonSize;
        var settingsPos = FramePool.vec2(windowCamera.viewportWidth - margin - buttonSize, buttonPosY);
        var cityPos     = FramePool.vec2(settingsPos.x - margin - buttonSize, buttonPosY);
        var turretPos   = FramePool.vec2(cityPos.x - margin - buttonSize, buttonPosY);
        var springPos   = FramePool.vec2(turretPos.x - margin - buttonSize, buttonPosY);

        var gearRegion = AnimMisc.GEAR.get().getKeyFrame(0f);
        var gearDrawable = new TextureRegionDrawable(gearRegion);
        var settingsButton = new VisImageButton(gearDrawable, "Settings");
        settingsButton.setSize(buttonSize, buttonSize);
        settingsButton.setPosition(settingsPos.x, settingsPos.y);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settingsUI.toggle();
            }
        });

        var cityRegion = AnimBaseCity.IDLE.get().getKeyFrame(0f);
        var cityDrawable = new TextureRegionDrawable(cityRegion);
        var cityTestButton = new VisImageButton(cityDrawable, "City Anim Test");
        cityTestButton.setSize(buttonSize, buttonSize);
        cityTestButton.setPosition(cityPos.x, cityPos.y);
        cityTestButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                scene.cityAnimTest();
            }
        });

        var turretRegion = AnimBaseTurret.BARREL_ICON.get().getKeyFrame(0f);
        var turretDrawable = new TextureRegionDrawable(turretRegion);
        var turretTestButton = new VisImageButton(turretDrawable, "Turret Barrel Anim Test");
        turretTestButton.setSize(buttonSize, buttonSize);
        turretTestButton.setPosition(turretPos.x, turretPos.y);
        turretTestButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                scene.turretAnimTest();
            }
        });

        var springRegion = AnimMisc.SPRING.get().getKeyFrame(0f);
        var springDrawable = new TextureRegionDrawable(springRegion);
        var springTestButton = new VisImageButton(springDrawable, "Spring Connection Test");
        springTestButton.setSize(buttonSize, buttonSize);
        springTestButton.setPosition(springPos.x, springPos.y);
        springTestButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                scene.springConnectionTest();
            }
        });

        uiStage.addActor(settingsButton);
        uiStage.addActor(cityTestButton);
        uiStage.addActor(turretTestButton);
        uiStage.addActor(springTestButton);
    }
}
