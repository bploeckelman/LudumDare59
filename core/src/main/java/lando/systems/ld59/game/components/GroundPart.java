package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class GroundPart implements Component {
    public final Entity baseEntity;
    public GroundPart(Entity baseEntity) {
        this.baseEntity = baseEntity;
    }
}
