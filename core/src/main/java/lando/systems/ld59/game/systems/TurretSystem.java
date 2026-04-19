package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.game.signals.ConnectionEvent;
import lando.systems.ld59.utils.Util;

public class TurretSystem extends IteratingSystem {

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

    private Entity loggingEntity = null;

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
            turret.base.remove(Collider.class);
            turret.cannon.remove(Collider.class);

            if (turret.repairTimer < 0) {
                turret.repairTimer = 10f;
            }

            turret.repairTimer -= dt;
            if (turret.repairTimer <= 0) {
                // repair complete
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

        var targetRotation = turret.rotation;
        if (interp.isPresent() && turretPattern.isPresent()) {
            var extents = turretPattern.get().angleExtents();
            targetRotation = turret.rotation + interp.get().apply(-extents, extents);
        }

        // Lerp cannonRotation toward targetRotation with max angular speed
        var maxDegreesPerSecond = 1000f;
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
        } else {
            if (loggingEntity == null) {
                loggingEntity = entity;
            } else {
                if (entity == loggingEntity) {
                    Util.log(Components.get(loggingEntity, Id.class).toString(), "cannonRotation: " + turret.cannonRotation.floatValue());
                }
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
}
