package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.assets.SoundType;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.signals.AudioEvent;
import lando.systems.ld59.game.systems.AudioSystem;
import lando.systems.ld59.utils.Util;

public class Health implements Component {
    public float currentHealth;
    public float maxHealth;

    public float lastHit;

    public Health(float maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.lastHit = 3f;
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    public void update(float dt) {
        lastHit += dt;
    }

    public void heal(float amount) {
        currentHealth = Math.min(maxHealth, currentHealth + amount);
    }

    public void damage(float amount) {
        currentHealth = Math.max(0, currentHealth - amount);
    }

    public void getHit(Entity entity, float damage) {
        lastHit = 0f;
        damage(damage);
//        var energyColor = Components.get(entity, EnergyColor.class);
//        boolean useFancySounds = energyColor != null;
    }
}
