package lando.systems.ld59.assets.anims;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld59.assets.AnimType;

import java.util.EnumMap;

public enum AnimBaseCity implements AnimType {
      CRACK_1
    , CRACK_2
    , CRACK_3
    , CRACK_4
    , DEAD
    , GLASS_BREAK
    , HIT_A
    , IDLE(Animation.PlayMode.LOOP)
    , IDLE_WEAK(Animation.PlayMode.LOOP)
    ;

    private static final String BASE_PATH = "base/city/";
    private static final EnumMap<AnimBaseCity, Animation<TextureRegion>> container = AnimType.createAndRegisterContainer(AnimBaseCity.class);
    private static final EnumMap<AnimBaseCity, AnimType.AnimConfig> configs = AnimType.createConfigs(
            AnimBaseCity.values(), BASE_PATH,
            e -> e.folderPrefix + e.name().toLowerCase().replace("_", "-"),
            e -> new AnimType.Data()
    );

    public final String folderPrefix;
    public final Animation.PlayMode playMode;
    public final float frameDuration;

    AnimBaseCity() {
        this(0.1f, Animation.PlayMode.NORMAL);
    }

    AnimBaseCity(Animation.PlayMode playMode) {
        this(0.1f, playMode);
    }

    AnimBaseCity(float frameDuration, Animation.PlayMode playMode) {
        this.folderPrefix = name().toLowerCase().replace("_", "-") + "/";
        this.playMode = playMode;
        this.frameDuration = frameDuration;
    }

    @Override
    public AnimConfig getConfig() {
        return configs.get(this);
    }
}
