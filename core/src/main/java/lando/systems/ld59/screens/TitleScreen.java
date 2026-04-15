package lando.systems.ld59.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld59.assets.EffectType;

public class TitleScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x121212ff);
    private final TextureRegion pixel;

    private float animTime = 0;
    private boolean drawUI = true;

    public TitleScreen() {
        var atlas = assets.atlas;

        this.pixel = assets.pixelRegion;

        initializeUI();
    }

    @Override
    public void initializeUI() {
        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (!transitioning && Gdx.input.justTouched()){
            transitioning = true;
//            AudioEvent.stopAllMusic();
            game.setScreen(new IntroScreen(), EffectType.DREAMY);
        }

        animTime += delta;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(backgroundColor);

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            // Draw black overlap that fades off
//            batch.setColor(0, 0, 0, pixelOverlayAlpha.floatValue());
//            batch.draw(pixel, 0, 0, winWidth, winHeight);
//            batch.setColor(Color.WHITE);
        }
        batch.end();

        if (drawUI) {
            uiStage.draw();
        }
    }
}
