package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.game.Components;

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
    }

}
