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
        var enemyType = spawner.enemyType.get(MathUtils.random(spawner.enemyType.size() - 1));

        float actualSpawnY = (enemyType == EnemyTag.EnemyType.FLYER) ? spawnY - 80f : spawnY;

        var velX = 0f;
        var velY = -10f;

        var enemy = Factory.enemyShip(enemyType, EnergyColor.Type.getRandom(), spawnX, actualSpawnY, velX, velY);
        getEngine().addEntity(enemy);
        spawner.spawnsLeft--;
        spawner.spawnTimer = spawner.spawnInterval;
        if (spawner.fireOnce || spawner.spawnsLeft <= 0) {
            getEngine().removeEntity(entity);
        }
    }
}
