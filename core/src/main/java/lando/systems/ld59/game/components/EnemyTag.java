package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.Main;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.collision.CollisionMask;
import lando.systems.ld59.game.components.renderable.Animator;

import static lando.systems.ld59.game.Constants.ENEMY_PROJECTILE_DAMAGE;

public class EnemyTag implements Component {
    public final Entity entity;
    public EnergyColor.Type energyColor;
    public EnemyType type;
    private Engine engine;
    public float FIRE_RATE = 2f;
    public float fireTimer = 0f;
    public float floatTimer = 0f;

    public enum EnemyType {
        FLYER,
        SUICIDER,
        LOVER,
        MUNCHER,
        SUCKER,
        ;

        public static EnemyType getRandom() {
            return values()[MathUtils.random(values().length - 1)];
        }
    }

    public EnemyTag(Entity entity, Engine engine) {
        this.entity = entity;
        this.engine = engine;
    }

    public void shoot() {
        float width = 10f;

        var bullet = Factory.createEntity();
        var baseAnim = new Animator(Main.game.assets.pixelRegion);
        var energyColor = entity.getComponent(EnergyColor.class);
        baseAnim.depth = 100;
        baseAnim.size.set(width, width);
        baseAnim.tint.set(energyColor.getColor());
        baseAnim.origin.set(width / 2f, width / 2f);

        var collidesWith = new CollisionMask[] { CollisionMask.TURRET, CollisionMask.CITY, CollisionMask.SHIELD };
        var bulletCollider = Collider.circ(CollisionMask.ENEMY_PROJECTILE, 0,  0, 2f, collidesWith);

        var pos = entity.getComponent(Position.class);

        bullet.add(new Position(pos.x, pos.y - width));
        bullet.add(baseAnim);
        bullet.add(new Velocity(0, -100));
        bullet.add(new Projectile(ENEMY_PROJECTILE_DAMAGE));
        bullet.add(bulletCollider);
        bullet.add(new Health(1));
        if (energyColor != null) {
            bullet.add(energyColor);
        }

        engine.addEntity(bullet);
    }
}
