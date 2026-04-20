package lando.systems.ld59.assets.anims;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld59.assets.AnimType;

import java.util.EnumMap;

public enum AnimBaseButton implements AnimType {
      BOARD_LEFT
    , BOARD_RIGHT
    , BLANK_HIT
    , BLANK_IDLE
    , BLANK_ON
    // Color Energy button animations -----------------------------------------
    , BLUE_HIT
    , BLUE_IDLE
    , BLUE_ON(0.3f, Animation.PlayMode.LOOP)
    , GREEN_HIT
    , GREEN_IDLE
    , GREEN_ON(0.3f, Animation.PlayMode.LOOP)
    , RED_HIT
    , RED_IDLE
    , RED_ON(0.3f, Animation.PlayMode.LOOP)
    // Shape button animations ------------------------------------------------
    , CIRCLE_HIT
    , CIRCLE_IDLE
    , CIRCLE_ON(0.3f, Animation.PlayMode.LOOP)
    , SQUARE_HIT
    , SQUARE_IDLE
    , SQUARE_ON(0.3f, Animation.PlayMode.LOOP)
    , TRIANGLE_HIT
    , TRIANGLE_IDLE
    , TRIANGLE_ON(0.3f, Animation.PlayMode.LOOP)
    ;

    private static final String BASE_PATH = "buttons/";
    private static final EnumMap<AnimBaseButton, Animation<TextureRegion>> container = AnimType.createAndRegisterContainer(AnimBaseButton.class);
    private static final EnumMap<AnimBaseButton, AnimType.AnimConfig> configs = AnimType.createConfigs(
            AnimBaseButton.values(), BASE_PATH,
            e -> e.folderPrefix + e.name().toLowerCase().replace("_", "-"),
            e -> new AnimType.Data(e.frameDuration, e.playMode)
    );

    public final String folderPrefix;
    public final Animation.PlayMode playMode;
    public final float frameDuration;

    AnimBaseButton() {
        this(0.1f, Animation.PlayMode.NORMAL);
    }

    AnimBaseButton(float frameDuration, Animation.PlayMode playMode) {
        this.folderPrefix = name().toLowerCase().replace("_", "-") + "/";
        this.playMode = playMode;
        this.frameDuration = frameDuration;
    }

    @Override
    public AnimType.AnimConfig getConfig() {
        return configs.get(this);
    }
}
