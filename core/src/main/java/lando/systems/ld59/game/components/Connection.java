package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import lando.systems.ld59.Main;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.renderable.FlatShape;
import lando.systems.ld59.game.signals.EntityEvent;
import lando.systems.ld59.utils.Util;

public class Connection implements Component {

    private static final String TAG = Connection.class.getSimpleName();

    public enum State { PENDING, CONNECTED }

    public final Entity entity;

    private State state;
    private Turret turret;
    private BaseButton baseButton;
    private FlatShape flatShape;

    public static Connection createPending(Entity entity, Turret turret) {
        return new Connection(entity, State.PENDING, turret, null);
    }

    public static Connection createPending(Entity entity, BaseButton baseButton) {
        return new Connection(entity, State.PENDING, null, baseButton);
    }

    private Connection(Entity entity, State state, Turret turret, BaseButton baseButton) {
        this.entity = entity;
        this.state = state;
        this.turret = turret;
        this.baseButton = baseButton;
        this.flatShape = null;
    }

    public void complete() {
        if (baseButton.isEnergy()) {
            var energyColor = baseButton.getEnergyColor();
            turret.connectEnergy(energyColor);
            state = State.CONNECTED;
            Util.log(TAG, "Connected: energy '" + energyColor.type + "' to turret");
        }
        else if (baseButton.isPattern()) {
            var turretPattern = baseButton.getTurretPattern();
            turret.connectPattern(turretPattern);
            state = State.CONNECTED;
            Util.log(TAG, "Connected: pattern '" + turretPattern.type + "' to turret");
        }

        if (flatShape == null) {
            var turretPos = Components.get(turret.entity, Position.class);
            var buttonPos = Components.get(baseButton.entity, Position.class);
            flatShape = new FlatShape(buttonPos.x, buttonPos.y, turretPos.x, turretPos.y, turret.getCannonColor(), 10);
            flatShape.depth = Base.ANIM_DEPTH + 20;
            entity.add(flatShape);
        }
    }

    public boolean hasTurret() { return turret != null; }
    public boolean hasBaseButton() { return baseButton != null; }

    public boolean hasBothEndpoints() { return hasTurret() && hasBaseButton(); }
    public boolean isConnected() { return state == State.CONNECTED; }
    public boolean isPending()   { return state == State.PENDING; }

    public Turret getTurret() { return turret; }
    public BaseButton getBaseButton() { return baseButton; }

    public void setTurret(Turret turret) { this.turret = turret; }
    public void setBaseButton(BaseButton baseButton) { this.baseButton = baseButton; }

    public void removeConnection() {
        if (baseButton != null && turret != null) {
            if (baseButton.isEnergy()) {
                turret.connectEnergy(null);
            } else if (baseButton.isPattern()) {
                turret.connectPattern(null);
            }
            flatShape = null;
            turret = null;
            baseButton = null;
            EntityEvent.remove(entity);
        }

    }
}
