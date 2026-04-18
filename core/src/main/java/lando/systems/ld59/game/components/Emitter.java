package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld59.assets.EmitterType;
import lando.systems.ld59.game.components.renderable.Renderable;
import lando.systems.ld59.particles.ParticleData;
import lando.systems.ld59.particles.ParticleEffect;
import lando.systems.ld59.particles.ParticleEffectParams;

import java.util.List;

public class Emitter extends Renderable implements Component {

    public final EmitterType type;
    public final ParticleEffect effect;

    public ParticleEffectParams params;

    public Emitter(EmitterType type, ParticleEffectParams params) {
        this.type = type;
        this.effect = type.get();
        this.params = params;
    }

    @Override
    public void render(SpriteBatch batch, Position position) {
        throw new GdxRuntimeException("render() not yet implemented for Emitter renderable");
    }

    public List<ParticleData> spawn() {
        return effect.spawn(params);
    }

    public boolean isComplete() {
        return params.isComplete();
    }
}
