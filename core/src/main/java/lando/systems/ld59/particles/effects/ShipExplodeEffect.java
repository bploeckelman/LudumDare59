package lando.systems.ld59.particles.effects;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.assets.anims.AnimEffect;
import lando.systems.ld59.game.Systems;
import lando.systems.ld59.game.components.Position;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.particles.ParticleData;
import lando.systems.ld59.particles.ParticleEffect;
import lando.systems.ld59.particles.ParticleEffectParams;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ShipExplodeEffect implements ParticleEffect {

    public static class Params implements ParticleEffectParams {
        public final Position position;
        public final Animator animator;
        public boolean once = false;

        public Params(Position targetPosition, Animator animator) {
            this.position = targetPosition;
            this.animator = animator;
        }

        @Override
        public boolean isComplete() {
            return once;
        }
    }

    @Override
    public List<ParticleData> spawn(ParticleEffectParams parameters) {
        var params = (ShipExplodeEffect.Params) parameters;
        if (params.once) return Collections.emptyList(); // ???
        else params.once = true;

        var pool = Systems.particles.pool;

        // Parallel: flare(0.2), light(0.3), circle(0.3), smoke(0.5)
//        var flare = Stream.of(ParticleData.initializer(pool.obtain())
//                .animation(AnimEffect.FLARE.get())
//                .startPos(params.position.x, params.position.y)
//                .startSize(params.animator.size.x, params.animator.size.y)
//                .startAlpha(1)
//                .timeToLive(0.2f)
//                .init());
//
//        var light = Stream.of(ParticleData.initializer(pool.obtain())
//                .animation(AnimEffect.LIGHT.get())
//                .startPos(params.position.x, params.position.y)
//                .startSize(0, 0)
//                .endSize(params.animator.size.x, params.animator.size.y)
//                .startAlpha(1)
//                .timeToLive(0.3f)
//                .init());
//
        var circle = Stream.of(ParticleData.initializer(pool.obtain())
                .animation(AnimEffect.CIRCLE.get())
                .startPos(params.position.x, params.position.y)
                .startSize(0, 0)
                .endSize(100f, 100f)//params.animator.size.x, params.animator.size.y)
                .startAlpha(1)
//                .timeToLive(1f)
                .init());

//        var smoke = IntStream.range(0, 10).boxed().map(i -> ParticleData.initializer(pool.obtain())
//                .animation(AnimEffect.SMOKE.get())
//                .startPos(params.position.x, params.position.y)
//                .startSize(0, 0)
//                .endSize(30f, 30f)
////                        MathUtils.random(0.5f, 1.5f) * params.animator.size.x,
////                        MathUtils.random(0.5f, 1.5f) * params.animator.size.y)
//                .startColor(1f, 1f, 1f, 1f)
//                .endColor(1f, 1f, 1f, 0f)
//                .interpolation(Interpolation.pow3In)
//                .timeToLive(0.5f)
//                .init());

//        var a = Stream.concat(flare, light);
//        var b = Stream.concat(circle, smoke);
//        return Stream.concat(a, b).collect(Collectors.toList());
        return circle.collect(Collectors.toList());
    }
}
