package lando.systems.ld59.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.digital.Stringf;
import com.kotcrab.vis.ui.widget.VisLabel;
import lando.systems.ld59.Flag;
import lando.systems.ld59.assets.EffectType;
import lando.systems.ld59.utils.Calc;
import lando.systems.ld59.utils.FramePool;

public class EndingScreen extends BaseScreen {

    // TEMPORARY -----------------------------------------
    private float countdownDurationSecs = 3f;
    private float countdownTimer = countdownDurationSecs;
    private VisLabel countdownLabel;
    // TEMPORARY -----------------------------------------

    public EndingScreen() {
        this.countdownLabel = new VisLabel(Stringf.format("%.1f", countdownTimer));

        initializeUI();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        countdownLabel.setText(Stringf.format("%.1f", countdownTimer));
        countdownTimer = Calc.clampf(countdownTimer - delta, 0, countdownDurationSecs);

        if (!transitioning && countdownTimer <= 0){
            transitioning = true;
            game.setScreen(new CreditsScreen(), EffectType.DREAMY);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1f, 0f, 0f, 1f);

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            // ...
        }
        batch.end();
        batch.setShader(null);

        uiStage.draw();
    }

    @Override
    protected void initializeUI() {
        if (Flag.DEBUG_RENDER.isEnabled()) {
            var screenName = EndingScreen.class.getSimpleName();
            uiRoot.add(new VisLabel(screenName)).pad(10).top().left().row();
            // TODO: uncomment when removing countdownLabel, unless there is already other ui stuff
            // uiRoot.add(new VisLabel()).grow();
        }

        // TEMP: remove when we have story stuff in this screen
        uiRoot.add(countdownLabel).expand().center();
    }
}
