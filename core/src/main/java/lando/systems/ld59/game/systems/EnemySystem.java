package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.Config;
import lando.systems.ld59.Main;
import lando.systems.ld59.assets.EmitterType;
import lando.systems.ld59.assets.SoundType;
import lando.systems.ld59.assets.anims.AnimEnemy;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.game.signals.AudioEvent;
import lando.systems.ld59.particles.effects.ShipExplodeEffect;
import lando.systems.ld59.particles.effects.SmokeEffect;
import lando.systems.ld59.screens.EndStoryScreen;
import lando.systems.ld59.screens.EndingScreen;
import lando.systems.ld59.utils.FramePool;
import lando.systems.ld59.utils.Util;

public class EnemySystem extends IteratingSystem {

    public static final Family enemyFamily = Family.one(EnemyTag.class).get();

    private static final String TAG = EnemySystem.class.getSimpleName();

    public EnemySystem() {
        super(Family.one(EnemyTag.class, Boss.class).get());
        transitioning = false;
    }

    @Override
    public void update (float dt) {
        for (int i = getEntities().size() - 1; i >= 0;  i--) {
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
        if (Components.has(entity, Boss.class)) {
            // Boss dead
        } else if (Components.has(entity, EnemyTag.class)) {
            var enemyTag = Components.get(entity, EnemyTag.class);
            getEngine().removeEntity(enemyTag.lightOverlay);
            AudioEvent.playSound(SoundType.EXPLOSION3, .25f);

            var canSplit   = enemyTag.split < enemyTag.MAX_SPLIT;
            var isSplitter = enemyTag.enemyType == EnemyTag.EnemyType.SPLITTER;
            if (isSplitter && canSplit) {
                splitEnemy(entity, enemyTag);
            } else {
                // Trigger explosion effect
                var position = Components.get(entity, Position.class);
                var animator = Components.get(entity, Animator.class);
                getEngine().addEntity(Factory.emitter(EmitterType.SHIP_EXPLODE, new ShipExplodeEffect.Params(position, animator)));

                // Add self-destructing alien entity; flail -> dead -> skeleton -> fade-out -> remove self
                getEngine().addEntity(Factory.alienBody(position.x, position.y));
            }
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
        var anim = Components.get(entity, Animator.class);

        health.update(delta);

        if (health.lastHit < .1f) {
            anim.tint.set(.8f, 0f, 0f, 1f);
        } else {
            anim.tint.set(1f, 1f, 1f, 1f);
        }

        switch (enemy.enemyType) {
            case FLYER:    flyerBehavior(entity, enemy, delta); break;
            case KAMIKAZE: kamikazeBehavior(entity, enemy, delta); break;
            case SPLITTER: splitterBehavior(entity, enemy, delta); break;
        }
    }

    private void splitEnemy(Entity entity, EnemyTag enemyTag) {
        var pos = Components.get(entity, Position.class);
        var anim = Components.get(entity, Animator.class);
        var energyColor = Components.get(entity, EnergyColor.class);

        int numSplits = 2;
        var size = anim.size.x / 2;
        for (int j = 0; j < numSplits; j++) {
            float angle = (360f / numSplits) * j;
            float velX = MathUtils.cosDeg(angle) * 40f;
            float velY = MathUtils.sinDeg(angle) * 20f - 20f; // Spread out and move downward
            float offsetX = MathUtils.random(-10f, 10f);
            float offsetY = MathUtils.random(-10f, 10f);

            var splitEntity = Factory.enemyShip(
                    EnemyTag.EnemyType.SPLITTER,
                    pos.x + offsetX,
                    pos.y + offsetY,
                    velX, velY, size);

            var splitEnemy = Components.get(splitEntity, EnemyTag.class);
            var splitHealth = Components.get(splitEntity, Health.class);

            splitEnemy.split = enemyTag.split + 1;
            splitHealth.maxHealth = 3 - enemyTag.split;
            splitHealth.currentHealth = splitHealth.maxHealth;

            getEngine().addEntity(splitEntity);
        }
    }

    private void kamikazeBehavior(Entity entity, EnemyTag enemy, float delta) {
        var pos = Components.get(entity, Position.class);
        var vel = Components.get(entity, Velocity.class);
        var anim = Components.get(entity, Animator.class);
        var posLights = Components.get(enemy.lightOverlay, Position.class);
        var animLights = Components.get(enemy.lightOverlay, Animator.class);

        float standbyDuration = 3f;
        float appearDuration = 1f;

        enemy.accumTimer += delta;

        if (enemy.accumTimer < appearDuration) {
            float appearProgress = enemy.accumTimer / appearDuration;
            float riseSpeed = 80f * (1f - appearProgress); // Start fast, slow down
            vel.set(0, riseSpeed);

            float scale = 0.3f + (0.7f * appearProgress);
            anim.scale.set(scale, scale);
            anim.tint.a = appearProgress;

            animLights.scale.set(scale, scale);
            animLights.tint.a = appearProgress;
            return;
        }
        anim.scale.set(1f, 1f);
        anim.tint.a = 1f;
        animLights.scale.set(1f, 1f);
        animLights.tint.a = 1f;

        if (enemy.accumTimer > standbyDuration) {
            if (vel.y() >= 0) {
                vel.set(0, -15f);
            }
            vel.set(0f, vel.y() * 1.0035f);
        }

        posLights.set(pos);
    }

    private void flyerBehavior(Entity entity, EnemyTag enemy, float delta) {
        var pos = Components.get(entity, Position.class);
        var vel = Components.get(entity, Velocity.class);
        var anim = Components.get(entity, Animator.class);
        var posLights = Components.get(enemy.lightOverlay, Position.class);
        var animLights = Components.get(enemy.lightOverlay, Animator.class);

        enemy.accumTimer += delta;

        float appearDuration = 1f;
        if (enemy.accumTimer < appearDuration) {
            float appearProgress = enemy.accumTimer / appearDuration;
            float riseSpeed = 80f * (1f - appearProgress); // Start fast, slow down
            vel.set(0, riseSpeed);

            float scale = 0.3f + (0.7f * appearProgress);
            anim.scale.set(scale, scale);
            anim.tint.a = appearProgress;

            animLights.scale.set(scale, scale);
            animLights.tint.a = appearProgress;
            return;
        }

        anim.scale.set(1f, 1f);
        anim.tint.a = 1f;
        animLights.scale.set(1f, 1f);
        animLights.tint.a = 1f;

        if (enemy.driftDirection.isZero()) {
            float angle = MathUtils.random(360f);
            enemy.driftDirection.set(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle));
            enemy.driftDirection.scl(MathUtils.random(30f, 60f));
        }

        enemy.driftChangeTimer += delta;
        float driftChangeInterval = 2f + enemy.randomOffset * 2f;
        if (enemy.driftChangeTimer >= driftChangeInterval) {
            enemy.driftChangeTimer = 0f;
            float angle = MathUtils.random(360f);
            enemy.driftDirection.set(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle));
            enemy.driftDirection.scl(MathUtils.random(30f, 60f));
        }

        float marginX = 50f;
        float marginY = 50f;
        float minX = marginX;
        float maxX = Config.window_width - marginX;
        float minY = Config.window_height / 2 + 50f;
        float maxY = Config.window_height - marginY;

        if (pos.x <= minX && enemy.driftDirection.x < 0) {
            enemy.driftDirection.x = -enemy.driftDirection.x;
        }
        if (pos.x >= maxX && enemy.driftDirection.x > 0) {
            enemy.driftDirection.x = -enemy.driftDirection.x;
        }
        if (pos.y <= minY && enemy.driftDirection.y < 0) {
            enemy.driftDirection.y = -enemy.driftDirection.y;
        }
        if (pos.y >= maxY && enemy.driftDirection.y > 0) {
            enemy.driftDirection.y = -enemy.driftDirection.y;
        }

        float bobX = MathUtils.sin(enemy.accumTimer * (2f + enemy.randomOffset * 0.5f)) * 15f;
        float bobY = MathUtils.cos(enemy.accumTimer * (1.5f + enemy.randomOffset * 0.5f)) * 10f;

        vel.set(enemy.driftDirection.x + bobX, enemy.driftDirection.y + bobY);

        if (enemy.fireTimer >= enemy.FIRE_RATE) {
            enemy.fireTimer -= enemy.FIRE_RATE;
            // Shoot bullet
            getEngine().addEntity(Factory.bullet(entity));
        }
        enemy.fireTimer += delta;

        posLights.set(pos);
    }

