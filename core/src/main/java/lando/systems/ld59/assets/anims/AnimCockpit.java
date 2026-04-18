package lando.systems.ld59.assets.anims;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld59.assets.AnimType;

import java.util.EnumMap;

public enum AnimCockpit implements AnimType {
      CABLE_1, CABLE_2
    , PORT_1, PORT_2
    , TURRET_EMPLACEMENT_1, TURRET_GUN_BASE_1, TURRET_GUN_1
    ;

    private static final String BASE_PATH = "cockpit/";
    private static final EnumMap<AnimCockpit, Animation<TextureRegion>> container = AnimType.createAndRegisterContainer(AnimCockpit.class);
    private static final EnumMap<AnimCockpit, AnimType.AnimConfig> configs = AnimType.createConfigs(
            AnimCockpit.values(), BASE_PATH,
            e -> e.name().toLowerCase().replace("_", "-"),
            e -> new AnimType.Data()
    );

    @Override
    public AnimType.AnimConfig getConfig() {
        return configs.get(this);
    }
}
