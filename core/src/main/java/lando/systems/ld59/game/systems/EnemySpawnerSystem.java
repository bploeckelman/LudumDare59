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
    private static final float SPAWN_INTERVAL = 15f;

    private float spawnTimer = 0f;
    private boolean massSpawn = false;

    public EnemySpawnerSystem() {
        super(Family.all(EnemySpawner.class).get());
    }

    @Override
    public void update(float delta) {
        spawnTimer += delta;
        massSpawn = false;
        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer -= SPAWN_INTERVAL;
            massSpawn = true;
            Util.log("mass spawn");
        }
        super.update(delta);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var spawner = Components.get(entity, EnemySpawner.class);
        var position = Components.get(entity, Position.class);

        boolean shouldSpawn = massSpawn;

        if (!massSpawn) {
            spawner.spawnTimer += delta;
            if (spawner.spawnTimer >= spawner.spawnInterval) {
                spawner.spawnTimer -= spawner.spawnInterval;
                shouldSpawn = true;
            }
        }

        if (shouldSpawn) {
            var massSpawnColor = EnergyColor.Type.getRandom();
            int spawnCount = massSpawn ? 2 : 1;
            for (int i = 0; i < spawnCount; i++) {
                float spawnX = position.x + MathUtils.random(-100f, 100f);
                float spawnY = position.y;

                var enemy = Factory.enemyShip(spawner.enemyType.get(spawner.enemyType.size() - 1),
                    massSpawn ? massSpawnColor : EnergyColor.Type.getRandom(),
                    spawnX,
                    spawnY,
                    0,
                    0
                );

                getEngine().addEntity(enemy);
            }
        }
    }
}
