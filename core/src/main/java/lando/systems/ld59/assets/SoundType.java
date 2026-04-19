package lando.systems.ld59.assets;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld59.utils.Util;

import java.util.EnumMap;

public enum SoundType implements AssetType<Sound> {
    SHOT("shot.wav", 1.0f)
    ,BOARD_CLICK("board_click.ogg")
    ,BLIP("blip1.ogg")
    ,LASER("laser1.wav", 1.5f)
    ,SINE_C1("sine_c1.wav", (float) Math.pow(2,(0./12.)))
    ,SINE_D("sine_c1.wav", (float) Math.pow(2,(2./12.)))
    ,SINE_E("sine_c1.wav", (float) Math.pow(2,(4./12.)))
    ,SINE_F("sine_c1.wav", (float) Math.pow(2,(5./12.)))
    ,SINE_G("sine_c1.wav", (float) Math.pow(2,(7./12.)))
    ,SINE_A("sine_c1.wav", (float) Math.pow(2,(8./12.)))
    ,SINE_B("sine_c1.wav", (float) Math.pow(2,(11./12.)))
    ,SINE_C2("sine_c1.wav", (float) Math.pow(2,(12./12)))
    ,SQUARE_C1("square_c1.wav", (float) Math.pow(2,(0./12.)))
    ,SQUARE_D("square_c1.wav", (float) Math.pow(2,(2./12.)))
    ,SQUARE_E("square_c1.wav", (float) Math.pow(2,(4./12.)))
    ,SQUARE_F("square_c1.wav", (float) Math.pow(2,(5./12.)))
    ,SQUARE_G("square_c1.wav", (float) Math.pow(2,(7./12.)))
    ,SQUARE_A("square_c1.wav", (float) Math.pow(2,(8./12.)))
    ,SQUARE_B("square_c1.wav", (float) Math.pow(2,(11./12.)))
    ,SQUARE_C2("square_c1.wav", (float) Math.pow(2,(12./12)))
    ,SAW_C1("saw_c1.wav", (float) Math.pow(2,(0./12.)))
    ,SAW_D("saw_c1.wav", (float) Math.pow(2,(2./12.)))
    ,SAW_E("saw_c1.wav", (float) Math.pow(2,(4./12.)))
    ,SAW_F("saw_c1.wav", (float) Math.pow(2,(5./12.)))
    ,SAW_G("saw_c1.wav", (float) Math.pow(2,(7./12.)))
    ,SAW_A("saw_c1.wav", (float) Math.pow(2,(8./12.)))
    ,SAW_B("saw_c1.wav", (float) Math.pow(2,(11./12.)))
    ,SAW_C2("saw_c1.wav", (float) Math.pow(2,(12./12)))
    ;

    private static final String TAG = SoundType.class.getSimpleName();
    private static final EnumMap<SoundType, Sound> container = AssetType.createContainer(SoundType.class);

    private final String path;
    public float pitch;

    SoundType(String filename, float pitch) {
        this.path = "audio/sounds/" + filename;
        this.pitch = pitch;
    }

    SoundType(String filename) {
        this.path = "audio/sounds/" + filename;
        this.pitch = 1f;
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
