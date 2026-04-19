package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;

import java.util.List;

public class EnemySpawner implements Component {

    public float spawnInterval;
    public float spawnTimer;
    public List<EnemyTag.EnemyType> enemyType;

    public EnemySpawner(List<EnemyTag.EnemyType> enemyType) {
        this.spawnInterval = 4f;
        this.spawnTimer = 0f;
        this.enemyType = enemyType;
    }
}
