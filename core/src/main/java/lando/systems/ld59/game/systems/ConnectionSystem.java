package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.BaseButton;
import lando.systems.ld59.game.components.Connection;
import lando.systems.ld59.game.components.Turret;
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
        } else if (connection.isConnected()) {
            if (connection.ropePath != null) {
                connection.ropePath.update(deltaTime);
            }
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

                    // remove other connection to basebutton if exists
                    cleanUpReplacedConnections(pendingConnection);
                }
            }
        }
        else if (event instanceof ConnectionEvent.TouchedBaseButton) {
            var baseButton = ((ConnectionEvent.TouchedBaseButton) event).baseButton();

            // Create a pending connection if there isn't one...
            if (pendingConnection == null) {
                var entity = Factory.createEntity();
                var pConn = Connection.createPending(entity, baseButton);
                entity.add(pConn);
                getEngine().addEntity(entity);

                // remove the connection in the basebutton
                cleanUpReplacedConnections(pConn);
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

                    cleanUpReplacedConnections(pendingConnection);

                    // ...and allowing the next processEntity call to finalize the connection
                }
            }
        }
    }

    public Array<Entity> getConnectionEntityWithTurret(Turret turret) {
        Array<Entity> connectedEntities = new Array<>();
        var entities = getEntities();
        for (int i = 0; i < entities.size(); i++) {
            var connection = Components.get(entities.get(i), Connection.class);
            if (connection.isConnected() && connection.getTurret() == turret) {
                connectedEntities.add(entities.get(i));
            }
        }
        return connectedEntities;
    }

    public Array<Entity>  getConnectionEntityWithBaseButton(BaseButton baseButton) {
        Array<Entity> connectedEntities = new Array<>();
        var entities = getEntities();
        for (int i = 0; i < entities.size(); i++) {
            var connection = Components.get(entities.get(i), Connection.class);
            if (connection.isConnected() && connection.getBaseButton() == baseButton) {
                connectedEntities.add(entities.get(i));
            }
        }
        return connectedEntities;
    }

    public void cleanUpReplacedConnections(Connection pendingConnection) {
        if (pendingConnection.hasTurret() && pendingConnection.hasBaseButton()) {
            var baseButtonConnections = getConnectionEntityWithBaseButton(pendingConnection.getBaseButton());
            for (int i = 0; i < baseButtonConnections.size; i++) {
                var connection  = Components.get(baseButtonConnections.get(i), Connection.class);
                connection.removeConnection();
                EntityEvent.remove(baseButtonConnections.get(i));
            }
            var turretConnections = getConnectionEntityWithTurret(pendingConnection.getTurret());
            for (int i = 0; i < turretConnections.size; i++) {
                if (pendingConnection.getBaseButton().isEnergy()) {
                    var connection  = Components.get(turretConnections.get(i), Connection.class);
                    if (connection.getBaseButton().isEnergy()) {
                        connection.removeConnection();
                        EntityEvent.remove(turretConnections.get(i));
                    }
                } else {
                    // pattern
                    var connection  = Components.get(turretConnections.get(i), Connection.class);
                    if (connection.getBaseButton().isPattern()) {
                        connection.removeConnection();
                        EntityEvent.remove(turretConnections.get(i));
                    }
                }
            }
        } else if (pendingConnection.hasBaseButton()){
            var baseButtonConnections = getConnectionEntityWithBaseButton(pendingConnection.getBaseButton());
            for (int i = 0; i < baseButtonConnections.size; i++) {
                var connection  = Components.get(baseButtonConnections.get(i), Connection.class);
                connection.removeConnection();
                EntityEvent.remove(baseButtonConnections.get(i));
            }
        } else {
            // shouldn't be calling this when it isn't about to replace a connection
        }
    }
}
