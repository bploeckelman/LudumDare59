package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.assets.anims.AnimBase;
import lando.systems.ld59.assets.anims.AnimBaseCity;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.collision.CollisionMask;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.game.components.renderable.Outline;

import static lando.systems.ld59.game.Constants.*;

public class Base implements Component {

    public static final float ANIM_DEPTH = 100;

    public final Position pos;
    public final Entity ground;
    public final Entity city;
    public final Entity shield;
    public final Collider shieldCollider;

    public Base(Engine engine, Position pos) {
        this.pos = pos;
        this.ground = Factory.createEntity();
        this.city = Factory.createEntity();
        this.shield = Factory.createEntity();

        var groundW = 1080f;
        var groundH = 256f;
        var groundAnim = new Animator(AnimBase.GROUND, new Vector2(groundW / 2, 0));
        groundAnim.depth = ANIM_DEPTH;
        groundAnim.size.set(groundW, groundH);


        var collidesWith = new CollisionMask[] { CollisionMask.ENEMY, CollisionMask.ENEMY_PROJECTILE };
        var groundCollider = Collider.circ(CollisionMask.CITY, 0, -400, 570, collidesWith);

        var cityW = 200f;
        var cityH = 200f;
        var cityAnim = new Animator(AnimBaseCity.IDLE, new Vector2(cityW / 2, 25));
        cityAnim.depth = ANIM_DEPTH + 1;
        cityAnim.size.set(cityW, cityH);

        ground.add(new Position(pos.x, pos.y));
        ground.add(groundAnim);


        city.add(new Position(pos.x, pos.y + 25));
        city.add(cityAnim);
        city.add(new Outline(Color.LIME, Color.CLEAR_WHITE, 2));
        city.add(new Health(CITY_BASE_MAX_HEALTH));
        city.add(groundCollider);


        shieldCollider = Collider.circ(CollisionMask.SHIELD, 0, -350, 730, collidesWith);
        shield.add(new Position(pos.x, pos.y));
        shield.add(shieldCollider);
        shield.add(new Health(SHIELD_MAX_HEALTH));
        shield.add(new CityShield());

        engine.addEntity(ground);
        engine.addEntity(city);
        engine.addEntity(shield);


    }
}
