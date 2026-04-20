package lando.systems.ld59.assets.anims;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld59.assets.AnimType;
import lando.systems.ld59.game.components.EnergyColor;
import lando.systems.ld59.utils.Util;

import java.util.EnumMap;

public enum AnimEnemy implements AnimType {
      ALIEN_DEAD, ALIEN_FLAIL /* TODO: move AnimMisc.SKELETON here */
    , BOSS, GEM
    , SHIP_A, SHIP_A_LIGHT_OVERLAY
    , SHIP_B, SHIP_B_LIGHT_OVERLAY
    , SHIP_C, SHIP_C_LIGHT_OVERLAY
    ;

    private static final String BASE_PATH = "characters/enemies/";
    private static final EnumMap<AnimEnemy, Animation<TextureRegion>> container = AnimType.createAndRegisterContainer(AnimEnemy.class);
    private static final EnumMap<AnimEnemy, AnimType.AnimConfig> configs = AnimType.createConfigs(
            AnimEnemy.values(), BASE_PATH,
            e -> e.folderPrefix + e.name().toLowerCase().replace("_", "-"),
            e -> new AnimType.Data(e.frameDuration, e.playMode)
    );

    public final String folderPrefix;
    public final float frameDuration;
    public final Animation.PlayMode playMode;

    AnimEnemy() {
        this(0.1f, Animation.PlayMode.LOOP);
    }

    AnimEnemy(float frameDuration) {
        this(frameDuration, Animation.PlayMode.LOOP);
    }

    AnimEnemy(float frameDuration, Animation.PlayMode playMode) {
        this.folderPrefix = name().toLowerCase().replace("_", "-") + "/";
        this.frameDuration = frameDuration;
        this.playMode = playMode;
    }

    public static AnimEnemy of(EnergyColor.Type energyColorType) {
        var anim = AnimEnemy.SHIP_A;
        switch (energyColorType) {
            case RED: anim = AnimEnemy.SHIP_A; break;
            case GREEN: anim = AnimEnemy.SHIP_B; break;
            case BLUE: anim = AnimEnemy.SHIP_C; break;
        }
        return anim;
    }

    public AnimEnemy getLightOverlay() {
        var anim = SHIP_A_LIGHT_OVERLAY;
        switch (this) {
            case SHIP_A: anim = SHIP_A_LIGHT_OVERLAY;
            case SHIP_B: anim = SHIP_B_LIGHT_OVERLAY;
            case SHIP_C: anim = SHIP_C_LIGHT_OVERLAY;
            default: Util.warn(getClass().getSimpleName(), "No light overlay anim matching: " + name());
        }
        return anim;
    }

    @Override
    public AnimType.AnimConfig getConfig() {
        return configs.get(this);
    }
}
