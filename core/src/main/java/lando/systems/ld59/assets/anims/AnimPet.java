package lando.systems.ld59.assets.anims;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld59.assets.AnimType;

import java.util.EnumMap;

public enum AnimPet implements AnimType {
    ASUKA,
    OSHA,
    CHERRY,
    ROXIE,
    NOVA
    ;

    private static final String BASE_PATH = "pets/";
    private static final EnumMap<AnimPet, Animation<TextureRegion>> container = AnimType.createAndRegisterContainer(AnimPet.class);
    private static final EnumMap<AnimPet, AnimConfig> configs = AnimType.createConfigs(
            AnimPet.values(), BASE_PATH,
            e -> e.name().toLowerCase().replace("_", "-"),
            e -> new Data()
    );

    @Override
    public AnimConfig getConfig() {
        return configs.get(this);
    }
}
