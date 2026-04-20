package lando.systems.ld59.particles.effects;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.assets.anims.AnimEmoji;
import lando.systems.ld59.game.Systems;
import lando.systems.ld59.game.components.Position;
import lando.systems.ld59.particles.ParticleData;
import lando.systems.ld59.particles.ParticleEffect;
import lando.systems.ld59.particles.ParticleEffectParams;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EmojiPopEffect implements ParticleEffect {

    public static class Params implements ParticleEffectParams {
        public Position target;
        public AnimEmoji emoji;
        public int count;
        public boolean once;

        public Params(Position target, AnimEmoji emoji) {
            this(target, emoji, 1);
        }

        public Params(Position target, AnimEmoji emoji, int count) {
            this.target = target;
            this.emoji = emoji;
            this.count = count;
            this.once = false;
        }

        @Override
        public boolean isComplete() {
            return once;
        }
    }

    @Override
    public List<ParticleData> spawn(ParticleEffectParams parameters) {
        var params = (EmojiPopEffect.Params) parameters;
        if (params.once) return Collections.emptyList();
        else params.once = true;

        var pool = Systems.particles.pool;
        return IntStream.range(0, params.count).boxed()
            .map(i -> {
                float angle = MathUtils.random(60f, 120f);
                float speed = MathUtils.random(10f, 25f);
                float startSize = MathUtils.random(32f, 48f);
                float ttl = 2f;

                return ParticleData.initializer(pool.obtain())
                    .animation(params.emoji.get())
                    .interpolation(Interpolation.linear)
                    .startPos(params.target.x, params.target.y)
                    .velocityDirection(angle, speed)
                    .acceleration(0f, 50f)
                    .startSize(startSize)
                    .endSize(0.5f)
                    .startAlpha(1f)
                    .endAlpha(0.25f)
                    .timeToLive(ttl)
                    .init();
            })
            .collect(Collectors.toList());
    }
}
