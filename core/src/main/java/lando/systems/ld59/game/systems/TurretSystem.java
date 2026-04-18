package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.Interp;
import lando.systems.ld59.game.components.Turret;
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
        if (interp.isPresent()) {
            gunAnim.rotation = -turret.rotation + interp.get().apply(-45, 45);
        } else {
            gunAnim.rotation = turret.rotation;
        }


    }
}
