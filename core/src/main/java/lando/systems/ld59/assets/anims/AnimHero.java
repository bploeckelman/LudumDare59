package lando.systems.ld59.assets.anims;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld59.assets.AnimType;

import java.util.EnumMap;

public enum AnimHero implements AnimType {
    ATTACK, ATTACK_EFFECT, DEATH, FALL, HURT, IDLE, JUMP, LAND_EFFECT, RUN;

    private static final String BASE_PATH = "characters/hero/";
    private static final EnumMap<AnimHero, Animation<TextureRegion>> container = AnimType.createAndRegisterContainer(AnimHero.class);
    private static final EnumMap<AnimHero, AnimConfig> configs = AnimType.createConfigs(
            AnimHero.values(), BASE_PATH,
            e -> "hero-" + e.name().toLowerCase().replace("_", "-"),
            e -> new Data()
    );

    @Override
    public AnimConfig getConfig() {
        return configs.get(this);
    }
}
