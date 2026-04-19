package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.EnemyTag;
import lando.systems.ld59.game.components.Health;
import lando.systems.ld59.game.components.Velocity;

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

        if      (EnemyTag.State.MOVE == enemy.state) move(entity, enemy, delta);
        else if (EnemyTag.State.SHOOT == enemy.state) shoot(entity, enemy, delta);
    }

    private void move(Entity entity, EnemyTag enemy, float delta) {
        var vel = Components.get(entity, Velocity.class);
//        vel.set(vel.x(), vel.y() * 1.01f);
    }

    private void shoot(Entity entity, EnemyTag enemy, float delta) {
        var vel = Components.get(entity, Velocity.class);

        vel.set(0, -20f);
    }
}
