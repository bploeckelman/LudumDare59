package lando.systems.ld59.assets.anims;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld59.assets.AnimType;

import java.util.EnumMap;

public enum AnimBaseTurret implements AnimType {
      BARREL_ICON /* TODO: should be moved to AnimMisc + /misc */
    , BASE_DAMAGED(0.5f)
    , BASE_IDLE(0.5f)
    , BASE_PLUG_LEFT
    , BASE_PLUG_RIGHT
    , CANNON_BARREL_A
    , CANNON_BARREL_B
    , CANNON_BARREL_C
    , CANNON_BARREL_D
    , CANNON_BARREL_E
    , DOOR_OPEN(0.2f, Animation.PlayMode.LOOP_PINGPONG)
    , PORT_ARROW_LIGHT_OVERLAY(0.5f)
    , PORT_LEFT_LIGHT_OVERLAY(0.5f)
    , PORT_RIGHT_LIGHT_OVERLAY(0.5f)
    , ROCK_OVERLAY
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
            e -> e.folderPrefix + e.name().toLowerCase().replace("_", "-"),
            e -> new AnimType.Data(e.frameDuration, e.playMode)
    );

    public final String folderPrefix;
    public final float frameDuration;
    public final Animation.PlayMode playMode;

    AnimBaseTurret() {
        this(0.1f, Animation.PlayMode.LOOP);
    }

    AnimBaseTurret(float frameDuration) {
        this(frameDuration, Animation.PlayMode.LOOP);
    }

    AnimBaseTurret(float frameDuration, Animation.PlayMode playMode) {
        this.folderPrefix = name().toLowerCase().replace("_", "-") + "/";
        this.frameDuration = frameDuration;
        this.playMode = playMode;
    }

    @Override
    public AnimType.AnimConfig getConfig() {
        return configs.get(this);
    }
}
