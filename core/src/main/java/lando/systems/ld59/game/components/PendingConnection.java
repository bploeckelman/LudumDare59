package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class PendingConnection implements Component {

    public final Entity entity;

    public Turret turret;
    public BaseButton baseButton;

    public PendingConnection(Entity entity, Turret turret) { this.entity = entity; this.turret = turret; }
    public PendingConnection(Entity entity, BaseButton baseButton) { this.entity = entity; this.baseButton = baseButton; }

    public boolean hasTurret() { return turret != null; }
    public boolean hasBaseButton() { return baseButton != null; }

    public boolean isComplete() {
        return hasTurret() && hasBaseButton();
    }
}
