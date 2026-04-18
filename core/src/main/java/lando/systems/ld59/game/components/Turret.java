package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.assets.anims.AnimBase;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.collision.CollisionMask;
import lando.systems.ld59.game.components.renderable.Animator;

public class Turret implements Component {

    public static final float ANIM_DEPTH = Base.ANIM_DEPTH + 10;

    public final float rotation;
    public float cannonRotation;
    public final Position pos;

    public final Entity base;
    public final Entity cannon;

    public Turret(Engine engine, Position pos, float rotation) {
        this.pos = pos;
        this.rotation = rotation;
        this.base = Factory.createEntity();
        this.cannon = Factory.createEntity();

        var width = 200;
        var height = 200;
        var baseAnim = new Animator(AnimBase.TURRET_BASE, new Vector2(width / 2f, 0));
        var cannonAnim = new Animator(AnimBase.TURRET_CANNON, new Vector2(width / 2f, 0));
        var baseCollider = Collider.circ(CollisionMask.TURRET, 0, 10, 80);
        var cannonCollider = Collider.circ(CollisionMask.TURRET, 0,  96, 23);

        baseAnim.depth = ANIM_DEPTH + 1;
        cannonAnim.depth = ANIM_DEPTH + 2;
        baseAnim.size.set(width, height);
        cannonAnim.size.set(width, height);
        baseAnim.rotation = -rotation;
        cannonAnim.rotationOrigin.set(width / 2f, height / 2f);

        base.add(new Position(pos.x, pos.y));
        base.add(baseAnim);
        base.add(baseCollider);

        cannon.add(new Position(pos.x + MathUtils.sinDeg(rotation) * 96, pos.y - 96 + MathUtils.cosDeg(rotation) * 96 ));
        cannon.add(cannonAnim);
        cannon.add(cannonCollider);
        cannon.add(new Interp(1f, Interpolation.linear, Interp.Repeat.PINGPONG));

        //DEBUG
        TurretPattern.Type[] values = TurretPattern.Type.values();
        connectPattern(new TurretPattern(values[MathUtils.random(values.length-1)]));

        engine.addEntity(base);
        engine.addEntity(cannon);
    }

    public void shoot() {

    }

    public void connectPattern(TurretPattern pattern) {
        // remove old pattern, add new one
        cannon.remove(TurretPattern.class);
        cannon.add(pattern);

        // reset the interpolation
        cannon.remove(Interp.class);
        cannon.add(pattern.getInterp());
    }
}
