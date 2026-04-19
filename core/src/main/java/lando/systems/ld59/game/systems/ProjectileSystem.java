package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.Health;
import lando.systems.ld59.game.components.Projectile;

public class ProjectileSystem  extends IteratingSystem  {

    public ProjectileSystem() {
       super(Family.one(Projectile.class).get(), 0);
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
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
