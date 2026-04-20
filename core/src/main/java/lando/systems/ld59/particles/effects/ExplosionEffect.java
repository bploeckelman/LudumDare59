package lando.systems.ld59.particles.effects;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.assets.anims.AnimMisc;
import lando.systems.ld59.assets.anims.AnimPet;
import lando.systems.ld59.game.Systems;
import lando.systems.ld59.game.components.Position;
import lando.systems.ld59.particles.ParticleData;
import lando.systems.ld59.particles.ParticleEffect;
import lando.systems.ld59.particles.ParticleEffectParams;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExplosionEffect implements ParticleEffect {

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
        var params = (ExplosionEffect.Params) parameters;
        if (params.once) return Collections.emptyList();
        else params.once = true;

        var pool = Systems.particles.pool;
        // Two rings: inner slower big chunks, outer fast small sparks
        var inner = IntStream.range(0, 12).boxed().map(i -> {
            float angle = (360f / 12f) * i + MathUtils.random(-15f, 15f);
            float speed = MathUtils.random(120f, 220f);
            float size = MathUtils.random(14f, 22f);
            float ttl = MathUtils.random(0.5f, 1.0f);
            return ParticleData.initializer(pool.obtain())
                .animation(AnimMisc.SPRING.get())
                .interpolation(Interpolation.pow3Out)
                .startPos(params.target.x, params.target.y)
                .velocityDirection(angle, speed)
                .acceleration(0f, -300f)
                .startSize(size)
                .endSize(size * 0.1f)
                .startColor(1f, 0.9f, 0.1f, 1f)
                .endColor(0.8f, 0.1f, 0f, 0f)
                .timeToLive(ttl)
                .init();
        });

        var outer = IntStream.range(0, 20).boxed().map(i -> {
            float angle = MathUtils.random(0f, 360f);
            float speed = MathUtils.random(250f, 400f);
            float size = MathUtils.random(5f, 10f);
            float ttl = MathUtils.random(0.3f, 0.7f);
            return ParticleData.initializer(pool.obtain())
                .animation(AnimPet.OSHA.get())
                .interpolation(Interpolation.pow2Out)
                .startPos(params.target.x, params.target.y)
                .velocityDirection(angle, speed)
                .acceleration(0f, -500f)
                .startSize(size)
                .endSize(0f)
                .startColor(1f, 0.6f, 0f, 1f)
                .endColor(1f, 0f, 0f, 0f)
                .timeToLive(ttl)
                .init();
        });

        return java.util.stream.Stream.concat(inner, outer).collect(Collectors.toList());
    }
}
