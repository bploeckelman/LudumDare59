package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;

import java.util.List;

public class EnemySpawner implements Component {

    public boolean fireOnce = true;
    public float spawnInterval;
    public int spawnsLeft;
    public float spawnTimer;
    public List<EnemyTag.EnemyType> enemyType;

    public EnemySpawner(List<EnemyTag.EnemyType> enemyType) {
        this.spawnInterval = 4f;
        this.spawnTimer = 0f;
        this.spawnsLeft = 1;
        this.enemyType = enemyType;
    }
}
