package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;

public class EnemySpawner implements Component {

    public int x;
    public int y;
    public float spawnInterval;
    public float spawnTimer;

    public EnemySpawner(int x, int y) {
        this.x = x;
        this.y = y;
        this.spawnInterval = 2f; // 2 seconds
        this.spawnTimer = 0f;
    }
}
