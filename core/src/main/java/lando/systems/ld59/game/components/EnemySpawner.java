package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;

public class EnemySpawner implements Component {

    public float spawnInterval;
    public float spawnTimer;

    public EnemySpawner() {
        this.spawnInterval = 5f;
        this.spawnTimer = 0f;
    }
}
