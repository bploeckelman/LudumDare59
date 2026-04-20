package lando.systems.ld59.assets;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld59.particles.ParticleEffect;
import lando.systems.ld59.particles.effects.*;

import java.util.EnumMap;

public enum EmitterType implements AssetType<ParticleEffect> {
      TEST(TestEffect.class)
    , CONFETTI(PetConfettiEffect.class)
    , EMOJI_POP(EmojiPopEffect.class)
    , EXPLOSION(ExplosionEffect.class)
    , SHIP_EXPLODE(ShipExplodeEffect.class)
    , SMOKE(SmokeEffect.class)
    , SPARKLE(SparkleEffect.class)
    ;

    private static final String TAG = EmitterType.class.getSimpleName();
    private static final EnumMap<EmitterType, ParticleEffect> container = AssetType.createContainer(EmitterType.class);

    public final Class<? extends ParticleEffect> effectType;

    EmitterType(Class<? extends ParticleEffect> effectType) {
        this.effectType = effectType;
    }

    @Override
    public ParticleEffect get() {
        return container.get(this);
    }

    public static void initEnum(Class<?> enumClass, Assets assets) {
        var values = (EmitterType[]) enumClass.getEnumConstants();
        for (var type : values) {
            try {
                var effect = ClassReflection.newInstance(type.effectType);
                container.put(type, effect);
            } catch (ReflectionException e) {
                throw new GdxRuntimeException(
                    Stringf.format("%s: effect '%s' not found for type '%s'",
                    TAG, type.effectType.getSimpleName(), type.name()));
            }
        }
    }
}
