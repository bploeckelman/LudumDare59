package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.Config;
import lando.systems.ld59.Main;
import lando.systems.ld59.assets.anims.AnimEnemy;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.renderable.Animator;

import java.util.ArrayList;
import java.util.List;

public class WaveScheduleSystem extends IteratingSystem {

    public Entity boss;

    public static class WaveEvent {
        public float time;
        public Runnable action;

        public WaveEvent(float time, Runnable action) {
            this.time = time;
            this.action = action;
        }
    }

    private float elapsedTime = 0f;
    private int currentWaveIndex = 0;
    private List<WaveEvent> waves = new ArrayList<>();

    public WaveScheduleSystem() {
        super(Family.all(SceneContainer.class).get());
        setupWaves();
        this.boss = Main.game.engine.createEntity();
        boss.add(new Boss(this.boss));
        boss.add(new Health(20));
        Main.game.engine.addEntity(boss);
    }

    private void setupWaves() {
        var centerX = Config.window_width / 2f;
        var topY = Config.window_height - 50f;
        var customY = 3 * topY / 4f;


        // Wave 1 (1 second in)
        waves.add(new WaveEvent(1f, () -> {
            var spawner = Factory.enemySpawner(centerX, customY, List.of(EnemyTag.EnemyType.FLYER));
            getEngine().addEntity(spawner);
        }));

        // Wave 1.5 (5 second in)
        waves.add(new WaveEvent(5f, () -> {
            for (int i = 0; i < 3; i++) {
                var x = Config.window_width / 4f * (i + 1);
                var spawner = Factory.enemySpawner(x, customY + 50f, List.of(EnemyTag.EnemyType.FLYER));
                getEngine().addEntity(spawner);
            }
        }));


        // Wave 2 (10 seconds)
        waves.add(new WaveEvent(10f, () -> {
            for (int i = 0; i < 3; i++) {
                var x = Config.window_width / 4f * (i + 1);
                var spawner = Factory.enemySpawner(x, topY, List.of(EnemyTag.EnemyType.KAMIKAZE));
                getEngine().addEntity(spawner);
            }
        }));

        // Wave 3 (15 seconds)
        waves.add(new WaveEvent(15f, () -> {
            for (int i = 0; i < 6; i++) {
                var x = Config.window_width / 7f * (i + 1);
                var spawner = Factory.enemySpawner(x, topY, List.of(EnemyTag.EnemyType.KAMIKAZE));
                getEngine().addEntity(spawner);
            }
        }));

        // Wave 4 (20 seconds)
        waves.add(new WaveEvent(20f, () -> {
            for (int i = 0; i < 5; i++) {
                var x = Config.window_width / 6f * (i + 1);
                var spawner = Factory.enemySpawner(x, topY, List.of(EnemyTag.EnemyType.SPLITTER));
                getEngine().addEntity(spawner);
            }
        }));

        // Wave 5 (30 seconds and keeps going)
        waves.add(new WaveEvent(30f, () -> {
            for (int i = 0; i < 7; i++) {
                var x = Config.window_width / 8f * (i + 1);
                var spawner = Factory.enemySpawner(x, topY, List.of(
                    EnemyTag.EnemyType.KAMIKAZE,
                    EnemyTag.EnemyType.SPLITTER
                ));
                var spawnerComponent = Components.get(spawner, EnemySpawner.class);
                spawnerComponent.fireOnce = false;
                spawnerComponent.spawnInterval = 10f;
                spawnerComponent.spawnsLeft = 20;
                getEngine().addEntity(spawner);
            }

            for (int i = 0; i < 3; i++) {
                var x = Config.window_width / 4f * (i + 1);
                var spawner = Factory.enemySpawner(x, customY + 50f, List.of(EnemyTag.EnemyType.FLYER));
                var spawnerComponent = Components.get(spawner, EnemySpawner.class);
                spawnerComponent.fireOnce = false;
                spawnerComponent.spawnInterval = 5f;
                spawnerComponent.spawnsLeft = 20;
                getEngine().addEntity(spawner);
            }

        }));

        waves.add(new WaveEvent(120f, () -> {
            float width = 300;
            var pos = new Position(Config.window_width / 3f, Config.window_height + 100);
            var bossComp = Components.get(boss, Boss.class);
            bossComp.addPosition(pos);
            boss.add(pos);
            boss.add(new Velocity(0, 0));
            var bossAnim = new Animator(AnimEnemy.BOSS, new Vector2(width / 2f, width / 2f));
            bossAnim.depth = 100;
            bossAnim.size.set(width, width);
            boss.add(bossAnim);
            // Anything else that needs to happen after the boss is spawned
        }));



    }

    @Override
    public void update(float delta) {
        elapsedTime += delta;

        // Process all waves that should have triggered by now
        while (currentWaveIndex < waves.size() &&
               elapsedTime >= waves.get(currentWaveIndex).time) {
            waves.get(currentWaveIndex).action.run();
            currentWaveIndex++;
        }

        super.update(delta);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        // No per-entity processing needed
    }

    // Optional: Reset the wave schedule (useful for restarting the game)
    public void reset() {
        elapsedTime = 0f;
        currentWaveIndex = 0;
    }
}
