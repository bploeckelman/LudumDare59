package lando.systems.ld59.particles.effects;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.assets.anims.AnimMisc;
import lando.systems.ld59.game.Systems;
import lando.systems.ld59.game.components.Position;
import lando.systems.ld59.particles.ParticleData;
import lando.systems.ld59.particles.ParticleEffect;
import lando.systems.ld59.particles.ParticleEffectParams;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A burst of spinning star/gear sparkles that radiate outward, shrink, and fade.
 * Color shifts from bright white-yellow to gold.
 */
public class SparkleEffect implements ParticleEffect {

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
        var params = (SparkleEffect.Params) parameters;
        if (params.once) return Collections.emptyList();
        else params.once = true;

        var pool = Systems.particles.pool;
        return IntStream.range(0, 16).boxed()
            .map(i -> {
                float angle = MathUtils.random(0f, 360f);
                float speed = MathUtils.random(80f, 200f);
                float startSize = 32f;
                float ttl = MathUtils.random(0.4f, 0.9f);
                float startRot = MathUtils.random(0f, 360f);
                float endRot = startRot + MathUtils.randomSign() * MathUtils.random(180f, 540f);
                return ParticleData.initializer(pool.obtain())
                    .animation(AnimMisc.GEAR.get())
                    .interpolation(Interpolation.pow2Out)
                    .startPos(params.target.x, params.target.y)
                    .velocityDirection(angle, speed)
                    .startSize(startSize)
                    .endSize(0f)
                    .startRotation(startRot)
                    .endRotation(endRot)
                    .startColor(1f, 1f, 0.6f, 1f)
                    .endColor(1f, 0.7f, 0f, 0f)
                    .timeToLive(ttl)
                    .init();
            })
            .collect(Collectors.toList());
    }
}
