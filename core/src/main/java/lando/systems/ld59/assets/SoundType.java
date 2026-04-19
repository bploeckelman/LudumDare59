package lando.systems.ld59.assets;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld59.utils.Util;

import java.util.EnumMap;

public enum SoundType implements AssetType<Sound> {
    SHOT("shot.wav")
    ,BLIP("blip1.ogg")
    ,SINE_C1("sine_c1.wav")
    ,SINE_D("sine_d.wav")
    ,SINE_E("sine_e.wav")
    ,SINE_F("sine_f.wav")
    ,SINE_G("sine_g.wav")
    ,SINE_A("sine_a.wav")
    ,SINE_B("sine_b.wav")
    ,SINE_C2("sine_c2.wav")
    ,SQUARE_C1("square_c1.wav")
    ,SQUARE_D("square_d.wav")
    ,SQUARE_E("square_e.wav")
    ,SQUARE_F("square_f.wav")
    ,SQUARE_G("square_g.wav")
    ,SQUARE_A("square_a.wav")
    ,SQUARE_B("square_b.wav")
    ,SQUARE_C2("square_c2.wav")
    ,SAW_C1("saw_c1.wav")
    ,SAW_D("saw_d.wav")
    ,SAW_E("saw_e.wav")
    ,SAW_F("saw_f.wav")
    ,SAW_G("saw_g.wav")
    ,SAW_A("saw_a.wav")
    ,SAW_B("saw_b.wav")
    ,SAW_C2("saw_c2.wav")
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

    public static SoundType getRandomSineSound() {
        SoundType[] sineSounds = {SINE_C1, SINE_D, SINE_E, SINE_F, SINE_G, SINE_A, SINE_B, SINE_C2};
        return sineSounds[MathUtils.random(0, sineSounds.length - 1)];
    }

    public static SoundType getRandomSawSound() {
        SoundType[] sawSounds = {SAW_C1, SAW_D, SAW_E, SAW_F, SAW_G, SAW_A, SAW_B, SAW_C2};
        return sawSounds[MathUtils.random(0, sawSounds.length - 1)];
    }

    public static SoundType getRandomSquareSound() {
        SoundType[] squareSounds = {SQUARE_C1, SQUARE_D, SQUARE_E, SQUARE_F, SQUARE_G, SQUARE_A, SQUARE_B, SQUARE_C2};
        return squareSounds[MathUtils.random(0, squareSounds.length - 1)];
    }
}
