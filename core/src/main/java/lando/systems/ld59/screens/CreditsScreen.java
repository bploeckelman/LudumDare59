package lando.systems.ld59.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.textra.Justify;
import com.github.tommyettinger.textra.TypingLabel;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import lando.systems.ld59.Flag;
import lando.systems.ld59.assets.FontType;
import lando.systems.ld59.assets.anims.AnimPet;

public class CreditsScreen extends BaseScreen {

    private static final Color BACKGROUND_COLOR = new Color(0f, 0f, 0f, 0f);
    private float accum = 0f;
    private Image asukaImage;
    private Image roxieImage;
    private Image novaImage;
    private Image cherryImage;
    private Image oshaImage;

    // TODO: in i18n strings...
    //  how to mix formatting placeholders: {0}, {1}, ..., with typing label placeholders like {GRADIENT},
    //  I think they don't play nice because they use the same delimiter `{}`
    //  usage: assets.strings.format("property.name", placeholderValue)

    private class Header extends VisTable {
        private Header() {
            //setDebug(true);
            padTop(10);
            padBottom(10);
            //setHeight(32 + 24 + 50); // font large + font default + padding

            var title = new TypingLabel(assets.strings.get("credits.header.title"), FontType.ATKINSON_HYPERLEGIBLE.get());
            var theme = new TypingLabel(assets.strings.get("credits.header.theme"), FontType.ROUNDABOUT.get());

            add(theme).expandX().padTop(20).row();
            add(title).expand().padTop(20);
            //add(title).expand().padTop(10).padBottom(10).row();
            //add(theme).expandX();
        }
    }

    private class Body extends VisTable {
        private Body() {
            //setDebug(true);
            padTop(10);
            padBottom(10);

            var panelLeft = new VisTable();
            var panelRight = new VisTable();

            // Set background texture on the panels
            var panelBackground = new NinePatchDrawable(assets.plainNine);
            panelLeft.setBackground(panelBackground);
            panelRight.setBackground(panelBackground);
            panelLeft.defaults().left().pad(10);
            panelRight.defaults().right().pad(10);

            panelLeft.add(new TypingLabel(assets.strings.get("credits.body.left.code-heading"), FontType.ROUNDABOUT.get())).row();
            panelLeft.add(new TypingLabel(assets.strings.get("credits.body.left.code-names"), FontType.ROBOTO.get())).row();
            panelLeft.row().height(50f);
            panelLeft.add(new TypingLabel(assets.strings.get("credits.body.left.pets-heading"), FontType.ROUNDABOUT.get())).row();

            var petsTable = new VisTable();
            var petSprites = new VisTable();
            petsTable.add(new TypingLabel(assets.strings.get("credits.body.left.pets-names"), FontType.COUSINE.get())).growY();
            petsTable.add(new VisLabel()).width(10f);
            asukaImage = new Image(AnimPet.ASUKA.get().getKeyFrame(accum));
            roxieImage = new Image(AnimPet.ROXIE.get().getKeyFrame(accum));
            novaImage = new Image(AnimPet.NOVA.get().getKeyFrame(accum));
            cherryImage = new Image(AnimPet.CHERRY.get().getKeyFrame(accum));
            oshaImage = new Image(AnimPet.OSHA.get().getKeyFrame(accum));
            petSprites.add(asukaImage).size(32f, 32f).row();
            petSprites.add(oshaImage).size(32f, 32f).row();
            petSprites.add(cherryImage).size(32f, 32f).row();
            petSprites.add(roxieImage).size(38f, 32f).row();
            petSprites.add(novaImage).size(38f, 32f);
            petsTable.add(petSprites);
            panelLeft.add(petsTable).row();

            panelRight.add(new TypingLabel(assets.strings.get("credits.body.right.art-heading"), FontType.ROUNDABOUT.get())).row();
            panelRight.add(new TypingLabel(assets.strings.get("credits.body.right.art-names"), FontType.ROBOTO.get())).row();
            panelRight.row().height(50f);
            panelRight.add(new TypingLabel(assets.strings.get("credits.body.right.audio-heading"), FontType.ROUNDABOUT.get())).row();
            panelRight.add(new TypingLabel(assets.strings.get("credits.body.right.audio-names"), FontType.COUSINE.get())).row();

            add(panelLeft).width(500f).growY().pad(10);
            add(panelRight).width(500f).growY().pad(10);
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
            //add(returnToTitleBtn).growX().height(40).row();
            add(returnToTitleBtn).width(200).height(40).row();
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
    public void update(float delta) {
        super.update(delta);
        accum += delta;

        // Update animated pet sprites
        if (asukaImage != null) {
            asukaImage.setDrawable(new Image(AnimPet.ASUKA.get().getKeyFrame(accum)).getDrawable());
        }
        if (roxieImage != null) {
            roxieImage.setDrawable(new Image(AnimPet.ROXIE.get().getKeyFrame(accum)).getDrawable());
        }
        if (novaImage != null) {
            novaImage.setDrawable(new Image(AnimPet.NOVA.get().getKeyFrame(accum)).getDrawable());
        }
        if (cherryImage != null) {
            cherryImage.setDrawable(new Image(AnimPet.CHERRY.get().getKeyFrame(accum)).getDrawable());
        }
        if (oshaImage != null) {
            oshaImage.setDrawable(new Image(AnimPet.OSHA.get().getKeyFrame(accum)).getDrawable());
        }
        uiStage.act(delta);
    }

    @Override
    protected void initializeUI() {
        if (Flag.DEBUG_RENDER.isEnabled()) {
            var screenName = new VisLabel(getClass().getSimpleName());
            screenName.setPosition(10, windowCamera.viewportHeight - 10 - screenName.getHeight());
            uiStage.setDebugInvisible(false);
        }

        uiRoot.setDebug(true);
        uiRoot.add(new Header()).growX().row();
        uiRoot.add(new Body()).grow().row();
        uiRoot.add(new Footer()).growX();
    }
}
