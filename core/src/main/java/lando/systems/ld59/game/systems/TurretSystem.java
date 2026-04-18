package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.Interp;
import lando.systems.ld59.game.components.Turret;
import lando.systems.ld59.game.components.TurretPattern;
import lando.systems.ld59.game.components.renderable.Animator;

public class TurretSystem extends IteratingSystem {

    public TurretSystem() {
        super(Family.one(Turret.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float dt) {
        var turret = Components.get(entity, Turret.class);

        // update gun rotation from iterp
        var gunAnim = Components.get(turret.cannon, Animator.class);
        var interp = Components.optional(turret.cannon, Interp.class);
        var turretPattern = Components.optional(turret.cannon, TurretPattern.class);
        float targetRotation = 0;
        if (interp.isPresent() && turretPattern.isPresent()) {
            float extents = turretPattern.get().angleExtents();
             targetRotation = -turret.rotation + interp.get().apply(-extents, extents);

        }

        // Lerp cannonRotation toward targetRotation with max angular speed
        float maxDegreesPerSecond = 1000f;
        float maxDelta = maxDegreesPerSecond * dt;

        // Get shortest signed angle difference: [-180, 180]
        float deltaAngle = ((targetRotation - turret.cannonRotation + 540) % 360) - 180;

        // Clamp the step to maxDelta
        float step = MathUtils.clamp(deltaAngle, -maxDelta, maxDelta);

        float dist = Math.abs(turret.cannonRotation - targetRotation);
        if (dist < Math.abs(step)) {
            turret.cannonRotation = targetRotation;
        } else {
            turret.cannonRotation += step;
        }

        gunAnim.rotation = turret.cannonRotation;


    }
}
