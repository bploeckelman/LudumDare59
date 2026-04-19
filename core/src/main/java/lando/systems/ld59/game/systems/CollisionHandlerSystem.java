package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import lando.systems.ld59.assets.SoundType;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.collision.CollisionResponse;
import lando.systems.ld59.game.signals.AudioEvent;
import lando.systems.ld59.game.signals.CollisionEvent;
import lando.systems.ld59.game.signals.EntityEvent;
import lando.systems.ld59.game.signals.SignalEvent;
import lando.systems.ld59.utils.FramePool;
import lando.systems.ld59.utils.Util;

import static lando.systems.ld59.game.Constants.ENEMY_RAMMING_DAMAGE;

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

        var turret = Components.has(overlap.entityA(), TurretPart.class) ? overlap.entityA()
                   : Components.has(overlap.entityB(), TurretPart.class) ? overlap.entityB()
                   : null;

        var shield = Components.has(overlap.entityA(), CityShield.class) ? overlap.entityA()
                   : Components.has(overlap.entityB(), CityShield.class) ? overlap.entityB()
                   : null;

        if (bullet != null) {
            var other = bullet == overlap.entityA() ? overlap.entityB() : overlap.entityA();

            if (Components.has(other, Projectile.class)) {
                // both bullets

            }

            var health = Components.get(other, Health.class);
            var bulletDamage = Components.get(bullet, Projectile.class);
            var bulletHealth = Components.get(bullet, Health.class);

            if (health.isDead() || bulletDamage.damage <= 0) {
                // don't let dead things interact
                return;
            }

            var bulletColor = Components.get(bullet, EnergyColor.class);
            var entityColor = Components.get(other, EnergyColor.class);
            var damageMultiplier = 1f;
            if (bulletColor != null && entityColor != null) {
                damageMultiplier = bulletColor.type == entityColor.type ? 3f : 1.0f;
            }
            health.getHit(other, bulletDamage.damage * damageMultiplier);
            bulletDamage.damage = 0f;
            bulletHealth.getHit(bullet, 2f);

        } else if (turret != null && enemy != null) {
            // turret kamikazed
            Components.get(enemy, Health.class).getHit(enemy, 1000f); // kill the enemy
            Components.get(turret, Health.class).getHit(turret, ENEMY_RAMMING_DAMAGE); // do damage to the turret

        } else if (shield != null) {   // shield vs non bullets
            var other = shield == overlap.entityA() ? overlap.entityB() : overlap.entityA();
            Components.get(other, Health.class).getHit(other, 1000f);
            Components.get(shield, Health.class).getHit(shield, ENEMY_RAMMING_DAMAGE);
        } else {
            Util.warn(TAG, "Overlap collision that wasn't handled between: \n\t" + Util.entityString(overlap.entityA()) + " and \n\t" + Util.entityString(overlap.entityB()) + ".");
            Components.get(overlap.entityA(), Health.class).getHit(overlap.entityA(), 100f);
            Components.get(overlap.entityB(), Health.class).getHit(overlap.entityB(), 100f);
        }
    }
}
