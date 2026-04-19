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

    public EnemySpawnerSystem() {
        super(Family.all(EnemySpawner.class).get());
        spawnTimer = MathUtils.random(0, 5);
    }

    @Override
    public void update(float delta) {
        spawnTimer += delta;
        super.update(delta);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var spawner = Components.get(entity, EnemySpawner.class);
        var position = Components.get(entity, Position.class);

        float spawnX = position.x + MathUtils.random(-100f, 100f);
        float spawnY = position.y;

        spawnTimer += delta;
        if (spawnTimer < SPAWN_INTERVAL) return;
        var enemy = Factory.enemyShip(spawner.enemyType.get(MathUtils.random(spawner.enemyType.size() - 1)),
            EnergyColor.Type.getRandom(),
            spawnX,
            spawnY,
            0,
            -10f,
            32f
        );
        getEngine().addEntity(enemy);
        spawnTimer = 0f;
    }
}