    private void splitterBehavior(Entity entity, EnemyTag enemy, float delta) {
        var pos = Components.get(entity, Position.class);
        var vel = Components.get(entity, Velocity.class);
        var anim = Components.get(entity, Animator.class);
        var posLights = Components.get(enemy.lightOverlay, Position.class);
        var animLights = Components.get(enemy.lightOverlay, Animator.class);

        enemy.accumTimer += delta;

        float appearDuration = 1f;

        if (enemy.accumTimer < appearDuration) {
            float appearProgress = enemy.accumTimer / appearDuration;
            float riseSpeed = 80f * (1f - appearProgress); // Start fast, slow down
            vel.set(0, riseSpeed);

            float scale = 0.3f + (0.7f * appearProgress);
            anim.scale.set(scale, scale);
            anim.tint.a = appearProgress;

            animLights.scale.set(1f, 1f);
            animLights.tint.a = 1f;
            return;
        }

        float zapInterval = 5f;
        float teleportDuration = 0.3f;

        enemy.zapTimer += delta;

        float cycleTime = zapInterval + teleportDuration;
        float normalizedTime = (enemy.zapTimer + enemy.randomOffset) % cycleTime;

        if (normalizedTime < teleportDuration / 2f) {
            float disappearProgress = normalizedTime / (teleportDuration / 2f);
            float scale = 1f - disappearProgress;
            anim.scale.set(scale, scale);
            anim.tint.a = scale;
            animLights.scale.set(scale, scale);
            animLights.tint.a = scale;
        } else if (normalizedTime < teleportDuration) {
            if (anim.scale.x < 0.1f) {
                float marginX = Config.window_width / 4f;
                float minX = marginX;
                float maxX = Config.window_width - marginX;
                pos.x = (int) MathUtils.random(minX, maxX);
            }

            float appearProgress = (normalizedTime - teleportDuration / 2f) / (teleportDuration / 2f);
            float scale = appearProgress;
            anim.scale.set(scale, scale);
            anim.tint.a = scale;
            animLights.scale.set(scale, scale);
            animLights.tint.a = scale;
        } else {
            anim.scale.set(1f, 1f);
            anim.tint.a = 1f;
            animLights.scale.set(1f, 1f);
            animLights.tint.a = 1f;
        }

        vel.set(0, -15f);

        posLights.set(pos);
    }

