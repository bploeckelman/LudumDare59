package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;

public class Health implements Component {
    public float currentHealth;
    public float maxHealth;

    public Health(float maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    public void heal(float amount) {
        currentHealth = Math.min(maxHealth, currentHealth + amount);
    }

    public void damage(float amount) {
        currentHealth = Math.max(0, currentHealth - amount);
    }
}
