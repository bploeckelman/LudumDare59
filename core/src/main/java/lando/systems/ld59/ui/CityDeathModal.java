package lando.systems.ld59.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import lando.systems.ld59.Config;
import lando.systems.ld59.Main;

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

        add().height(20f).row();

        VisLabel titleLabel = new VisLabel("City Destroyed!");
        titleLabel.setFontScale(1.5f);
        add(titleLabel).row();

        add().height(20f).row();

        VisLabel messageLabel = new VisLabel("You failed to protect the city...");
        add(messageLabel).row();

        add().height(10f).row();

        VisLabel mercyLabel = new VisLabel("But alien god revived you.");
        add(mercyLabel).row();

        add().height(30f).row();

        VisTextButton continueButton = new VisTextButton("Continue");
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
