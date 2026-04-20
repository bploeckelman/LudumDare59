package lando.systems.ld59.assets;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld59.utils.Util;

import java.util.EnumMap;

public enum SoundType implements AssetType<Sound> {
    SHOT("shot.wav", 1.0f)
    ,EXPLODE_SMALL("explode_small.ogg")
    ,BLIP("blip1.ogg")
    ,BLIP_HIT("blip_hit.wav", 1f)
    ,SHATTER("shatter2.wav", 0.2f)
    ,CRACK1("crack1.wav", 0.2f)
    ,CRACK2("crack2.wav", 0.2f)
    ,THUD("thud.wav")
    ,LASER("laser1.wav", 1f)
    ,CLANG("clang.wav", 1.5f)
    ,SINE_C1("sine_c1.wav", (float) Math.pow(2,(0./12.)))
    ,SINE_D("sine_c1.wav", (float) Math.pow(2,(2./12.)))
    ,SINE_E("sine_c1.wav", (float) Math.pow(2,(4./12.)))
    ,SINE_F("sine_c1.wav", (float) Math.pow(2,(5./12.)))
    ,SINE_G("sine_c1.wav", (float) Math.pow(2,(7./12.)))
    ,SINE_A("sine_c1.wav", (float) Math.pow(2,(9./12.)))
    ,SINE_B("sine_c1.wav", (float) Math.pow(2,(11./12.)))
    ,SINE_C2("sine_c1.wav", (float) Math.pow(2,(12./12)))
    ,SQUARE_C1("square_c1.wav", (float) Math.pow(2,(0./12.)))
    ,SQUARE_D("square_c1.wav", (float) Math.pow(2,(2./12.)))
    ,SQUARE_E("square_c1.wav", (float) Math.pow(2,(4./12.)))
    ,SQUARE_F("square_c1.wav", (float) Math.pow(2,(5./12.)))
    ,SQUARE_G("square_c1.wav", (float) Math.pow(2,(7./12.)))
    ,SQUARE_A("square_c1.wav", (float) Math.pow(2,(9./12.)))
    ,SQUARE_B("square_c1.wav", (float) Math.pow(2,(11./12.)))
    ,SQUARE_C2("square_c1.wav", (float) Math.pow(2,(12./12)))
    ,SAW_C1("saw_c1.wav", (float) Math.pow(2,(0./12.)))
    ,SAW_D("saw_c1.wav", (float) Math.pow(2,(2./12.)))
    ,SAW_E("saw_c1.wav", (float) Math.pow(2,(4./12.)))
    ,SAW_F("saw_c1.wav", (float) Math.pow(2,(5./12.)))
    ,SAW_G("saw_c1.wav", (float) Math.pow(2,(7./12.)))
    ,SAW_A("saw_c1.wav", (float) Math.pow(2,(9/12.)))
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

    public static int[] cMaj = {0, 2, 4};
    public static int[] dMin = {1, 3, 5};
    public static int[] aMin = {0, 2, 5};
    public static int[] fMaj = {0, 3, 5};
    public static int[] gMaj = {1, 4, 6};
    public enum NoteType  {
        SAW,
        SQUARE,
        SINE
    }
    // 0 - 1 - 2 - 3 - 4 - 5 - 6 - 7
    // C - D - E - F - G - A - B - C

    public static SoundType getRandomSound(int[] notes, NoteType noteType) {

        SoundType[] sounds;

        switch (noteType) {
            case SAW:
                sounds = new SoundType[] {SAW_C1, SAW_D, SAW_E, SAW_F, SAW_G, SAW_A, SAW_B, SAW_C2};
                break;
            case SINE:
                sounds = new SoundType[] {SINE_C1, SINE_D, SINE_E, SINE_F, SINE_G, SINE_A, SINE_B, SINE_C2};
                break;
            case SQUARE:
                sounds = new SoundType[] {SQUARE_C1, SQUARE_D, SQUARE_E, SQUARE_F, SQUARE_G, SQUARE_A, SQUARE_B, SQUARE_C2};
                break;
            default:
                sounds = new SoundType[] {SINE_C1, SINE_D, SINE_E, SINE_F, SINE_G, SINE_A, SINE_B, SINE_C2};
                break;
        }
        int noteIndex = notes[MathUtils.random(0, notes.length - 1)];
        return sounds[noteIndex];
    }

    public static SoundType getRandomSineSound(int[] notes, NoteType noteType) {
        SoundType[] sineSounds = {SINE_C1, SINE_D, SINE_E, SINE_F, SINE_G, SINE_A, SINE_B, SINE_C2};
        int noteIndex = notes[MathUtils.random(0, notes.length - 1)];
        return sineSounds[noteIndex];
    }

    public static SoundType getRandomSawSound(int[] notes) {
        SoundType[] sawSounds = {SAW_C1, SAW_D, SAW_E, SAW_F, SAW_G, SAW_A, SAW_B, SAW_C2};
        int noteIndex = notes[MathUtils.random(0, notes.length - 1)];
        return sawSounds[MathUtils.random(0, sawSounds.length - 1)];
    }

    public static SoundType getRandomSquareSound(int[] notes) {
        SoundType[] squareSounds = {SQUARE_C1, SQUARE_D, SQUARE_E, SQUARE_F, SQUARE_G, SQUARE_A, SQUARE_B, SQUARE_C2};
        int noteIndex = notes[MathUtils.random(0, notes.length - 1)];
        return squareSounds[MathUtils.random(0, squareSounds.length - 1)];
    }
}
