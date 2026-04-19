package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import lando.systems.ld59.game.components.CityBase;

public class CityBaseSystem extends IteratingSystem {


    public CityBaseSystem() {
        super(Family.one(CityBase.class).get());
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
