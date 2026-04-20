package lando.systems.ld59.particles.effects;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.assets.anims.AnimPet;
import lando.systems.ld59.game.Systems;
import lando.systems.ld59.game.components.Position;
import lando.systems.ld59.particles.ParticleData;
import lando.systems.ld59.particles.ParticleEffect;
import lando.systems.ld59.particles.ParticleEffectParams;
import lando.systems.ld59.utils.Util;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A festive confetti burst: colorful pieces shoot upward, tumble with spin,
 * then fall back down under gravity. Each piece has a random vivid color.
 */
public class PetConfettiEffect implements ParticleEffect {

    public static class Params implements ParticleEffectParams {
        public Position target;
        public boolean once;

        public Params(Position target) {
            this.target = target;
            this.once = false;
        }

        @Override
        public boolean isComplete() {
            return once;
        }
    }

    @Override
    public List<ParticleData> spawn(ParticleEffectParams parameters) {
        var params = (PetConfettiEffect.Params) parameters;
        if (params.once) return Collections.emptyList();
        else params.once = true;

        var pool = Systems.particles.pool;
        return IntStream.range(0, 30).boxed()
            .map(i -> {
                float angle = MathUtils.random(-20f, 200f);
                float speed = MathUtils.random(150f, 350f);
                float size = MathUtils.random(30f, 50f);
                float ttl = MathUtils.random(0.8f, 1.6f);
                float startRot = MathUtils.random(0f, 360f);
                float spinDir = MathUtils.randomSign();
                float spinAmount = MathUtils.random(360f, 1080f);

                var anim = AnimPet.values()[MathUtils.random(AnimPet.values().length - 1)].get();

                return ParticleData.initializer(pool.obtain())
                    .animation(anim)
                    .startPos(
                        params.target.x,
                        params.target.y
                    )
                    .velocityDirection(angle, speed)
                    .acceleration(0f, -600f)
                    .startSize(size)
                    .endSize(size * 0.5f)
                    .startRotation(startRot)
                    .endRotation(startRot + spinDir * spinAmount)
                    .startColor(Util.randomColor())
                    .endColor(Util.randomColor())
                    .timeToLive(ttl)
                    .init();
            })
            .collect(Collectors.toList());
    }
}
