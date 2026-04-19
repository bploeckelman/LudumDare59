package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.assets.anims.AnimBase;
import lando.systems.ld59.assets.anims.AnimBaseButton;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.collision.CollisionCirc;
import lando.systems.ld59.game.components.collision.CollisionMask;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.utils.FramePool;

public class Turret implements Component {

    public static final float size = 200f;

    public static final float ANIM_DEPTH = Base.ANIM_DEPTH + 10;

    public final float rotation;
    public float cannonRotation;

    private Engine engine;

    public final Entity entity;
    public final Position pos;

    public final Entity base;
    public final Entity cannon;

    public Turret(Engine engine, Entity entity, Position pos, float rot) {
        this.engine = engine;
        this.entity = entity;
        this.pos = pos;
        this.rotation = rot;
        this.base = Factory.createEntity();
        this.cannon = Factory.createEntity();

        var width = size;
        var height = size;
        var baseAnim = new Animator(AnimBase.TURRET_BASE, new Vector2(0, width / 2f));
        var cannonAnim = new Animator(AnimBase.TURRET_CANNON, new Vector2(0, width / 2f));
        var baseCollider = Collider.circ(CollisionMask.TURRET, 0, 10, 80);
        var cannonCollider = Collider.circ(CollisionMask.TURRET, 96,  0, 23);

        baseAnim.depth = ANIM_DEPTH + 1;
        cannonAnim.depth = ANIM_DEPTH + 2;
        baseAnim.size.set(width, height);
        cannonAnim.size.set(width, height);
        baseAnim.rotation = rotation;
        cannonAnim.rotationOrigin.set(width / 2f, height / 2f);

        base.add(new Position(pos.x, pos.y));
        base.add(baseAnim);
        base.add(baseCollider);

        cannon.add(new Position(pos.x -96 + MathUtils.cosDeg(rot) * 96, pos.y + MathUtils.sinDeg(rot) * 96 ));
        cannon.add(cannonAnim);
        cannon.add(cannonCollider);
        cannon.add(new Interp(1f, Interpolation.linear, Interp.Repeat.PINGPONG));

        //DEBUG
        TurretPattern.Type[] values = TurretPattern.Type.values();
        connectPattern(new TurretPattern(values[MathUtils.random(values.length-1)]));

        EnergyColor.Type[] colors = EnergyColor.Type.values();
        connectEnergy(new EnergyColor(colors[MathUtils.random(colors.length-1)]));

        // END DEBUG

        engine.addEntity(base);
        engine.addEntity(cannon);
    }

    public void shoot() {
        float width = 20f;

        var bullet = Factory.createEntity();
        var cannonPos = cannon.getComponent(Position.class);
        var pos = new Position(cannonPos.x + 100, cannonPos.y );
        Vector2 tempVec = FramePool.vec2();
        tempVec.set(60, 0);
        tempVec.rotateDeg(cannonRotation);
        pos.add((int) tempVec.x, (int)tempVec.y);


        float totalRotation = cannonRotation;
        var vel = new Velocity(MathUtils.cosDeg(totalRotation) * 100, MathUtils.sinDeg(totalRotation) * 100);

        var baseAnim = new Animator(AnimBaseButton.BLUE_ON, new Vector2(width / 2f, width / 2f));
        baseAnim.depth = 100;
        baseAnim.size.set(width, width);

        var bulletCollider = Collider.circ(CollisionMask.PLAYER_PROJECTILE, 0,  0, width/2f);
        bulletCollider.collidesWith(CollisionMask.ENEMY);

        var energyColor = cannon.getComponent(EnergyColor.class);

        bullet.add(pos);
        bullet.add(baseAnim);
        bullet.add(vel);
        bullet.add(new Projectile(4));
        bullet.add(bulletCollider);
        if (energyColor != null) {
            bullet.add(energyColor);
        }

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
            return;
        }
        cannon.add(color);
    }

    public Color getCannonColor() {
        return Components.optional(cannon, EnergyColor.class)
                .map(energy -> {
                    var color = FramePool.color();
                    switch (energy.type) {
                        case RED: color.set(1, 0, 0, 1); break;
                        case GREEN: color.set(0, 1, 0, 1); break;
                        case BLUE: color.set(0, 0, 1, 1); break;
                    }
                    return color;
                })
                .orElse(FramePool.color(1, 1, 1));
    }

    public Circle getBaseCollisionCircle() {
        var collider = Components.get(base, Collider.class);
        var pos = Components.get(base, Position.class);
        var shape = collider.shape(CollisionCirc.class);
        return shape.circle(pos);
    }

    public Circle getCannonCollisionCircle() {
        var collider = Components.get(cannon, Collider.class);
        var pos = Components.get(cannon, Position.class);
        var shape = collider.shape(CollisionCirc.class);
        return shape.circle(pos);
    }
}
