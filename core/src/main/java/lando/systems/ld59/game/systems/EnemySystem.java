package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.assets.SoundType;
import lando.systems.ld59.assets.anims.AnimEnemy;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.game.signals.AudioEvent;
import lando.systems.ld59.utils.Util;

public class EnemySystem extends IteratingSystem {

    private static final String TAG = EnemySystem.class.getSimpleName();

    public EnemySystem() {
        super(Family.one(EnemyTag.class, Boss.class).get());
    }

    @Override
    public void update (float dt) {
        for (int i = getEntities().size() - 1; i>= 0;  i--) {
            var entity = getEntities().get(i);
            var health = Components.get(entity, Health.class);
            if (health != null && health.isDead()) {
                handleEnemyDeath(entity);
                getEngine().removeEntity(entity);
                continue;
            }
            processEntity(entity, dt);
        }
    }

    private void handleEnemyDeath(Entity entity) {
        var enemy = Components.get(entity, EnemyTag.class);
        if (Components.has(entity, Boss.class)) {
            // Boss dead
        }
        if (Components.has(entity, EnemyTag.class)) {
            if (enemy.enemyType == EnemyTag.EnemyType.SPLITTER && enemy.split < enemy.MAX_SPLIT) {
                var pos = Components.get(entity, Position.class);
                var anim = Components.get(entity, Animator.class);
                var energyColor = Components.get(entity, EnergyColor.class);

                int numSplits = 2;
                var size = anim.size.x / 2;
                for (int j = 0; j < numSplits; j++) {
                    float angle = (360f / numSplits) * j;
                    float velX = MathUtils.cosDeg(angle) * 40f;
                    float velY = MathUtils.sinDeg(angle) * 20f - 20f; // Spread out and move downward

                    var split = Factory.enemyShip(
                        EnemyTag.EnemyType.SPLITTER,
                        energyColor.type,
                        pos.x + MathUtils.random(-10f, 10f),
                        pos.y + MathUtils.random(-10f, 10f),
                        velX,
                        velY,
                        size
                    );
                    var splitEnemy = Components.get(split, EnemyTag.class);
                    splitEnemy.split = enemy.split + 1;
                    var splitHealth = Components.get(split, Health.class);
                    splitHealth.maxHealth = 3 - enemy.split;
                    splitHealth.currentHealth = splitHealth.maxHealth;

                    getEngine().addEntity(split);
                }
            }
            AudioEvent.playSound(SoundType.EXPLOSION3, .25f);
        }
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        if (Components.has(entity, Boss.class)) {
            // Handle boss logic
            updateBoss(entity, delta);
            return;
        }
        var enemy = Components.get(entity, EnemyTag.class);
        var health = Components.get(entity, Health.class);
        health.update(delta);
        var anim = Components.get(entity, Animator.class);
        if (health.lastHit < .1f) {
            anim.tint.set(.8f, 0f, 0f, 1f);
        } else {
            anim.tint.set(1f, 1f, 1f, 1f);
        }

        if      (EnemyTag.EnemyType.KAMIKAZE == enemy.enemyType) suicider(entity, enemy, delta);
        else if (EnemyTag.EnemyType.FLYER == enemy.enemyType) flyer(entity, enemy, delta);
        else if (EnemyTag.EnemyType.SPLITTER == enemy.enemyType) splitter(entity, enemy, delta);
    }



    private void suicider(Entity entity, EnemyTag enemy, float delta) {
        var pos = Components.get(entity, Position.class);
        var vel = Components.get(entity, Velocity.class);
        float standbyDuration = 3f;
        float appearDuration = 1f;
        var anim = Components.get(entity, Animator.class);

        enemy.accumTimer += delta;

        if (enemy.accumTimer < appearDuration) {
            float appearProgress = enemy.accumTimer / appearDuration;
            float riseSpeed = 80f * (1f - appearProgress); // Start fast, slow down
            vel.set(0, riseSpeed);

            float scale = 0.3f + (0.7f * appearProgress);
            anim.scale.set(scale, scale);

            anim.tint.a = appearProgress;
            return;
        }
        anim.scale.set(1f, 1f);
        anim.tint.a = 1f;
        if (enemy.accumTimer > standbyDuration) {
            if (vel.y() >= 0) {
                vel.set(0, -15f);
            }
            vel.set(0f, vel.y() * 1.0035f);
        }
    }

    private void flyer(Entity entity, EnemyTag enemy, float delta) {
        var pos = Components.get(entity, Position.class);
        var vel = Components.get(entity, Velocity.class);
        var anim = Components.get(entity, Animator.class);

        enemy.accumTimer += delta;

        float appearDuration = 1f;
        if (enemy.accumTimer < appearDuration) {
            float appearProgress = enemy.accumTimer / appearDuration;
            float riseSpeed = 80f * (1f - appearProgress); // Start fast, slow down
            vel.set(0, riseSpeed);

            float scale = 0.3f + (0.7f * appearProgress);
            anim.scale.set(scale, scale);

            anim.tint.a = appearProgress;
            return;
        }

        anim.scale.set(1f, 1f);
        anim.tint.a = 1f;

        float bobSpeed = MathUtils.sin(enemy.accumTimer * 2f) * 15f;
        float driftSpeed = MathUtils.sin(enemy.accumTimer * 1.5f) * 10f;

        vel.set(driftSpeed, bobSpeed);

        if (enemy.fireTimer > enemy.FIRE_RATE) {
            enemy.shoot();
            enemy.fireTimer = 0f;
        }
        enemy.fireTimer += delta;

    }

    private void splitter(Entity entity, EnemyTag enemy, float delta) {
        var pos = Components.get(entity, Position.class);
        var vel = Components.get(entity, Velocity.class);
        var anim = Components.get(entity, Animator.class);

        enemy.accumTimer += delta;

        float appearDuration = 1f;

        if (enemy.accumTimer < appearDuration) {
            float appearProgress = enemy.accumTimer / appearDuration;
            float riseSpeed = 80f * (1f - appearProgress); // Start fast, slow down
            vel.set(0, riseSpeed);

            float scale = 0.3f + (0.7f * appearProgress);
            anim.scale.set(scale, scale);

            anim.tint.a = appearProgress;
            return;
        }

        anim.rotation += delta * 360f;
        float driftSpeed = MathUtils.sin(enemy.accumTimer * 1.5f) * 20f;
        vel.set(driftSpeed, -10f);
    }

    private void updateBoss(Entity entity, float dt) {
        if (!Components.has(entity, Position.class)) {
            return;
        }
        var boss = Components.get(entity, Boss.class);
        var bossAnim = Components.get(entity, Animator.class);
        bossAnim.rotation += dt * 30f;
        boss.rotation = bossAnim.rotation;
        boss.update(dt);
        if (boss.areAllGemsDead()) {
            if (boss.finalGem == null) {
                // Set up final boss gem
                var finalGem = getEngine().createEntity();
                boss.finalGem = finalGem;
                var gemAnim = new Animator(AnimEnemy.GEM);
                gemAnim.depth = 120;
                gemAnim.size.set(60, 60);
                gemAnim.origin.set(30, 30);

                finalGem.add(gemAnim);
                finalGem.add(new Health(20));
                finalGem.add(boss.bossCollider);
                finalGem.add(new Position(boss.bossPos));

                getEngine().addEntity(finalGem);
            }

        }
    }
}
