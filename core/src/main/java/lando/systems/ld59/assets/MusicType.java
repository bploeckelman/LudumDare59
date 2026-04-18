package lando.systems.ld59.assets;

import com.badlogic.gdx.audio.Music;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld59.utils.Util;

import java.util.EnumMap;

public enum MusicType implements AssetType<Music> {
    SAMPLE_MUSIC("music.wav")
    ;

    private static final String TAG = MusicType.class.getSimpleName();
    private static final EnumMap<MusicType, Music> container = AssetType.createContainer(MusicType.class);

    private final String path;

    MusicType(String filename) {
        this.path = "audio/musics/" + filename;
    }

    @Override
    public Music get() {
        return container.get(this);
    }

    public static void loadEnum(Class<?> enumClass, Assets assets) {
        var mgr = assets.mgr;
        var values = (MusicType[]) enumClass.getEnumConstants();
        for (var type : values) {
            mgr.load(type.path, Music.class);
        }
    }

    public static void initEnum(Class<?> enumClass, Assets assets) {
        var mgr = assets.mgr;
        var values = (MusicType[]) enumClass.getEnumConstants();
        for (var type : values) {
            var music = mgr.get(type.path, Music.class);
            if (music == null) {
                Util.log(TAG, Stringf.format("music '%s' not found for type %s", type.path, type));
                continue;
            }
            container.put(type, music);
        }
    }
}
