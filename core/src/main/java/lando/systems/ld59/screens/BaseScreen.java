package lando.systems.ld59.screens;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.Layout;
import com.kotcrab.vis.ui.widget.VisTable;
import lando.systems.ld59.Config;
import lando.systems.ld59.Flag;
import lando.systems.ld59.Main;
import lando.systems.ld59.assets.Assets;
import lando.systems.ld59.assets.FontType;
import lando.systems.ld59.game.scenes.Scene;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class BaseScreen implements Screen {

    public final Main game;
    public final Assets assets;
    public final SpriteBatch batch;
    public final ShapeDrawer shapes;
    public final TweenManager tween;
    public final OrthographicCamera windowCamera;
    public final Engine engine;
    public final Stage uiStage;

    public OrthographicCamera worldCamera;
    public VisTable uiRoot;
    public Layout layout;
    public Font font;
    public Scene<? extends BaseScreen> scene;
    public boolean transitioning = false;

    public BaseScreen() {
        this.game = Main.game;
        this.assets = game.assets;
        this.batch = game.assets.batch;
        this.shapes = game.assets.shapes;
        this.tween = game.tween;
        this.windowCamera = game.windowCamera;
        this.engine = game.engine;

        this.uiRoot = new VisTable();
        this.uiRoot.setFillParent(true);
        var viewport = new ScreenViewport(windowCamera);
        this.uiStage = new Stage(viewport);
        uiStage.addActor(uiRoot);

        this.worldCamera = new OrthographicCamera();
        worldCamera.setToOrtho(false, Config.framebuffer_width, Config.framebuffer_height);
        worldCamera.update();

        this.font = FontType.ROUNDABOUT.get();
        this.layout = new Layout(font);
        layout.setTargetWidth(windowCamera.viewportWidth);
        font.markup(getClass().getSimpleName(), layout);
        font.regenerateLayout(layout);

        // NOTE: if a screen needs to adjust input processors (rearrange, add more, etc...) use this pattern:
        game.inputMux.setProcessors(uiStage);
        Gdx.input.setInputProcessor(game.inputMux);
    }

    public Scene<? extends BaseScreen> scene() {
        return scene;
    }

    /** Called when this screen becomes the current screen for a {@link Game}. */
    @Override
    public void show() {}

    /** Called when this screen is no longer the current screen for a {@link Game}. */
    @Override
    public void hide() {}

    /** @see ApplicationListener#resize(int, int) */
    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
        windowCamera.setToOrtho(false, width, height);

        uiStage.getViewport().update(width, height, true);
    }

    /** @see ApplicationListener#pause() */
    @Override
    public void pause() {}

    /** @see ApplicationListener#resume() */
    @Override
    public void resume() {}

    /** Called when the screen should render itself.
     * @param delta The time in seconds since the last render. */
    public abstract void render(float delta);

    /** Called when this screen should release all resources. */
    @Override
    public void dispose() {
        uiStage.dispose();
    }

    public void alwaysUpdate(float delta) {}

    public void update(float delta) {
        windowCamera.update();
        if (worldCamera != null) {
            worldCamera.update();
        }
        // TODO: add a global input processor to toggle flags at runtime
        uiRoot.setDebug(Flag.DEBUG_UI.isEnabled());
        uiStage.act(Math.min(delta, 1 / 30f));
    }

    public void renderOffscreenBuffers(SpriteBatch batch) {}

    protected void initializeUI() {}
}
