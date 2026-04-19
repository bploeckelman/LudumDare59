package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.assets.SoundType;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.signals.AudioEvent;

public class Health implements Component {
    public float currentHealth;
    public float maxHealth;

    public float lastHit;

    public Health(float maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    public void update(float dt) {
        lastHit -= dt;
    }

    public void heal(float amount) {
        currentHealth = Math.min(maxHealth, currentHealth + amount);
    }

    public void damage(float amount) {
        currentHealth = Math.max(0, currentHealth - amount);
    }

    public void getHit(Entity entity, float damage) {
        lastHit = .1f;
        damage(damage);

        var energyColor = Components.get(entity, EnergyColor.class);

        boolean useFancySounds = energyColor != null;
        if(useFancySounds){
            switch (energyColor.type) {
                case RED:
                    float sineVolume = .45f;
                    switch (MathUtils.random(0, 7)) {
                        case 0:
                            AudioEvent.playSound(SoundType.SINE_C1, sineVolume);
                            break;
                        case 1:
                            AudioEvent.playSound(SoundType.SINE_D, sineVolume);
                            break;
                        case 2:
                            AudioEvent.playSound(SoundType.SINE_E, sineVolume);
                            break;
                        case 3:
                            AudioEvent.playSound(SoundType.SINE_F, sineVolume);
                            break;
                        case 4:
                            AudioEvent.playSound(SoundType.SINE_G, sineVolume);
                            break;
                        case 5:
                            AudioEvent.playSound(SoundType.SINE_A, sineVolume);
                            break;
                        case 6:
                            AudioEvent.playSound(SoundType.SINE_B, sineVolume);
                            break;
                        case 7:
                            AudioEvent.playSound(SoundType.SINE_C2, sineVolume);
                            break;
                    }
                    break;


                case BLUE:
                    float squareVolume = .05f;
                    switch (MathUtils.random(0, 7)) {
                        case 0:
                            AudioEvent.playSound(SoundType.SQUARE_C1, squareVolume);
                            break;
                        case 1:
                            AudioEvent.playSound(SoundType.SQUARE_D, squareVolume);
                            break;
                        case 2:
                            AudioEvent.playSound(SoundType.SQUARE_E, squareVolume);
                            break;
                        case 3:
                            AudioEvent.playSound(SoundType.SQUARE_F, squareVolume);
                            break;
                        case 4:
                            AudioEvent.playSound(SoundType.SQUARE_G, squareVolume);
                            break;
                        case 5:
                            AudioEvent.playSound(SoundType.SQUARE_A, squareVolume);
                            break;
                        case 6:
                            AudioEvent.playSound(SoundType.SQUARE_B, squareVolume);
                            break;
                        case 7:
                            AudioEvent.playSound(SoundType.SQUARE_C2, squareVolume);
                            break;
                    }
                    break;
                case GREEN:
                    float sawVolume = .125f;
                    switch (MathUtils.random(0, 7)) {
                        case 0:
                            AudioEvent.playSound(SoundType.SAW_C1, sawVolume);
                            break;
                        case 1:
                            AudioEvent.playSound(SoundType.SAW_D, sawVolume);
                            break;
                        case 2:
                            AudioEvent.playSound(SoundType.SAW_E, sawVolume);
                            break;
                        case 3:
                            AudioEvent.playSound(SoundType.SAW_F, sawVolume);
                            break;
                        case 4:
                            AudioEvent.playSound(SoundType.SAW_G, sawVolume);
                            break;
                        case 5:
                            AudioEvent.playSound(SoundType.SAW_A, sawVolume);
                            break;
                        case 6:
                            AudioEvent.playSound(SoundType.SAW_B, sawVolume);
                            break;
                        case 7:
                            AudioEvent.playSound(SoundType.SAW_C2, sawVolume);
                            break;
                    }

                    break;
                default:
                    break;
            }
        }
    }
}
