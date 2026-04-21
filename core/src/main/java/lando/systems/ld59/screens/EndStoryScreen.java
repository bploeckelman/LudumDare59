package lando.systems.ld59.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.TypingLabel;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import lando.systems.ld59.Flag;
import lando.systems.ld59.assets.FontType;
import lando.systems.ld59.assets.anims.AnimPet;
import lando.systems.ld59.game.Stats;

public class EndStoryScreen extends BaseScreen {

    private static final Color BACKGROUND_COLOR = new Color(0f, 0f, .1f, 0f);
    private float accum = 0f;
    private Font creditFont = FontType.HEMI_HEAD_SMALL.get();
    private Font creditHeader = FontType.HEMI_HEAD_CREDITS.get();
    TypingLabel storyText;

    // TODO: in i18n strings...
    //  how to mix formatting placeholders: {0}, {1}, ..., with typing label placeholders like {GRADIENT},
    //  I think they don't play nice because they use the same delimiter `{}`
    //  usage: assets.strings.format("property.name", placeholderValue)

    private class Header extends VisTable {
        private Header() {
            //setDebug(true);
            padTop(50);
            padBottom(10);
            //setHeight(32 + 24 + 50); // font large + font default + padding

            var title = new TypingLabel(assets.strings.get("endstory.header.title"), creditHeader);
//            var theme = new TypingLabel(assets.strings.get("endstory.header.theme"), creditFont);

//            add(theme).expandX().padTop(10).row();
            add(title).expand().padTop(10);
            //add(title).expand().padTop(10).padBottom(10).row();
            //add(theme).expandX();
        }
    }

    private class Body extends VisTable {
        private Body() {
            //setDebug(true);
//            padTop(10);
//            padBottom(10);

            var panelLeft = new VisTable();
            var panelRight = new VisTable();

            // Set background texture on the panels
            var panelBackground = new NinePatchDrawable(assets.plainNine);
            panelLeft.setBackground(panelBackground);
            panelRight.setBackground(panelBackground);
            panelLeft.defaults().left().pad(10);
            panelRight.defaults().right().pad(10);
            String endStory1 =
                "Well, there you have it folks.\n\n" +
                    "Aliens.\n\n" +
                    ""+
                    "Vaguely phallic eruptions.\n\n" +
                    ""+
                    "Another weekend spent \n" +
                    "frantically cobbling together \n" +
                    "a game that will be played\n" +
                    "for mere moments, then \n" +
                    "forgotten forever. \n\n" +
                    "Like tears.\n\n" +
                    "In the rain.";
            storyText = new TypingLabel(assets.strings.get("endstory.body.main"), creditHeader);
            panelLeft.add(storyText).row();
//            panelLeft.add(new TypingLabel(assets.strings.get("credits.body.left.code-names"), creditFont)).row();
            panelLeft.row().height(40f);


            // Get stats
            var stats = Stats.instance();
            int minutes = (int) (stats.timeElapsed / 60);
            int seconds = (int) (stats.timeElapsed % 60);

            panelRight.add(new TypingLabel(assets.strings.get("endstory.body.right.art-heading"), creditHeader)).row();
            panelRight.row().height(10f);

            panelRight.add(new TypingLabel("Enemies Killed: " + stats.enemiesKilled, creditFont)).row();
            panelRight.add(new TypingLabel("Wrong Color Kills: " + stats.badKills, creditFont)).row();
            panelRight.add(new TypingLabel("Perfect Color Kills: " + stats.goodKills, creditFont)).row();
            panelRight.add(new TypingLabel("Neutral Color Kills: " + stats.neutralKills, creditFont)).row();
            panelRight.row().height(10f);
            panelRight.add(new TypingLabel("Time Survived: " + minutes + "minutes " + seconds + "seconds", creditFont)).row();
            panelRight.add(new TypingLabel("Damage Taken: " + stats.damageTaken, creditFont)).row();
            panelRight.row().height(10f);
            panelRight.add(new TypingLabel("Cities Lost: " + stats.cityLost, creditFont)).row();
            panelRight.row().height(20f);

            add(panelLeft).width(500f).growY().pad(10);
            add(panelRight).width(500f).growY().pad(10);
        }
    }

    private class Footer extends VisTable {
        private Footer() {
            //setDebug(true);
            defaults().padBottom(100);
            defaults().padTop(50);

//            var thanks = new TypingLabel(assets.strings.get("credits.footer.thanks"), creditFont);
//            var madeWith = new TypingLabel(assets.strings.get("credits.footer.made-with"), creditFont);
            var disclaimer = new TypingLabel(assets.strings.get("endstory.footer.disclaimer"), creditFont);
            var returnToTitleBtn = new VisTextButton("Return to title...", new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    if (transitioning) return;
                    game.setScreen(new TitleScreen());
                    transitioning = true;
                }
            });

//            add(thanks).expand().row();
//            add(madeWith).expandX().row();
//            add(returnToTitleBtn).growX().height(40).row();
//            add(returnToTitleBtn).width(200).height(40).row();
            add(disclaimer).expandX().bottom();
        }
    }

    public EndStoryScreen() {
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
        uiStage.act(delta);
        if (Gdx.input.isTouched() && storyText.hasEnded()) {
            if (transitioning) return;
            game.setScreen(new CreditsScreen());
            transitioning = true;
        } else {
            storyText.skipToTheEnd();
        }
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
