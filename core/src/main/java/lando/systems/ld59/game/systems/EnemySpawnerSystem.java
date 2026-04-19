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

public class EnemySpawnerSystem extends IteratingSystem {

    private static final String TAG = EnemySpawnerSystem.class.getSimpleName();

    public EnemySpawnerSystem() {
        super(Family.all(EnemySpawner.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var spawner = Components.get(entity, EnemySpawner.class);

        spawner.spawnTimer += delta;

        if (spawner.spawnTimer >= spawner.spawnInterval) {
            spawner.spawnTimer -= spawner.spawnInterval;

            // Spawn enemy at random x position near spawner
            float spawnX = spawner.x + MathUtils.random(-100f, 100f);
            float spawnY = spawner.y;
            float velX = MathUtils.random(-20f, 20f);
            float velY = -10f;

            var enemy = Factory.enemyShip(
                EnemyTag.EnemyType.getRandom(),
                EnergyColor.Type.getRandom(),
                spawnX, spawnY,
                velX, velY
            );

            getEngine().addEntity(enemy);
        }
    }
}
