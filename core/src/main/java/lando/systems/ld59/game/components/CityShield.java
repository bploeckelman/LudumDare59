package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import lando.systems.ld59.game.Constants;

public class CityShield implements Component {

    public float repairTimer = Constants.CITY_SHIELD_REPAIR_TIME;
    public Collider collider;

    public CityShield(Collider collider) {
        this.collider = collider;
    }
}
