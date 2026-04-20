package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.Config;
import lando.systems.ld59.Main;
import lando.systems.ld59.assets.EmitterType;
import lando.systems.ld59.assets.SoundType;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.collision.CollisionResponse;
import lando.systems.ld59.game.signals.AudioEvent;
import lando.systems.ld59.game.signals.CollisionEvent;
import lando.systems.ld59.game.signals.EntityEvent;
import lando.systems.ld59.game.signals.SignalEvent;
import lando.systems.ld59.particles.effects.SmokeEffect;
import lando.systems.ld59.particles.effects.TestEffect;
import lando.systems.ld59.utils.FramePool;
import lando.systems.ld59.utils.Util;

import static lando.systems.ld59.game.Constants.ENEMY_RAMMING_DAMAGE;
import static lando.systems.ld59.game.Constants.MATCHING_COLOR_DAMAGE_MULTIPLIER;

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

        var city = Components.has(overlap.entityA(), GroundPart.class) ? overlap.entityA()
                   : Components.has(overlap.entityB(), GroundPart.class) ? overlap.entityB()
                   : null;

        if (bullet != null) {
            var other = bullet == overlap.entityA() ? overlap.entityB() : overlap.entityA();

            float panValue = MathUtils.map(
                0, Config.window_width,
                -1, 1,
                Components.get(bullet, Position.class).x);

//            float volumeValue = MathUtils.map(
//                0, Config.window_height,
//                .5f, 1.5f,
//                Components.get(bullet, Position.class).y);

            if (Components.has(other, Projectile.class)) {
                // both bullets
                var pos = Components.get(bullet, Position.class);
                var effectPos = new Position(pos.x, pos.y);
                var params = new SmokeEffect.Params(effectPos);
                var emitter = Factory.emitter(EmitterType.SMOKE, params);
                getEngine().addEntity(emitter);

                AudioEvent.playSound(SoundType.THUD, .5f, panValue);
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
                damageMultiplier = bulletColor.type == entityColor.type ? MATCHING_COLOR_DAMAGE_MULTIPLIER : 1f;
            }


            if (Components.has(other, EnemyTag.class)) {
                // bullet collided with enemy ship
                var pos = Components.get(bullet, Position.class);

                var effectPos = new Position(pos.x, pos.y);
                var params = new SmokeEffect.Params(effectPos);
                var emitter = Factory.emitter(EmitterType.SMOKE, params);
                getEngine().addEntity(emitter);

                AudioEvent.playSound(SoundType.BLIP_HIT, 1.5f);
//                float squareVolume = 0.65f;
//                float sawVolume = 0.8f;
//                float sineVolume = 1f;

//                var energyColor = Components.get(bullet, EnergyColor.class);
//                boolean useFancySounds = energyColor != null;
//                if(useFancySounds) {
//                    switch (energyColor.type) {
//                        case BLUE:
//                            AudioEvent.playSound(
//                                SoundType.getRandomSound(SoundType.cMaj, SoundType.NoteType.SQUARE),
//                                squareVolume,
//                                panValue
//                            );
//                            break;
//                        case GREEN:
//                            AudioEvent.playSound(
//                                SoundType.getRandomSound(SoundType.fMaj, SoundType.NoteType.SAW),
//                                sawVolume,
//                                panValue
//                            );
//                            break;
//                        case RED:
//                            AudioEvent.playSound(
//                                SoundType.getRandomSound(SoundType.gMaj, SoundType.NoteType.SINE),
//                                sineVolume,
//                                panValue
//                            );
//                            break;
//                        default:
//                            break;
//                    }
//                }
//                else {
//                    AudioEvent.playSound(
//                        SoundType.BLIP_HIT,
//                        volumeValue * 1.5f,
//                        panValue
//                    );
//                }


            } else if (Components.has(other, TurretPart.class)) {
                //bullet collided with turret part
//                var pos = Components.get(bullet, Position.class);
//                float panValue = MathUtils.map(
//                    0, Config.window_width,
//                    -1, 1,
//                    pos.x);
                AudioEvent.playSound(SoundType.CLANG, .0625f, panValue);

            } else if (Components.has(other, CityShield.class)) {

                AudioEvent.playSound(SoundType.BOOM);
                //bullet collided with city shield
            }
            health.getHit(other, bulletDamage.damage * damageMultiplier);
            bulletDamage.damage = 0f;
            bulletHealth.getHit(bullet, 2f);

        } else if (turret != null && enemy != null) {
//            AudioEvent.playSound(SoundType.BOOM);
            // turret kamikazed
            Components.get(enemy, Health.class).getHit(enemy, 1000f); // kill the enemy
            Components.get(turret, Health.class).getHit(turret, ENEMY_RAMMING_DAMAGE); // do damage to the turret

        } else if (shield != null) {   // shield vs non bullets
            var other = shield == overlap.entityA() ? overlap.entityB() : overlap.entityA();
            Components.get(other, Health.class).getHit(other, 1000f);
            Components.get(shield, Health.class).getHit(shield, ENEMY_RAMMING_DAMAGE);
            AudioEvent.playSound(SoundType.EXPLOSION2, 0.125f);
        } else if (city != null) {
            // something not a bullet hit the city
            AudioEvent.playSound(SoundType.EXPLOSION2, 0.125f);
            Components.get(city, Health.class).getHit(city, ENEMY_RAMMING_DAMAGE);
            var other = city == overlap.entityA() ? overlap.entityB() : overlap.entityA();
            Components.get(other, Health.class).getHit(other, 1000f);

        } else {
            Util.warn(TAG, "Overlap collision that wasn't handled between: \n\t" + Util.entityString(overlap.entityA()) + " and \n\t" + Util.entityString(overlap.entityB()) + ".");
            Components.get(overlap.entityA(), Health.class).getHit(overlap.entityA(), 100f);
            Components.get(overlap.entityB(), Health.class).getHit(overlap.entityB(), 100f);
        }
    }
}
