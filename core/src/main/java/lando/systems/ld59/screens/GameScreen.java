package lando.systems.ld59.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import lando.systems.ld59.Config;
import lando.systems.ld59.Flag;
import lando.systems.ld59.assets.EffectType;
import lando.systems.ld59.game.Systems;
import lando.systems.ld59.game.scenes.SceneGame;

public class GameScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x333333ff);

    public GameScreen() {
        this.scene = new SceneGame(this);

        initializeUI();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        var TEMP_CLICK_TO_TRANSITION = Gdx.input.justTouched();
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
            screenName.setPosition(10, windowCamera.viewportHeight - 10 - screenName.getHeight());
            uiStage.addActor(screenName);
        }
    }
}
