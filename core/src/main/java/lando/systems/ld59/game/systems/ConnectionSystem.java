package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.PendingConnection;
import lando.systems.ld59.game.signals.ConnectionEvent;
import lando.systems.ld59.game.signals.EntityEvent;
import lando.systems.ld59.game.signals.SignalEvent;
import lando.systems.ld59.utils.Util;

public class ConnectionSystem extends IteratingSystem implements Listener<SignalEvent> {

    private static final String TAG = ConnectionSystem.class.getSimpleName();

    public ConnectionSystem() {
        super(Family.one(PendingConnection.class).get());
        SignalEvent.addListener(this);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var pendingConnection = Components.get(entity, PendingConnection.class);
        if (pendingConnection.isComplete()) {
            var turret = pendingConnection.turret;
            var baseButton = pendingConnection.baseButton;

            if (baseButton.isEnergy()) {
                var energyColor = baseButton.getEnergyColor();
                turret.connectEnergy(energyColor);
                Util.log(TAG, "Connected: energy '" + energyColor.type + "' to turret");
                EntityEvent.remove(entity);
            }
            else if (baseButton.isPattern()) {
                var turretPattern = baseButton.getTurretPattern();
                Util.log(TAG, "Connected: pattern '" + turretPattern.type + "' to turret");
                turret.connectPattern(turretPattern);
                EntityEvent.remove(entity);
            }
        }
    }

    @Override
    public void receive(Signal<SignalEvent> signal, SignalEvent event) {
        var isConnectionEvent = event instanceof ConnectionEvent;
        if (!isConnectionEvent) return;

        var pendingConnection = (PendingConnection) null;

        // Should only be one PendingConnection at any given time, get it if there is one
        var entities = getEntities();
        if (entities.size() > 0) {
            var entity = entities.get(0);
            pendingConnection = Components.get(entity, PendingConnection.class);
        }

        if (event instanceof ConnectionEvent.TouchedTurret) {
            var turret = ((ConnectionEvent.TouchedTurret) event).turret();

            // Create a pending connection if there isn't one...
            if (pendingConnection == null) {
                var entity = Factory.createEntity();
                entity.add(new PendingConnection(entity, turret));
                getEngine().addEntity(entity);
            }
            // ...otherwise update the existing pending connection...
            else {
                if (pendingConnection.hasTurret()) {
                    // ...by switching if we're already connected to a different turret
                    if (pendingConnection.turret != turret) {
                        pendingConnection.turret = turret;
                    }
                    // ...or by disconnecting if we touched the same turret, i.e. removing the now empty pending connection
                    else {
                        pendingConnection.turret = null;
                        EntityEvent.remove(pendingConnection.entity);
                    }
                } else {
                    Util.warn(TAG, "Had a pending connection that should have had a turret but didn't");
                }
            }
        }
        else if (event instanceof ConnectionEvent.TouchedBaseButton) {
            var baseButton = ((ConnectionEvent.TouchedBaseButton) event).baseButton();

            // Create a pending connection if there isn't one...
            if (pendingConnection == null) {
                var entity = Factory.createEntity();
                entity.add(new PendingConnection(entity, baseButton));
                getEngine().addEntity(entity);
            }
            // ...otherwise update the existing pending connection...
            else {
                if (pendingConnection.hasBaseButton()) {
                    // ...by switching if we're already connected to a different button
                    if (pendingConnection.baseButton != baseButton) {
                        pendingConnection.baseButton = baseButton;
                    }
                    // ...or by disconnecting if we touched the same button, i.e. removing the now empty pending connection
                    else {
                        pendingConnection.baseButton = null;
                        EntityEvent.remove(pendingConnection.entity);
                    }
                } else {
                    Util.warn(TAG, "Had a pending connection that should have had a baseButton but didn't");
                }
            }
        }
    }
}
