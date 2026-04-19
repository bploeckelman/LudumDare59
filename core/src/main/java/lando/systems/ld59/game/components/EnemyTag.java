package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.assets.SoundType;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.signals.AudioEvent;

public class EnemyTag implements Component {
    public final Entity entity;
    public EnergyColor.Type energyColor;
    public EnemyType type;
    public State state;
    public float lastHit;


    public enum State {
        IDLE,
        MOVE,
        SHOOT
    }
    public enum EnemyType {
        FLYER,
        RUNNER,
        LOVER,
        MUNCHER,
        SUCKER;

        public static EnemyType getRandom() {
            return values()[MathUtils.random(values().length - 1)];
        }
    }

    public EnemyTag(Entity entity) {
        this.entity = entity;
    }

    public void update(float dt) {
        lastHit -= dt;
    }

    public void getHit(float damage) {
        lastHit = .1f;
        // play sound or make particle effect
        var health = Components.get(entity, Health.class);
        health.damage(damage);

        boolean useFancySounds = true;
        if(useFancySounds){
            switch (energyColor) {
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
