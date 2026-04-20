package lando.systems.ld59.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld59.assets.AnimType;
import lando.systems.ld59.assets.anims.AnimEffect;
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

public class SmokeEffect implements ParticleEffect {

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
        var params = (SmokeEffect.Params) parameters;
        if (params.once) return Collections.emptyList();
        else params.once = true;

        var pool = Systems.particles.pool;
        return IntStream.range(0, 30).boxed()
            .map(i -> {
                var angle = MathUtils.random(0f, 360f);
                var speed = MathUtils.random(30f, 50f);
                var startSize = MathUtils.random(40f, 50f);
                var ttl = MathUtils.random(0.5f, 1f);
                return ParticleData.initializer(pool.obtain())
                    .keyframe(AnimEffect.SMOKE.get().getKeyFrame(MathUtils.random(0f, 1f)))
                    .startPos(params.target.x, params.target.y)
                    .velocityDirection(angle, speed)
                    .startSize(startSize)
                    .endSize(startSize/4f, startSize/4f)
                    .endAlpha(0f)
                    .timeToLive(ttl)
                    .init();
            })
            .collect(Collectors.toList());
    }
}
