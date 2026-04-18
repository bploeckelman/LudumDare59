package lando.systems.ld59.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.layout.FlowGroup;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import lando.systems.ld59.Flag;
import lando.systems.ld59.assets.FontType;

public class CreditsScreen extends BaseScreen {

    private static final Color BACKGROUND_COLOR = new Color(.0f, .0f, .2f, 1f);

    // TODO: in i18n strings...
    //  how to mix formatting placeholders: {0}, {1}, ..., with typing label placeholders like {GRADIENT},
    //  I think they don't play nice because they use the same delimiter `{}`
    //  usage: assets.strings.format("property.name", placeholderValue)

    // TODO: add pet sprites to Body

    // TODO: get the Header, Body, Footer row sections to fill the screen properly

    private class Header extends VisTable {
        private Header() {
            //setDebug(true);
            padTop(10);
            padBottom(10);
            //setHeight(32 + 24 + 50); // font large + font default + padding

            var title = new TypingLabel(assets.strings.get("credits.header.title"), FontType.ROUNDABOUT_LARGE.get());
            var theme = new TypingLabel(assets.strings.get("credits.header.theme"), FontType.ROUNDABOUT.get());

            add(title).expand().padTop(20).padBottom(20).row();
            add(theme).expandX();
        }
    }

    private class Body extends VisTable {
        private Body() {
            //setDebug(true);
            padTop(10);
            padBottom(10);

            var rowSpacing = 10f;
            var vertical = true;
            var panelLeft = new FlowGroup(vertical, rowSpacing);
            var panelRight = new FlowGroup(vertical, rowSpacing);

            // TODO: set a background texture on the panels (might have to extend and customize)

            // TODO: maybe don't use FlowGroups, harder to align the text than just straight table cells

            panelLeft.addActor(new TypingLabel(assets.strings.get("credits.body.left.code-heading"), FontType.ROUNDABOUT.get()));
            panelLeft.addActor(new TypingLabel(assets.strings.get("credits.body.left.code-names"), FontType.ROBOTO.get()));
            panelLeft.addActor(new VisLabel()); // spacer
            panelLeft.addActor(new VisLabel()); // spacer
            panelLeft.addActor(new TypingLabel(assets.strings.get("credits.body.left.pets-heading"), FontType.ROUNDABOUT.get()));
            panelLeft.addActor(new TypingLabel(assets.strings.get("credits.body.left.pets-names"), FontType.COUSINE.get()));

            panelRight.addActor(new TypingLabel(assets.strings.get("credits.body.right.art-heading"), FontType.ROUNDABOUT.get()));
            panelRight.addActor(new TypingLabel(assets.strings.get("credits.body.right.art-names"), FontType.ROBOTO.get()));
            panelRight.addActor(new VisLabel()); // spacer
            panelRight.addActor(new VisLabel()); // spacer
            panelRight.addActor(new TypingLabel(assets.strings.get("credits.body.right.audio-heading"), FontType.ROUNDABOUT.get()));
            panelRight.addActor(new TypingLabel(assets.strings.get("credits.body.right.audio-names"), FontType.COUSINE.get()));

            add(panelLeft).grow().pad(40);
            add(panelRight).grow().pad(40);
        }
    }

    private class Footer extends VisTable {
        private Footer() {
            //setDebug(true);
            defaults().padBottom(10);

            var thanks = new TypingLabel(assets.strings.get("credits.footer.thanks"), FontType.ROBOTO_LARGE.get());
            var madeWith = new TypingLabel(assets.strings.get("credits.footer.made-with"), FontType.ROBOTO.get());
            var disclaimer = new TypingLabel(assets.strings.get("credits.footer.disclaimer"), FontType.ROBOTO_SMALL.get());
            var returnToTitleBtn = new VisTextButton("Return to title...", new ChangeListener() {
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    if (transitioning) return;
                    game.setScreen(new TitleScreen());
                    transitioning = true;
                }
            });

            add(thanks).expand().row();
            add(madeWith).expandX().row();
            add(returnToTitleBtn).growX().height(40).row();
            add(disclaimer).expandX().bottom();
        }
    }

    public CreditsScreen() {
        initializeUI();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(BACKGROUND_COLOR);

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            // nothing to draw, it's all in the ui
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

        uiRoot.setDebug(true);
        uiRoot.add(new Header()).growX().row();
        uiRoot.add(new Body()).grow().row();
        uiRoot.add(new Footer()).growX();
    }
}
