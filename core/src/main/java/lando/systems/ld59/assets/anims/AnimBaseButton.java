package lando.systems.ld59.assets.anims;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld59.assets.AnimType;

import java.util.EnumMap;

public enum AnimBaseButton implements AnimType {
      BLUE_HIT, BLUE_IDLE, BLUE_ON
    , GREEN_HIT, GREEN_IDLE, GREEN_ON
    , RED_HIT, RED_IDLE, RED_ON
    , CIRCLE_IDLE, SQUARE_IDLE, TRIANGLE_IDLE
    ;

    private static final String BASE_PATH = "buttons/";
    private static final EnumMap<AnimBaseButton, Animation<TextureRegion>> container = AnimType.createAndRegisterContainer(AnimBaseButton.class);
    private static final EnumMap<AnimBaseButton, AnimType.AnimConfig> configs = AnimType.createConfigs(
            AnimBaseButton.values(), BASE_PATH,
            e -> e.folderPrefix + e.name().toLowerCase().replace("_", "-"),
            e -> new AnimType.Data(Animation.PlayMode.NORMAL)
    );

    public final String folderPrefix;

    AnimBaseButton() {
        this.folderPrefix = name().toLowerCase().replace("_", "-") + "/";
    }

    @Override
    public AnimType.AnimConfig getConfig() {
        return configs.get(this);
    }
}
