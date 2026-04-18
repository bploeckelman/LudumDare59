package lando.systems.ld59.assets.anims;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld59.assets.AnimType;

import java.util.EnumMap;

public enum AnimBase implements AnimType {
    GROUND, CITY, TURRET_BASE, TURRET_CANNON;

    private static final String BASE_PATH = "base/";
    private static final EnumMap<AnimBase, Animation<TextureRegion>> container = AnimType.createAndRegisterContainer(AnimBase.class);
    private static final EnumMap<AnimBase, AnimType.AnimConfig> configs = AnimType.createConfigs(
            AnimBase.values(), BASE_PATH,
            e -> e.name().toLowerCase().replace("_", "-"),
            e -> new AnimType.Data()
    );

    @Override
    public AnimType.AnimConfig getConfig() {
        return configs.get(this);
    }
}
