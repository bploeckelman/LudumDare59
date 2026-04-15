package lando.systems.ld59.particles;

import java.util.List;

public interface ParticleEffect {
    List<ParticleData> spawn(ParticleEffectParams params);
}
