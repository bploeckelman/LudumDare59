package lando.systems.ld59.game.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.assets.anims.AnimHero;
import lando.systems.ld59.game.Constants;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.collision.CollisionMask;
import lando.systems.ld59.game.components.renderable.Animator;

import static lando.systems.ld59.game.Factory.createEntity;

public class CharFactory {

    // ------------------------------------------------------------------------
    // Player character
    // ------------------------------------------------------------------------

    public static Entity player(TilemapObject.Spawner spawner) {
        var entity = Factory.createEntity();

        entity.add(new Player());
        entity.add(new Name("PLAYER"));
        entity.add(new Input());
        entity.add(new Position(spawner));
        entity.add(new Velocity());
        entity.add(new Friction());
        entity.add(new Gravity());
        entity.add(new Cooldowns()
            .add("jump", 0.2f)
            .add("taunt", 0.2f));

        var animBounds = Constants.BILLY_ANIMATOR_BOUNDS;
        var animOrigin = animBounds.getPosition(new Vector2());
        var animator = new Animator(AnimHero.IDLE, animOrigin);
        animator.size.set(animBounds.width, animBounds.height);
        animator.depth = 1;
        entity.add(animator);

        var collidesWith   = new CollisionMask[] { CollisionMask.SOLID, CollisionMask.ENEMY };
        entity.add(Collider.rect(CollisionMask.PLAYER, Constants.BILLY_COLLIDER_BOUNDS, collidesWith));

        return entity;
    }

    // ------------------------------------------------------------------------
    // Enemy characters
    // ------------------------------------------------------------------------

    public static Entity enemy(TilemapObjectType.Enemies type, TilemapObject.Spawner spawner) {
        var entity = createEntity();

        var tag = new EnemyTag();
        var name = new Name(spawner.type);
        var position = new Position(spawner);
        var velocity = new Velocity();
        var friction = new Friction();
        var gravity = new Gravity();

        entity.add(tag);
        entity.add(name);
        entity.add(position);
        entity.add(velocity);
        entity.add(friction);
        entity.add(gravity);

        // Customizations by type
        // TODO: review and update anim, collider bounds
        switch (type) {
//            case ANGRY_SUN: {
//                entity.add(new EnemyAngrySun());
//                entity.add(new KirbyPower(KirbyPower.PowerType.SUN));
//
//                var animOrigin = new Vector2(16, 12);
//                var anim = new Animator(AnimType.ANGRY_SUN, animOrigin);
//                anim.depth = 1000;
//                entity.add(anim);
//
//                var radius = 8f;
//                var collidesWith  = new CollisionMask[] { CollisionMask.PLAYER };
//                entity.add(Collider.circ(CollisionMask.ENEMY, 0, 0, radius, collidesWith));
//            } break;
        }

        return entity;
    }
}
