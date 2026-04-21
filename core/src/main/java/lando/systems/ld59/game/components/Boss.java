package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld59.Config;
import lando.systems.ld59.Main;
import lando.systems.ld59.assets.anims.AnimEnemy;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.collision.CollisionMask;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.utils.FramePool;
import lando.systems.ld59.utils.Util;

public class Boss implements Component {
    public static int GEMS = 6;
    public Array<Entity> gems = new Array<>();
    public Entity finalGem;
    public float rotation;
    public Position bossPos;

    public Collider bossCollider;

    public Position bossTarget = new Position();
    public Entity boss;

    public float accum;

    public float atLocationTimer = 10f;

    public Boss(Entity boss) {
        this.boss = boss;
        bossTarget.set(Config.window_width/2f, Config.window_height - 150f);
        bossCollider = Collider.circ(CollisionMask.ENEMY, 0, 0, 40, new CollisionMask[] { CollisionMask.PLAYER_PROJECTILE });
        float deltaDeg = 360f / GEMS;
        for (int i = 0; i < GEMS; i++) {
            var gem = Main.game.engine.createEntity();
            var energyColor = new EnergyColor(EnergyColor.Type.BLUE);
            switch (i%3) {
                case 0:
                    energyColor = new EnergyColor(EnergyColor.Type.BLUE);
                    break;
                case 1:
                    energyColor = new EnergyColor(EnergyColor.Type.GREEN);
                    break;
                case 2:
                    energyColor = new EnergyColor(EnergyColor.Type.RED);
                    break;
            }
            gem.add(new Gem(deltaDeg*i));
            gem.add(new Position(-100, -100));
            gem.add(new Health(25));
            var collidesWith = new CollisionMask[] { CollisionMask.PLAYER_PROJECTILE };
            var gemCollider = Collider.circ(CollisionMask.ENEMY, 0,  0, 25, collidesWith);

            gem.add(gemCollider);
            gem.add(energyColor);
            var gemAnim = new Animator(AnimEnemy.GEM);
            gemAnim.depth = 120;
            gemAnim.size.set(50, 50);
            gemAnim.origin.set(25, 25);
            gemAnim.tint.set(energyColor.getColor());
            gem.add(gemAnim);
            gems.add(gem);

            Main.game.engine.addEntity(gem);
        }
    }

    public void addPosition(Position pos) {
        bossPos = pos;
    }

    public void update(float delta) {
        accum += delta;
        // update Gem positions
        if (bossPos == null) return;
        for (int i = 0; i < gems.size; i++) {
            var gem = gems.get(i);
            var pos = gem.getComponent(Position.class);
            var gemComp = gem.getComponent(Gem.class);
            pos.x = bossPos.x + (int)(MathUtils.cosDeg(rotation + gemComp.angle ) * 130f);
            pos.y = bossPos.y + (int)(MathUtils.sinDeg(rotation + gemComp.angle) * 130f);

            var health = gem.getComponent(Health.class);
            health.update(delta);
            var gemAnim = gem.getComponent(Animator.class);

            if (health.lastHit < .1f) {
                gemAnim.tint.set(.8f, .4f, .4f, 1f);
            } else {
                var energyColor = gem.getComponent(EnergyColor.class);
                gemAnim.tint.set(energyColor.getColor());
            }
            if (health.isDead()) {
                gem.remove(Collider.class);
                gemAnim.tint.a = .25f;
            }
        }

        if (finalGem != null) {
            var pos = finalGem.getComponent(Position.class);
            pos.x = bossPos.x;
            pos.y = bossPos.y;
            var gemAnim = finalGem.getComponent(Animator.class);
            gemAnim.rotation = rotation;
            gemAnim.rotationOrigin.set(gemAnim.size.x/2f, gemAnim.size.y/2f);

            var gemHealth = finalGem.getComponent(Health.class);
            gemHealth.update(delta);
            if (gemHealth.lastHit < .1f) {
                gemAnim.tint.set(.8f, .4f, .4f, 1f);
            } else {
                Util.hsvToRgb(accum * .2f, .4f, 1f, gemAnim.tint);
            }
            if (gemHealth.isDead()) {
                finalGem.remove(Collider.class);
                gemAnim.tint.a = .25f;
            }
        }
        if (!isGameOver()) {
            var moveSpeed = 100f;
            var vel = Components.get(boss, Velocity.class);
            var tempVec2 = FramePool.vec2();
            tempVec2.set(bossTarget.x - bossPos.x, bossTarget.y - bossPos.y).nor();
            vel.set(tempVec2.x * moveSpeed, tempVec2.y * moveSpeed);
            if (bossPos.dst(bossTarget) < 10f) {
                atLocationTimer -= delta;
                if (atLocationTimer <= 0) {
                    atLocationTimer = MathUtils.random(1f, 5f);
                    bossTarget.x = MathUtils.random(300, Config.window_width - 300);
                }
            }
        }
    }

    public boolean areAllGemsDead() {
        boolean allDead = true;
        for (int i = 0; i < gems.size; i++) {
            var gem = gems.get(i);
            var health = gem.getComponent(Health.class);
            if (!health.isDead()) {
                allDead = false;
                break;
            }
        }
        return allDead;
    }

    public boolean isGameOver() {
        if (finalGem == null) return false;
        var finalGemHealth = finalGem.getComponent(Health.class);
        return finalGemHealth.isDead();
    }
}
