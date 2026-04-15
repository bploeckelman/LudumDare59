package lando.systems.ld59.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld59.utils.FramePool;

public class EndingScreen extends BaseScreen {

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1f, 0f, 0f, 1f);

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
//        batch.draw(screenTexture, 0, screenTexture.getHeight(), screenTexture.getWidth(), -screenTexture.getHeight());
        batch.end();
        batch.setShader(null);

        batch.enableBlending();
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            var pos = FramePool.vec2(
                    (windowCamera.viewportWidth - layout.getWidth()) / 2f,
                    windowCamera.viewportHeight - layout.getHeight());
            font.drawGlyphs(batch, layout, pos.x, pos.y);
        }
        batch.end();
        batch.setShader(null);
        batch.setColor(Color.WHITE);
    }
}
