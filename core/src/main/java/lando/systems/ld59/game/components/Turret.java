package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.Main;
import lando.systems.ld59.assets.SoundType;
import lando.systems.ld59.assets.anims.AnimBaseTurret;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.collision.CollisionCirc;
import lando.systems.ld59.game.components.collision.CollisionMask;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.game.components.renderable.Outline;
import lando.systems.ld59.game.signals.AudioEvent;
import lando.systems.ld59.utils.FramePool;

import static lando.systems.ld59.game.Constants.PLAYER_PROJECTILE_DAMAGE;

public class Turret implements Component {

    public static final float size = 200f;

    public static final float ANIM_DEPTH = Base.ANIM_DEPTH + 10;
    public static final CollisionMask[] COLLIDES_WITH = new CollisionMask[] {
            CollisionMask.ENEMY, CollisionMask.ENEMY_PROJECTILE
    };

    public final float rotation;
    public float cannonRotation;
    public float repairTimer;

    private Engine engine;

    public final Entity entity;
    public final Position pos;

    public final Entity base;
    public final Entity cannon;

    public final Collider baseCollider;
    public final Collider cannonCollider;
    private final Outline baseOutline;
    private final Outline cannonOutline;

    public Turret(Engine engine, Entity entity, Position pos, float rot, Health turretHealth) {
        this.engine = engine;
        this.entity = entity;
        this.pos = pos;
        this.rotation = rot;
        this.base = Factory.createEntity();
        this.cannon = Factory.createEntity();
        this.baseOutline    = new Outline(Color.MAGENTA, Color.CLEAR_WHITE, 2f);
        this.cannonOutline  = new Outline(Color.MAGENTA, Color.CLEAR_WHITE, 4f);
        this.baseCollider   = Collider.circ(CollisionMask.TURRET, 0, 10, 80, COLLIDES_WITH);
        this.cannonCollider = Collider.circ(CollisionMask.TURRET, 96,  0, 23, COLLIDES_WITH);

        var width = size;
        var height = size;

        // Cannon sits 'behind' base so that it can retract...

        var cannonAnim = new Animator(AnimBaseTurret.CANNON_BARREL_A, new Vector2(0, width / 2f));
        cannonAnim.depth = ANIM_DEPTH + 1;
        cannonAnim.size.set(width, height);
        cannonAnim.rotationOrigin.set(width / 2f, height / 2f);

        var baseAnim = new Animator(AnimBaseTurret.BASE, new Vector2(0, width / 2f));
        baseAnim.depth = ANIM_DEPTH + 2;
        baseAnim.size.set(width, height);
        baseAnim.rotation = rotation;

        cannon.add(turretHealth);
        cannon.add(cannonAnim);
        cannon.add(cannonOutline);
        cannon.add(cannonCollider);
        cannon.add(new TurretPart());
        cannon.add(new Position(
                pos.x - 96 + MathUtils.cosDeg(rot) * 96,
                pos.y + MathUtils.sinDeg(rot) * 96));
        cannon.add(new Interp(1f, Interpolation.linear, Interp.Repeat.PINGPONG));

        base.add(turretHealth);
        base.add(baseAnim);
        base.add(baseOutline);
        base.add(baseCollider);
        base.add(new TurretPart());
        base.add(new Position(pos.x, pos.y));

        engine.addEntity(base);
        engine.addEntity(cannon);
    }

    public void shoot() {
        float width = 10f;

        var bullet = Factory.createEntity();
        var cannonPos = cannon.getComponent(Position.class);
        var energyColor = cannon.getComponent(EnergyColor.class);
        if (energyColor == null) {
            // no energy, no bullet
            return;
        }

        var pos = new Position(cannonPos.x + 100, cannonPos.y );
        Vector2 tempVec = FramePool.vec2();
        tempVec.set(60, 0);
        tempVec.rotateDeg(cannonRotation);
        pos.add((int) tempVec.x, (int)tempVec.y);


        float totalRotation = cannonRotation;
        var vel = new Velocity(MathUtils.cosDeg(totalRotation) * 100, MathUtils.sinDeg(totalRotation) * 100);

        var baseAnim = new Animator(Main.game.assets.pixelRegion);
        baseAnim.depth = 100;
        baseAnim.size.set(width, width);
        baseAnim.tint.set(energyColor.getColor());
        baseAnim.origin.set(width / 2f, width / 2f);


        var collidesWith = new CollisionMask[] { CollisionMask.ENEMY, CollisionMask.ENEMY_PROJECTILE };
        var bulletCollider = Collider.circ(CollisionMask.PLAYER_PROJECTILE, 0,  0, width/2f, collidesWith);

        bullet.add(pos);
        bullet.add(baseAnim);
        bullet.add(vel);
        bullet.add(new Projectile(PLAYER_PROJECTILE_DAMAGE));
        bullet.add(bulletCollider);
        bullet.add(new Health(1));
        if (energyColor != null) {
            bullet.add(energyColor);
        }
        AudioEvent.playSound(SoundType.LASER, .5f);

        engine.addEntity(bullet);
    }

    /**
     * Connects a new pattern to the turret. Can be null to remove a pattern
     * @param pattern The new pattern to connect.
     */
    public void connectPattern(TurretPattern pattern) {
        // remove old pattern, add new one
        cannon.remove(TurretPattern.class);
        if (pattern != null) {
            cannon.add(pattern);
        }

        // reset the interpolation
        cannon.remove(Interp.class);
        if (pattern != null) {
            cannon.add(pattern.getInterp());
        }
    }

    /**
     * Connects a new color to the turret. Can be null to remove a color
     * @param color the new color to connect
     */
    public void connectEnergy(EnergyColor color) {
        cannon.remove(EnergyColor.class);
        if (color == null) {
            baseOutline.outlineColor(Color.CLEAR_WHITE);
            cannonOutline.outlineColor(Color.CLEAR_WHITE);
            return;
        }
        cannon.add(color);

        baseOutline.outlineColor(color.getColor());
        cannonOutline.outlineColor(color.getColor());
    }

    public Color getCannonColor() {
        return Components.optional(cannon, EnergyColor.class)
                .map(EnergyColor::getColor)
                .orElse(FramePool.color(1, 1, 1));
    }

    public Circle getBaseCollisionCircle() {
        var pos = Components.get(base, Position.class);
        var shape = baseCollider.shape(CollisionCirc.class);
        return shape.circle(pos);
    }

    public Circle getCannonCollisionCircle() {
        var pos = Components.get(cannon, Position.class);
        var shape = cannonCollider.shape(CollisionCirc.class);
        return shape.circle(pos);
    }
}
