package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.Main;
import lando.systems.ld59.assets.anims.AnimBaseTurret;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.game.signals.ConnectionEvent;
import lando.systems.ld59.game.signals.SignalEvent;
import lando.systems.ld59.utils.FramePool;import static lando.systems.ld59.game.Constants.CITY_TURRET_REPAIR_TIME;

public class TurretSystem extends IteratingSystem  {

    public float shotInterval = 0.125f;
    public float shootDelay = 0.5f;
    public boolean shootThisFrame = false;
    public int shotCount = 0;

    public TurretSystem() {
        super(Family.one(Turret.class).get());
    }

    @Override
    public void update (float dt) {
        shootDelay -=dt;
        shootThisFrame = false;
        while (shootDelay <= 0 ) {
            shootThisFrame = true;
            shootDelay += shotInterval;
            shotCount++;
        }
        for (int i = 0; i < getEntities().size(); ++i) {
            processEntity(getEntities().get(i), dt);
        }
    }

    @Override
    protected void processEntity(Entity entity, float dt) {
        var turret = Components.get(entity, Turret.class);

        boolean canShoot = false;

        var health = Components.get(entity, Health.class);
        var gunAnim = Components.get(turret.cannon, Animator.class);
        var baseAnim = Components.get(turret.base, Animator.class);
        var interp = Components.optional(turret.cannon, Interp.class);
        var turretPattern = Components.optional(turret.cannon, TurretPattern.class);

        health.update(dt);
        if (health.isDead()) {
            baseAnim.start(AnimBaseTurret.BASE_DAMAGED);

            turret.base.remove(Collider.class);
            turret.cannon.remove(Collider.class);

            if (turret.repairTimer < 0) {
                turret.repairTimer = CITY_TURRET_REPAIR_TIME;
            }

            turret.repairTimer -= dt;
            if (turret.repairTimer <= 0) {
                // repair complete
                baseAnim.start(AnimBaseTurret.BASE_IDLE);
                health.currentHealth = health.maxHealth;
                turret.base.add(turret.baseCollider);
                turret.cannon.add(turret.cannonCollider);
            }

            if (health.lastHit % .5f < .1f) {
                gunAnim.tint.set(.5f, .5f, .5f, .5f);
            } else {
                gunAnim.tint.set(.6f, .6f, .6f, .7f);
            }

            baseAnim.tint.set(.6f, .6f, .6f, .7f);

            // don't update anything else
            return;
        }
        baseAnim.tint.set(Color.WHITE);
        var maxDegreesPerSecond = 10000f;
        var targetRotation = turret.rotation;
        if (interp.isPresent() && turretPattern.isPresent()) {
            if (turretPattern.get().type == TurretPattern.Type.LINE) {
                maxDegreesPerSecond = 100f;

                var cannonPos = FramePool.pos(Components.get(turret.cannon, Position.class));

                var closestPos = nearestEnemy(cannonPos);
                if (closestPos == null) {
                    targetRotation = turret.rotation;
                } else {
                    targetRotation = MathUtils.atan2Deg(closestPos.y - cannonPos.y, closestPos.x - cannonPos.x);
                }
                var extents = turretPattern.get().angleExtents();
                targetRotation = MathUtils.clamp(targetRotation, turret.rotation - extents, turret.rotation + extents);
            } else {
                var extents = turretPattern.get().angleExtents();
                targetRotation = turret.rotation + interp.get().apply(-extents, extents);
            }
        }

        // Lerp cannonRotation toward targetRotation with max angular speed

        var maxDelta = maxDegreesPerSecond * dt;

        // Get shortest signed angle difference: [-180, 180]
        float deltaAngle = ((targetRotation - turret.cannonRotation.floatValue() + 540) % 360) - 180;

        // Clamp the step to maxDelta
        float step = MathUtils.clamp(deltaAngle, -maxDelta, maxDelta);

        if (!turret.swappingCannonBarrel) {
            float dist = Math.abs(turret.cannonRotation.floatValue() - targetRotation);
            if (dist <= Math.abs(maxDelta)) {
                turret.cannonRotation.setValue(targetRotation);
                canShoot = true;
            } else {
                turret.cannonRotation.setValue(turret.cannonRotation.floatValue() + step);
            }
        }

        gunAnim.rotation = turret.cannonRotation.floatValue();
        if (health.lastHit < .1f) {
            gunAnim.tint.set(.8f, .4f, .4f, 1f);
            baseAnim.tint.set(.8f, .4f, .4f, 1f);
        } else {
            gunAnim.tint.set(1f, 1f, 1f, 1f);
            baseAnim.tint.set(1f, 1f, 1f, 1f);
        }

        if (interp.isEmpty() || turretPattern.isEmpty()) {
            // we need to attach something
            return;
        }

        if (shotCount % turretPattern.get().getShotMod() != 0) {
            canShoot = false;
        }

        if (shootThisFrame && canShoot)  {
            turret.shoot();
        }
    }

    public boolean handleTouchUp(float worldX, float worldY, int pointer, int button) {
        // NOTE(Brian): I remember for-each not playing nice with the getEntities() collection type, hence normal for loop
        var entities = getEntities();
        for (int i = 0; i < entities.size(); i++) {
            var entity = entities.get(i);

            var turret = Components.get(entity, Turret.class);
            var turretBaseColliderCircle = turret.getBaseCollisionCircle();
            var turretCannonColliderCircle = turret.getCannonCollisionCircle();
            var isTouched = turretBaseColliderCircle.contains(worldX, worldY)
                    || turretCannonColliderCircle.contains(worldX, worldY);

            if (isTouched) {
                ConnectionEvent.touchedTurret(turret);
            }
        }
        return false;
    }

    public Position nearestEnemy(Position tPos) {
        var enemies = Main.game.engine.getEntitiesFor(Family.one(EnemyTag.class, Gem.class).get());
        float closestDist = Float.MAX_VALUE;
        Position closestPos = null;
        for (int i = 0; i < enemies.size(); i++) {
            var enemy = enemies.get(i);
            var enemyPos = Components.get(enemy, Position.class);
            if (enemyPos == null) continue;
            var dist = enemyPos.dst2(tPos);
            if (dist < closestDist) {
                closestDist = dist;
                closestPos = enemyPos;
            }
        }
        return closestPos;
    }
}
