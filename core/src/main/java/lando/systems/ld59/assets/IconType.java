package lando.systems.ld59.assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.tommyettinger.digital.Stringf;

import java.util.EnumMap;

public enum IconType implements AssetType<TextureRegion> {
    //@formatter:off
//      CARD_STACK   ("card-stack")
//    , CIRCLE_CHECK ("circle-check")
//    , CIRCLE_X     ("circle-x")
//    , HEART        ("heart")
//    , HEART_BROKEN ("heart-broken")
//    , NOTEPAD      ("notepad")
//    , PERSON_PLAY  ("person-play")
//    , PERSON_X     ("person-x")
//    , PUZZLE       ("puzzle")
//    , SKULL        ("skull")
//    , MENU         ("menu")
//    , WRENCH       ("wrench")
//    , X            ("x")
    ;
    //@formatter:on

    private static final String TAG = IconType.class.getSimpleName();
    private static final EnumMap<IconType, TextureRegion> container = AssetType.createContainer(IconType.class);

    private final String regionName;

    IconType(String regionName) {
        this.regionName = "icons/" + regionName;
    }

    @Override
    public TextureRegion get() {
        return container.get(this);
    }

    public static void initEnum(Class<?> enumClass, Assets assets) {
        var atlas = assets.atlas;
        var values = (IconType[]) enumClass.getEnumConstants();
        for (var type : values) {
            var region = atlas.findRegion(type.regionName);
            if (region == null) {
                throw new GdxRuntimeException(Stringf.format("%s: atlas region '%s' not found for '%s'", TAG, type.regionName, type.name()));
            }
            container.put(type, region);
        }
    }
}
