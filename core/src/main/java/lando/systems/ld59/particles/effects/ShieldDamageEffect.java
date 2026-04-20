package lando.systems.ld59.particles.effects;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.assets.anims.AnimEffect;
import lando.systems.ld59.game.Systems;
import lando.systems.ld59.game.components.Position;
import lando.systems.ld59.particles.ParticleData;
import lando.systems.ld59.particles.ParticleEffect;
import lando.systems.ld59.particles.ParticleEffectParams;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShieldDamageEffect implements ParticleEffect {

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
        var params = (ShieldDamageEffect.Params) parameters;
        if (params.once) return Collections.emptyList();
        else params.once = true;

        var pool = Systems.particles.pool;
        return IntStream.range(0, 8).boxed()
            .map(i -> {
                var ttl = MathUtils.random(0.3f, 0.6f);
                return ParticleData.initializer(pool.obtain())
                    .keyframe(AnimEffect.FLARE.get().getKeyFrame(0f))
                    .startPos(params.target.x, params.target.y)
                    .startSize(140f, 40f)
                    .endSize(0f, 40f)
                    .startColor(0f, 1f, 1f, 1f)
                    .endColor(0f, 1f, 1f, 1f)
                    .timeToLive(ttl)
                    .init();
            })
            .collect(Collectors.toList());
    }
}
