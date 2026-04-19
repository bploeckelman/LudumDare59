package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.Connection;
import lando.systems.ld59.game.signals.ConnectionEvent;
import lando.systems.ld59.game.signals.EntityEvent;
import lando.systems.ld59.game.signals.SignalEvent;

public class ConnectionSystem extends IteratingSystem implements Listener<SignalEvent> {

    public ConnectionSystem() {
        super(Family.one(Connection.class).get());
        SignalEvent.addListener(this);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var connection = Components.get(entity, Connection.class);
        if (connection.isPending() && connection.hasBothEndpoints()) {
            connection.complete();
        }
    }

    @Override
    public void receive(Signal<SignalEvent> signal, SignalEvent event) {
        var isConnectionEvent = event instanceof ConnectionEvent;
        if (!isConnectionEvent) return;


        // Should only be one PendingConnection at any given time, get it if there is one
        var entities = getEntities();
        var pendingConnection = (Connection) null;
        for (int i = 0; i < entities.size(); i++) {
            var entity = entities.get(i);

            var connection = Components.get(entity, Connection.class);
            if (connection.isPending()) {
                pendingConnection = connection;
                break;
            }
        }

        if (event instanceof ConnectionEvent.TouchedTurret) {
            var turret = ((ConnectionEvent.TouchedTurret) event).turret();

            // Create a pending connection if there isn't one...
            if (pendingConnection == null) {
                var entity = Factory.createEntity();
                entity.add(Connection.createPending(entity, turret));
                getEngine().addEntity(entity);
            }
            // ...otherwise update the existing pending connection...
            else {
                if (pendingConnection.hasTurret()) {
                    // ...by switching if we're already connected to a different turret
                    if (pendingConnection.getTurret() != turret) {
                        pendingConnection.setTurret(turret);
                    }
                    // ...or by disconnecting if we touched the same turret, i.e. removing the now empty pending connection
                    else {
                        pendingConnection.setTurret(null);
                        EntityEvent.remove(pendingConnection.entity);
                    }
                }
                // ...by attaching the turret to complete a connection!
                else {
                    pendingConnection.setTurret(turret);
                    // ...and allowing the next processEntity call to finalize the connection
                }
            }
        }
        else if (event instanceof ConnectionEvent.TouchedBaseButton) {
            var baseButton = ((ConnectionEvent.TouchedBaseButton) event).baseButton();

            // Create a pending connection if there isn't one...
            if (pendingConnection == null) {
                var entity = Factory.createEntity();
                entity.add(Connection.createPending(entity, baseButton));
                getEngine().addEntity(entity);
            }
            // ...otherwise update the existing pending connection...
            else {
                if (pendingConnection.hasBaseButton()) {
                    // ...by switching if we're already connected to a different button
                    if (pendingConnection.getBaseButton() != baseButton) {
                        pendingConnection.setBaseButton(baseButton);
                    }
                    // ...or by disconnecting if we touched the same button, i.e. removing the now empty pending connection
                    else {
                        pendingConnection.setBaseButton(null);
                        EntityEvent.remove(pendingConnection.entity);
                    }
                }
                // ...by attaching the button to complete a connection!
                else {
                    pendingConnection.setBaseButton(baseButton);
                    // ...and allowing the next processEntity call to finalize the connection
                }
            }
        }
    }
}
