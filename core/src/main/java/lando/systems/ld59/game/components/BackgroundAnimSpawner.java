package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;

public class BackgroundAnimSpawner implements Component {
    public float spawnTimer;
    public float spawnInterval;

    public BackgroundAnimSpawner() {
        this.spawnInterval = 7f;
        this.spawnTimer = spawnInterval;
    }
}
