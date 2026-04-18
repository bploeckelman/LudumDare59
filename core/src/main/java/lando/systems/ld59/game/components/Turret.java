package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.assets.anims.AnimBase;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.collision.CollisionMask;
import lando.systems.ld59.game.components.renderable.Animator;

public class Turret implements Component {

    public static final float ANIM_DEPTH = Base.ANIM_DEPTH + 10;

    public final Position pos;

    public final Entity base;
    public final Entity cannon;

    public Turret(Engine engine, Position pos) {
        this.pos = pos;
        this.base = Factory.createEntity();
        this.cannon = Factory.createEntity();

        var width = 200;
        var height = 200;
        var baseAnim = new Animator(AnimBase.TURRET_BASE, new Vector2(width / 2f, 0));
        var cannonAnim = new Animator(AnimBase.TURRET_CANNON, new Vector2(width / 2f, 0));
        var baseCollider = Collider.circ(CollisionMask.TURRET, 0, 10, 80);
        var cannonCollider = Collider.circ(CollisionMask.TURRET, 0, 96, 23);

        baseAnim.depth = ANIM_DEPTH + 1;
        cannonAnim.depth = ANIM_DEPTH + 2;
        baseAnim.size.set(width, height);
        cannonAnim.size.set(width, height);

        base.add(new Position(pos.x, pos.y));
        base.add(baseAnim);
        base.add(baseCollider);

        cannon.add(new Position(pos.x, pos.y));
        cannon.add(cannonAnim);
        cannon.add(cannonCollider);

        engine.addEntity(base);
        engine.addEntity(cannon);
    }
}
