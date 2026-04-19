package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;

public class Projectile implements Component {
    public float damage;

    public Projectile(float damage) {
        this.damage = damage;
    }
}
