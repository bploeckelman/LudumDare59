package lando.systems.ld59.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.VisLabel;
import lando.systems.ld59.Flag;
import lando.systems.ld59.assets.FontType;
import lando.systems.ld59.assets.SkinType;

public class LaunchScreen extends BaseScreen {

    public LaunchScreen() {
        initializeUI();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (!transitioning && Gdx.input.justTouched()){
            transitioning = true;
            game.setScreen(new TitleScreen());
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            // ...
        }
        batch.end();

        uiStage.draw();
    }

    @Override
    public void initializeUI() {
        if (Flag.DEBUG_RENDER.isEnabled()) {
            var screenName = new VisLabel(getClass().getSimpleName());
            screenName.setPosition(10, windowCamera.viewportHeight - 10 - screenName.getHeight());
            uiStage.addActor(screenName);
        }

        var clickToBegin = new TypingLabel(assets.strings.get("launch.click-text"), FontType.ROUNDABOUT_LARGE.get());
        uiRoot.add(clickToBegin).expand().center();
    }
}
