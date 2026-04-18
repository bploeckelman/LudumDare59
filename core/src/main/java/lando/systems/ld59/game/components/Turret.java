package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.assets.anims.AnimCockpit;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.renderable.Animator;

public class Turret implements Component {

    public static final float ANIM_DEPTH = 100;

    public final Position pos;

    public final Entity base;
    public final Entity gun;

    public Turret(Engine engine, Position pos) {
        this.pos = pos;
        this.base = Factory.createEntity();
        this.gun = Factory.createEntity();

        var w = 16;
        var h = 16;
        var offset = 30;
        var baseAnimator = new Animator(AnimCockpit.TURRET_GUN_BASE_1, new Vector2(w / 2f, h / 2f));
        var gunAnimator = new Animator(AnimCockpit.TURRET_GUN_1, new Vector2(w / 2f, h / 2f));

        baseAnimator.depth = ANIM_DEPTH + 1;
        gunAnimator.depth = ANIM_DEPTH + 2;
        baseAnimator.size.set(w, h);
        gunAnimator.size.set(w, h);

        base.add(new Position(pos.x, pos.y + offset));
        base.add(baseAnimator);
        gun.add(new Position(pos.x, pos.y + offset));
        gun.add(gunAnimator);

        engine.addEntity(base);
        engine.addEntity(gun);
    }
}
