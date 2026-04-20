package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;

public class CityShield implements Component {

    public float repairTimer = 0f;
    public Collider collider;

    public CityShield(Collider collider) {
        this.collider = collider;
    }
}
