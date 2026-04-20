package lando.systems.ld59.assets.anims;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld59.assets.AnimType;

import java.util.EnumMap;

public enum AnimEffect implements AnimType {
    CIRCLE, FLARE, LIGHT, SMOKE;

    private static final String BASE_PATH = "effects/";
    private static final EnumMap<AnimEffect, Animation<TextureRegion>> container = AnimType.createAndRegisterContainer(AnimEffect.class);
    private static final EnumMap<AnimEffect, AnimType.AnimConfig> configs = AnimType.createConfigs(
            AnimEffect.values(), BASE_PATH,
            e -> e.name().toLowerCase().replace("_", "-"),
            e -> new AnimType.Data(e.frameDuration, e.playMode)
    );

    public final float frameDuration;
    public final Animation.PlayMode playMode;

    AnimEffect() {
        this(0.1f, Animation.PlayMode.LOOP);
    }

    AnimEffect(float frameDuration) {
        this(frameDuration, Animation.PlayMode.LOOP);
    }

    AnimEffect(float frameDuration, Animation.PlayMode playMode) {
        this.frameDuration = frameDuration;
        this.playMode = playMode;
    }

    @Override
    public AnimType.AnimConfig getConfig() {
        return configs.get(this);
    }
}
