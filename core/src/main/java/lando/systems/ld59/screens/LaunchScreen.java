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
            var screenName = LaunchScreen.class.getSimpleName();
            uiRoot.add(new VisLabel(screenName)).pad(10).expandX().left().row();
        }

        var skin = SkinType.ZENDO.get();
        var styleName = FontType.ATKINSON_HYPERLEGIBLE.textraLabelStyleName;
        var text = "{WAVE}{RAINBOW}Click to Begin{ENDRAINBOW}{ENDWAVE}";
        uiRoot.add(new TypingLabel(text, skin, styleName)).expand().center();
    }
}
