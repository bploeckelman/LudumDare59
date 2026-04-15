package lando.systems.ld59.assets;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.github.tommyettinger.textra.Effect;
import lando.systems.ld59.utils.Util;

import java.util.EnumMap;

public enum EffectType implements AssetType<ShaderProgram> {
    //@formatter:off
      BLINDS
    , CIRCLECROP
    , CROSSHATCH
    , CUBE
    , DISSOLVE
    , DOOMDRIP
    , DOORWAY
    , DREAMY
    , HEART
    , PIXELIZE
    , RADIAL
    , RIPPLE
    , SIMPLEZOOM
    , STEREO
    ;
    //@formatter:on

    private static final String TAG = EffectType.class.getSimpleName();
    private static final EnumMap<EffectType, ShaderProgram> container = AssetType.createContainer(EffectType.class);

    public static EffectType random() {
        var index = MathUtils.random(values().length - 1);
        return values()[index];
    }

    @Override
    public ShaderProgram get() {
        return container.get(this);
    }

    // TODO: unify with ShaderType to use AssetManager
    public static void initEnum(Class<?> enumClass, Assets assets) {
        var prefix = "shaders/transitions/";
        var vertex = prefix + "default.vert";
        var values = (EffectType[]) enumClass.getEnumConstants();
        for (var type : values) {
            var filename = type.name().toLowerCase() + ".frag";
            var fragment = prefix + filename;
            var shader = Util.loadShader(vertex, fragment);
            container.put(type, shader);
        }
    }
}
