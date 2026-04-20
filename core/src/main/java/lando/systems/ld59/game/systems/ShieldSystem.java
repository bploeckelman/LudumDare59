package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.CityShield;
import lando.systems.ld59.game.components.Health;

import static lando.systems.ld59.game.Constants.CITY_SHIELD_REPAIR_TIME;

public class ShieldSystem extends IteratingSystem {


    public ShieldSystem() {
        super(Family.one(CityShield.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var health = Components.get(entity, Health.class);
        var cityShield = Components.get(entity, CityShield.class);
        if (health == null) return;
        if (health.isDead()) {
            if (cityShield.repairTimer < 0) {
                cityShield.repairTimer = CITY_SHIELD_REPAIR_TIME;
            }

            cityShield.repairTimer -= deltaTime;

            if (cityShield.repairTimer <= 0) {
                health.currentHealth = health.maxHealth;
                entity.add(cityShield.collider);
            }
        }
    }
}
