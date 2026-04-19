package lando.systems.ld59.assets.anims;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld59.assets.AnimType;

import java.util.EnumMap;

public enum AnimBaseTurret implements AnimType {
    // TODO: add door closed/open, overlays (rocks, port, arrow), etc... once provided
      BASE
    , BARREL_ICON
    , CANNON_BARREL_A
    , CANNON_BARREL_B
    , CANNON_BARREL_C
    , CANNON_BARREL_D
    , CANNON_BARREL_E
    , CANNON
    ;

    public AnimBaseTurret nextBarrel() {
        var next = AnimBaseTurret.CANNON_BARREL_A;
        switch (this) {
            case CANNON_BARREL_A: next = CANNON_BARREL_B; break;
            case CANNON_BARREL_B: next = CANNON_BARREL_C; break;
            case CANNON_BARREL_C: next = CANNON_BARREL_D; break;
            case CANNON_BARREL_D: next = CANNON_BARREL_E; break;
            case CANNON_BARREL_E: next = CANNON_BARREL_A; break;
        }
        return next;
    }

    private static final String BASE_PATH = "base/turret/";
    private static final EnumMap<AnimBaseTurret, Animation<TextureRegion>> container = AnimType.createAndRegisterContainer(AnimBaseTurret.class);
    private static final EnumMap<AnimBaseTurret, AnimType.AnimConfig> configs = AnimType.createConfigs(
            AnimBaseTurret.values(), BASE_PATH,
            e -> e.name().toLowerCase().replace("_", "-"),
            e -> new AnimType.Data()
    );

    @Override
    public AnimType.AnimConfig getConfig() {
        return configs.get(this);
    }
}
