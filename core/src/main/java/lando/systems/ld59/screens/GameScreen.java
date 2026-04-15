package lando.systems.ld59.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld59.Config;
import lando.systems.ld59.Flag;

public class GameScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x333333ff);

    @Override
    public void update(float delta) {
        super.update(delta);

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
//        Systems.render.draw(batch);
//        Systems.renderDebug.draw(shapes);
        batch.end();

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
//        Systems.render.drawInWindowSpace(batch, windowCamera);
        batch.end();
    }
}
