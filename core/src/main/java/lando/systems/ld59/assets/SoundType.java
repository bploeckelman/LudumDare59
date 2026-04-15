package lando.systems.ld59.assets;

import com.badlogic.gdx.audio.Sound;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld59.utils.Util;

import java.util.EnumMap;

public enum SoundType implements AssetType<Sound> {
    ;

    private static final String TAG = SoundType.class.getSimpleName();
    private static final EnumMap<SoundType, Sound> container = AssetType.createContainer(SoundType.class);

    private final String path;

    SoundType(String filename) {
        this.path = "audio/sounds/" + filename;
    }

    @Override
    public Sound get() {
        return container.get(this);
    }

    public static void loadEnum(Class<?> enumClass, Assets assets) {
        var mgr = assets.mgr;
        var values = (SoundType[]) enumClass.getEnumConstants();
        for (var type : values) {
            mgr.load(type.path, Sound.class);
        }
    }

    public static void initEnum(Class<?> enumClass, Assets assets) {
        var mgr = assets.mgr;
        var values = (SoundType[]) enumClass.getEnumConstants();
        for (var type : values) {
            var sound = mgr.get(type.path, Sound.class);
            if (sound == null) {
                Util.log(TAG, Stringf.format("sound '%s' not found for type %s", type.path, type));
                continue;
            }
            container.put(type, sound);
        }
    }
}