    private float wonDelay = 0;
    private boolean transitioning = false;
    private boolean firstRun = true;
    private void updateBoss(Entity entity, float dt) {
        if (!Components.has(entity, Position.class)) {
            return;
        }
        var boss = Components.get(entity, Boss.class);
        var bossAnim = Components.get(entity, Animator.class);
        if (!boss.isGameOver()) {
            bossAnim.rotation += dt * 30f;
            boss.rotation = bossAnim.rotation;
        }
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

        if (boss.isGameOver()) {
            var isWon = getEngine().getEntitiesFor(Family.one(GameWon.class).get());
            var vel = Components.get(entity, Velocity.class);
            vel.set(0, 0);
            var pos = Components.get(entity, Position.class);
            var tempPos = FramePool.pos(pos);
            float rotation = MathUtils.random(360f);
            float dist = MathUtils.random(200f);
            tempPos.x += dist * MathUtils.cosDeg(rotation);
            tempPos.y += dist * MathUtils.sinDeg(rotation);
            var params = new SmokeEffect.Params(tempPos);
            var emitter = Factory.emitter(EmitterType.SMOKE, params);
            getEngine().addEntity(emitter);

            if (isWon.size() == 0) { // trigger an is won flag
                var won = getEngine().createEntity();
                won.add(new GameWon());
                getEngine().addEntity(won);
            }

            Engine engine = Main.game.engine;
            // kill it all with fire....
            var enemies = engine.getEntitiesFor(enemyFamily);
            for (int i = enemies.size() -1; i>= 0; i--) {
                var isBoss = Components.has(enemies.get(i), Boss.class);
                if (!isBoss) {
                    var enemyTag = Components.get(enemies.get(i), EnemyTag.class);
                    engine.removeEntity(enemyTag.lightOverlay);
                    engine.removeEntity(enemies.get(i));
                }
            }
            wonDelay += dt;
            if (wonDelay > 6f && !transitioning) {
                transitioning = true;
                getEngine().removeAllEntities(Family.one(GameWon.class).get());
                Main.game.setScreen(new EndStoryScreen());

            }
        }
    }
}
