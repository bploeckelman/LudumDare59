package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.renderable.Animator;

public class EnemySystem extends IteratingSystem {

    private static final String TAG = EnemySystem.class.getSimpleName();

    public EnemySystem() {
        super(Family.one(EnemyTag.class).get());
    }

    @Override
    public void update (float dt) {
        for (int i = getEntities().size() - 1; i>= 0;  i--) {
            var entity = getEntities().get(i);
            var health = Components.get(entity, Health.class);
            if (health.isDead()) {
                getEngine().removeEntity(entity);
                continue;
            }
            processEntity(entity, dt);
        }
    }

    @Override
    protected void processEntity(Entity entity, float delta) {

        var enemy = Components.get(entity, EnemyTag.class);
        var health = Components.get(entity, Health.class);
        health.update(delta);
        var anim = Components.get(entity, Animator.class);
        if (health.lastHit < .1f) {
            anim.tint.set(.8f, 0f, 0f, 1f);
        } else {
            anim.tint.set(1f, 1f, 1f, 1f);
        }

        if      (EnemyTag.EnemyType.SUICIDER == enemy.type) suicider(entity, enemy, delta);
        else if (EnemyTag.EnemyType.FLYER == enemy.type) flyer(entity, enemy, delta);
    }

    private void suicider(Entity entity, EnemyTag enemy, float delta) {
        var pos = Components.get(entity, Position.class);
        var vel = Components.get(entity, Velocity.class);

        vel.set(0f, -20f);
    }

    private void flyer(Entity entity, EnemyTag enemy, float delta) {
        var pos = Components.get(entity, Position.class);
        var vel = Components.get(entity, Velocity.class);

        vel.set(0f, 0f);
        if (enemy.fireTimer > enemy.FIRE_RATE) {
            enemy.shoot();
            enemy.fireTimer = 0f;
        }
        enemy.fireTimer += delta;

    }
}
