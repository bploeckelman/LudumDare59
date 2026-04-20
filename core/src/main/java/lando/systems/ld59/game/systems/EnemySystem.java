package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.renderable.Animator;

public class EnemySystem extends IteratingSystem {

    private static final String TAG = EnemySystem.class.getSimpleName();

    public EnemySystem() {
        super(Family.one(EnemyTag.class).get());
    }

    @Override
    public void update (float dt) {
        for (int i = getEntities().size() - 1; i>= 0;  i--) {
            var entity = getEntities().get(i);
            var health = Components.get(entity, Health.class);
            if (health.isDead()) {
                handleEnemyDeath(entity);
                getEngine().removeEntity(entity);
                continue;
            }
            processEntity(entity, dt);
        }
    }

    private void handleEnemyDeath(Entity entity) {
        var enemy = Components.get(entity, EnemyTag.class);

        if (enemy.type == EnemyTag.EnemyType.SPLITTER && enemy.split < enemy.MAX_SPLIT) {
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
    }

    @Override
    protected void processEntity(Entity entity, float delta) {

        var enemy = Components.get(entity, EnemyTag.class);
        var health = Components.get(entity, Health.class);
        health.update(delta);
        var anim = Components.get(entity, Animator.class);
        if (health.lastHit < .1f) {
            anim.tint.set(.8f, 0f, 0f, 1f);
        } else {
            anim.tint.set(1f, 1f, 1f, 1f);
        }

        if      (EnemyTag.EnemyType.SUICIDER == enemy.type) suicider(entity, enemy, delta);
        else if (EnemyTag.EnemyType.FLYER == enemy.type) flyer(entity, enemy, delta);
        else if (EnemyTag.EnemyType.SPLITTER == enemy.type) splitter(entity, enemy, delta);
    }

    private void suicider(Entity entity, EnemyTag enemy, float delta) {
        var pos = Components.get(entity, Position.class);
        var vel = Components.get(entity, Velocity.class);

        vel.set(0f, vel.y() * 1.0025f);
    }

    private void flyer(Entity entity, EnemyTag enemy, float delta) {
        var pos = Components.get(entity, Position.class);
        var vel = Components.get(entity, Velocity.class);

        enemy.floatTimer += delta;
        float bobSpeed = MathUtils.sin(enemy.floatTimer * 2f) * 15f;
        float driftSpeed = MathUtils.sin(enemy.floatTimer * 1.5f) * 10f;

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

        enemy.floatTimer += delta;
        anim.rotation += delta * 360f;
        float driftSpeed = MathUtils.sin(enemy.floatTimer * 1.5f) * 20f;
        vel.set(driftSpeed, -10f);
    }
}
