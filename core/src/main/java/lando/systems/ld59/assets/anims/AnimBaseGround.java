package lando.systems.ld59.assets.anims;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld59.assets.AnimType;

import java.util.EnumMap;

public enum AnimBaseGround implements AnimType {
      HIT(Animation.PlayMode.NORMAL)
    , IDLE
    , IDLE_NO_GLOW
    ;

    private static final String BASE_PATH = "base/ground/";
    private static final EnumMap<AnimBaseGround, Animation<TextureRegion>> container = AnimType.createAndRegisterContainer(AnimBaseGround.class);
    private static final EnumMap<AnimBaseGround, AnimType.AnimConfig> configs = AnimType.createConfigs(
            AnimBaseGround.values(), BASE_PATH,
            e -> e.name().toLowerCase().replace("_", "-"),
            e -> new AnimType.Data(e.playMode)
    );

    public final Animation.PlayMode playMode;

    AnimBaseGround() {
        this(Animation.PlayMode.LOOP);
    }

    AnimBaseGround(Animation.PlayMode playMode) {
        this.playMode = playMode;
    }

    @Override
    public AnimType.AnimConfig getConfig() {
        return configs.get(this);
    }
}
