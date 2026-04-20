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

    public AnimBaseCity next() {
        var next = AnimBaseCity.IDLE;
        switch (this) {
            case CRACK_1:     next = CRACK_2; break;
            case CRACK_2:     next = CRACK_3; break;
            case CRACK_3:     next = CRACK_4; break;
            case CRACK_4:     next = DEAD; break;
            case DEAD:        next = GLASS_BREAK; break;
            case GLASS_BREAK: next = HIT_A; break;
            case HIT_A:       next = IDLE; break;
            case IDLE:        next = IDLE_WEAK; break;
            case IDLE_WEAK:   next = CRACK_1; break;
        }
        return next;
    }

    public AnimBaseCity getFromPercent(float percent) {
        if (percent > .9f) return IDLE;
        if (percent > .7f) return IDLE_WEAK;
        if (percent > .5f) return CRACK_1;
        if (percent > .4f) return CRACK_2;
        if (percent > .3f) return CRACK_3;
        if (percent > .2f) return CRACK_4;
        if (percent > .05f) return GLASS_BREAK;
        if (percent > 0f) return DEAD;
        return DEAD;
    }

    private static final String BASE_PATH = "base/city/";
    private static final EnumMap<AnimBaseCity, Animation<TextureRegion>> container = AnimType.createAndRegisterContainer(AnimBaseCity.class);
    private static final EnumMap<AnimBaseCity, AnimType.AnimConfig> configs = AnimType.createConfigs(
            AnimBaseCity.values(), BASE_PATH,
            e -> e.folderPrefix + e.name().toLowerCase().replace("_", "-"),
            e -> new AnimType.Data(e.frameDuration, e.playMode)
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
