package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.EnemySpawner;
import lando.systems.ld59.game.components.EnemyTag;
import lando.systems.ld59.game.components.EnergyColor;
import lando.systems.ld59.game.components.Position;
import lando.systems.ld59.utils.Util;

public class EnemySpawnerSystem extends IteratingSystem {

    private static final String TAG = EnemySpawnerSystem.class.getSimpleName();

    public EnemySpawnerSystem() {
        super(Family.all(EnemySpawner.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var spawner = Components.get(entity, EnemySpawner.class);
        var position = Components.get(entity, Position.class);

        float spawnX = position.x;
        float spawnY = position.y;

        spawner.spawnTimer -= delta;
        if (spawner.spawnTimer > 0) return;
        var enemy = Factory.enemyShip(spawner.enemyType.get(MathUtils.random(spawner.enemyType.size() - 1)),
            EnergyColor.Type.getRandom(),
            spawnX,
            spawnY,
            0,
            -10f,
            32f
        );
        getEngine().addEntity(enemy);
        spawner.spawnTimer = spawner.spawnInterval;
        if (spawner.fireOnce) {
            getEngine().removeEntity(entity);
        }
    }
}
