package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.collision.CollisionCirc;import lando.systems.ld59.game.components.renderable.Animator;import lando.systems.ld59.game.signals.ConnectionEvent;

public class TurretSystem extends IteratingSystem {


    public float shootDelay = 0.5f;
    public boolean shootThisFrame = false;

    public TurretSystem() {
        super(Family.one(Turret.class).get());
    }

    @Override
    public void update (float dt) {
        shootDelay -=dt;
        shootThisFrame = false;
        if (shootDelay <= 0 ) {
            shootThisFrame = true;
            shootDelay += .5f;
        }
        for (int i = 0; i < getEntities().size(); ++i) {
            processEntity(getEntities().get(i), dt);
        }
    }

    @Override
    protected void processEntity(Entity entity, float dt) {
        var turret = Components.get(entity, Turret.class);

        boolean canShoot = false;

        // update gun rotation from iterp
        var gunAnim = Components.get(turret.cannon, Animator.class);
        var interp = Components.optional(turret.cannon, Interp.class);
        var turretPattern = Components.optional(turret.cannon, TurretPattern.class);
        float targetRotation = 90;
        if (interp.isPresent() && turretPattern.isPresent()) {
            float extents = turretPattern.get().angleExtents();
             targetRotation = turret.rotation + interp.get().apply(-extents, extents);
        }

        // Lerp cannonRotation toward targetRotation with max angular speed
        float maxDegreesPerSecond = 1000f;
        float maxDelta = maxDegreesPerSecond * dt;

        // Get shortest signed angle difference: [-180, 180]
        float deltaAngle = ((targetRotation - turret.cannonRotation + 540) % 360) - 180;

        // Clamp the step to maxDelta
        float step = MathUtils.clamp(deltaAngle, -maxDelta, maxDelta);

        float dist = Math.abs(turret.cannonRotation - targetRotation);
        if (dist <= Math.abs(maxDelta)) {
            turret.cannonRotation = targetRotation;
            canShoot = true;
        } else {
            turret.cannonRotation += step;
        }

        gunAnim.rotation = turret.cannonRotation;

        if (interp.isEmpty() || turretPattern.isEmpty()) {
            // we need to attach something
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
