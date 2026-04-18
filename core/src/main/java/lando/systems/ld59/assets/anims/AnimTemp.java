package lando.systems.ld59.assets.anims;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld59.assets.AnimType;

import java.util.EnumMap;

public enum AnimTemp implements AnimType {
      ASTRODOG
    , ASTRONAUT_A
    , ASTRONAUT_B
    , SKELETON
    , STARFOX_ARWING
    ;

    private static final String BASE_PATH = "misc/";
    private static final EnumMap<AnimTemp, Animation<TextureRegion>> container = AnimType.createAndRegisterContainer(AnimTemp.class);
    private static final EnumMap<AnimTemp, AnimConfig> configs = AnimType.createConfigs(
            AnimTemp.values(), BASE_PATH,
            e -> e.name().toLowerCase().replace("_", "-"),
            e -> new Data()
    );

    @Override
    public AnimConfig getConfig() {
        return configs.get(this);
    }
}
