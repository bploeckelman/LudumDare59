package lando.systems.ld59.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import lando.systems.ld59.Config;
import lando.systems.ld59.Main;
import lando.systems.ld59.assets.FontType;

public class CityDeathModal extends VisWindow {
    public boolean isVisible = false;
    float panelWidth = Config.window_width / 2f;
    float panelHeight = Config.window_height / 2f;

    public CityDeathModal() {
        super("");
        setBackground(new TextureRegionDrawable(Main.game.assets.dialogModal));
        setWidth(panelWidth);
        setHeight(panelHeight);
        setPosition(Config.window_width / 4f, Config.window_height / 3f);
        setVisible(false);
        setModal(true);

        add().height(10f).row();

//        VisLabel titleLabel = new VisLabel("Civilization was Destroyed!");
//        VisLabel titleLabel = new VisLabel("Civilization was Destroyed!");
        TypingLabel textLabelTitle = new TypingLabel("{GRADIENT=black;navy;}The aliens broke through!{ENDGRADIENT}", FontType.HEMI_HEAD.get());
//        titleLabel.setFontScale(1.5f);
//        add(titleLabel).row();
        add(textLabelTitle).row();
        textLabelTitle.skipToTheEnd();
//        textLabelTitle.setColor(new Color(.1f, 0f, 0f, 1));
//        textLabelTitle.align();

        add().height(18f).row();

//        VisLabel messageLabel = new VisLabel("You failed to protect the city...");
        TypingLabel line2 = new TypingLabel("Civilization has been destroyed...", FontType.HEMI_HEAD_SMALL.get());
//        add(messageLabel).row();
        add(line2).row();
        line2.skipToTheEnd();

        add().height(10f).row();

//        VisLabel mercyLabel = new VisLabel("But alien god revived you.");
        TypingLabel line3 = new TypingLabel("...but the gods of Ludum Dare are merciful!\n\nYou live to fight again - back in the fray!", FontType.HEMI_HEAD_SMALL.get());
//        add(mercyLabel).row();
        add(line3).row();
        line3.setColor(Color.BLACK);

        add().height(30f).row();

        VisTextButton continueButton = new VisTextButton("Let's gooooo!");
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        add(continueButton).height(35f).width(120f).row();
    }

    public void show() {
        isVisible = true;
        setVisible(true);
    }

    public void hide() {
        isVisible = false;
        setVisible(false);
    }
}
