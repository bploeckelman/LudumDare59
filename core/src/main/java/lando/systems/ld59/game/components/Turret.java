package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.assets.anims.AnimBase;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.renderable.Animator;

public class Turret implements Component {

    public static final float ANIM_DEPTH = Base.ANIM_DEPTH + 10;

    public final Position pos;

    public final Entity base;
    public final Entity gun;

    public Turret(Engine engine, Position pos) {
        this.pos = pos;
        this.base = Factory.createEntity();
        this.gun = Factory.createEntity();

        var width = 200;
        var height = 200;
        var baseAnimator = new Animator(AnimBase.TURRET_BASE, new Vector2(width / 2f, 0));
        var gunAnimator = new Animator(AnimBase.TURRET_CANNON, new Vector2(width / 2f, 0));

        baseAnimator.depth = ANIM_DEPTH + 1;
        gunAnimator.depth = ANIM_DEPTH + 2;
        baseAnimator.size.set(width, height);
        gunAnimator.size.set(width, height);

        base.add(new Position(pos.x, pos.y));
        base.add(baseAnimator);
        gun.add(new Position(pos.x, pos.y));
        gun.add(gunAnimator);

        // TODO: colliders

        engine.addEntity(base);
        engine.addEntity(gun);
    }
}
