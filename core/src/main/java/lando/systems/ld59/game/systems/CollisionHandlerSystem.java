package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.collision.CollisionResponse;
import lando.systems.ld59.game.signals.CollisionEvent;
import lando.systems.ld59.game.signals.SignalEvent;
import lando.systems.ld59.utils.FramePool;
import lando.systems.ld59.utils.Util;

public class CollisionHandlerSystem extends EntitySystem implements Listener<SignalEvent> {

    private static final String TAG = CollisionHandlerSystem.class.getSimpleName();

    public CollisionHandlerSystem() {
        SignalEvent.addListener(this);
    }

    @Override
    public void receive(Signal<SignalEvent> signal, SignalEvent event) {
        if (event instanceof CollisionEvent) {
            if      (event instanceof CollisionEvent.Move)    handleMoveCollision((CollisionEvent.Move) event);
            else if (event instanceof CollisionEvent.Overlap) handleOverlapCollision((CollisionEvent.Overlap) event);
            else Util.warn(TAG, "unhandled collision event type: " + event.getClass().getSimpleName());
        }
    }

    private void handleMoveCollision(CollisionEvent.Move move) {
        // Player/Enemy collision
//        if ((Components.has(move.mover(),  Player.class) && Components.hasEnemyComponent(move.target()))
//         || (Components.has(move.target(), Player.class) && Components.hasEnemyComponent(move.mover()))) {
//            handlePlayerEnemyCollision(move);
//        } else {
//            Util.warn(TAG, "Move collision that wasn't handled");
//        }
    }

    private void handleOverlapCollision(CollisionEvent.Overlap overlap) {
        var bullet = Components.has(overlap.entityA(), Projectile.class) ? overlap.entityA()
                   : Components.has(overlap.entityB(), Projectile.class) ? overlap.entityB()
                   : null;
        var enemy = Components.has(overlap.entityA(), EnemyTag.class) ? overlap.entityA()
                   : Components.has(overlap.entityB(), EnemyTag.class) ? overlap.entityB()
                   : null;

        if (bullet != null && enemy != null) {

            var enemyHealth = Components.get(enemy, Health.class);
            var bulletDamage = Components.get(bullet, Projectile.class);

            if (enemyHealth.isDead() || bulletDamage.damage <= 0) {
                // don't let dead things interact
                return;
            }

            var bulletColor = Components.get(bullet, EnergyColor.class);
            var enemyColor = Components.get(enemy, EnergyColor.class);
            var damageMultiplier = 1f;
            if (bulletColor != null && enemyColor != null) {
                damageMultiplier = bulletColor.type == enemyColor.type ? 3f : 1.0f;
            }
            enemyHealth.damage(bulletDamage.damage * damageMultiplier);
            bulletDamage.damage = 0;

        } else {
            Util.warn(TAG, "Overlap collision that wasn't handled");
        }
    }
}
