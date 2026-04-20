package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.AnimDepths;
import lando.systems.ld59.assets.anims.AnimBaseCity;
import lando.systems.ld59.assets.anims.AnimBaseGround;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.collision.CollisionMask;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.game.components.renderable.Outline;
import lando.systems.ld59.game.components.renderable.ShieldShaderRenderable;

import static lando.systems.ld59.game.Constants.CITY_BASE_MAX_HEALTH;
import static lando.systems.ld59.game.Constants.SHIELD_MAX_HEALTH;

public class Base implements Component {

    public final Entity entity;
    public final Position pos;
    public final Entity ground;
    public final Entity city;
    public final Entity shield;
    public final Collider shieldCollider;

    public Base(Engine engine, Entity entity, Position pos) {
        this.entity = entity;
        this.pos = pos;
        this.ground = Factory.createEntity();
        this.city = Factory.createEntity();
        this.shield = Factory.createEntity();

        var groundW = 1080f;
        var groundH = 320f;
        var groundAnim = new Animator(AnimBaseGround.IDLE, new Vector2(groundW / 2, 0));
        groundAnim.depth = AnimDepths.GROUND;
        groundAnim.size.set(groundW, groundH);

        var collidesWith = new CollisionMask[] { CollisionMask.ENEMY, CollisionMask.ENEMY_PROJECTILE };
        var groundCollider = Collider.circ(CollisionMask.CITY, 0, -400, 570, collidesWith);

        var cityW = 200f;
        var cityH = 200f;
        var cityAnim = new Animator(AnimBaseCity.IDLE, new Vector2(cityW / 2, 25));
        cityAnim.depth = AnimDepths.CITY;
        cityAnim.size.set(cityW, cityH);

        ground.add(new Position(pos.x, pos.y));
        ground.add(groundAnim);

        city.add(new Position(pos.x, pos.y + 25));
        city.add(cityAnim);
        city.add(new Outline(Color.LIME, Color.CLEAR_WHITE, 2));
        city.add(new Health(CITY_BASE_MAX_HEALTH));
        city.add(groundCollider);
        city.add(new GroundPart(entity));

        shieldCollider = Collider.circ(CollisionMask.SHIELD, 0, -350, 730, collidesWith);
        shield.add(new Position(pos.x, pos.y));
        shield.add(shieldCollider);
        shield.add(new Health(SHIELD_MAX_HEALTH));
        shield.add(new CityShield());
        shield.add(new ShieldShaderRenderable());

        engine.addEntity(ground);
        engine.addEntity(city);
        engine.addEntity(shield);
    }

    public void handleHit() {
        var animHit = AnimBaseGround.HIT;
        var animIdle = AnimBaseGround.IDLE;

        var animator = Components.get(ground, Animator.class);
        animator.start(animHit);

        // Revert to idle animation after hit animation completes
        var duration = animHit.get().getAnimationDuration();
        entity.add(new Timer(entity, duration, () -> {
            animator.start(animIdle);
            entity.remove(Timer.class);
        }));
    }
}
