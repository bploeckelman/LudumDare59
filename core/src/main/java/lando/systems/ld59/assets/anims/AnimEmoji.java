package lando.systems.ld59.assets.anims;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld59.assets.AnimType;

import java.util.EnumMap;

public enum AnimEmoji implements AnimType {
    HAPPY,
    NEUTRAL,
    SAD,
    ;

    private static final String BASE_PATH = "emoji/";
    private static final EnumMap<AnimEmoji, Animation<TextureRegion>> container = AnimType.createAndRegisterContainer(AnimEmoji.class);
    private static final EnumMap<AnimEmoji, AnimConfig> configs = AnimType.createConfigs(
            AnimEmoji.values(), BASE_PATH,
            e -> e.name().toLowerCase().replace("_", "-"),
            e -> new Data()
    );

    @Override
    public AnimConfig getConfig() {
        return configs.get(this);
    }
}
