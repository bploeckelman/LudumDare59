package lando.systems.ld59.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld59.Main;
import lando.systems.ld59.assets.EmitterType;
import lando.systems.ld59.assets.ImageType;
import lando.systems.ld59.assets.anims.AnimEnemy;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.collision.CollisionMask;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.game.components.renderable.Image;
import lando.systems.ld59.particles.ParticleEffectParams;

public class Factory {

    /**
     * Wrapper for {@link Engine#createEntity()} which ensures all
     * {@link Entity} instances has {@link Id} component attached.
     */
    public static Entity createEntity() {
        var engine = Main.game.engine;
        var entity = engine.createEntity();
        entity.add(new Id());
        return entity;
    }

    public static Entity base(float x, float y) {
        var entity = createEntity();

        var position = new Position(x, y);
        var base = new Base(Main.game.engine, position);

        entity.add(position);
        entity.add(base);
        entity.add(new CityBase());

        return entity;
    }

    public static Entity turret(float x, float y) {
        return turret(x, y, 0f);
    }

    public static Entity turret(float x, float y, float rotation) {
        var entity = createEntity();

        var position = new Position(x, y);
        var turret = new Turret(Main.game.engine, entity, position, rotation);
        entity.add(position);
        entity.add(turret);
        entity.add(new Health(100));

        return entity;
    }

    public static Entity enemySpawner(float x, float y) {
        var entity = createEntity();
        var position = new Position(x, y);
        var spawner = new EnemySpawner();
        entity.add(position);
        entity.add(spawner);
        return entity;
    }

    public static Entity enemyShip(EnemyTag.EnemyType enemy, EnergyColor.Type energyColor, float posX, float posY, float velX, float velY) {
        var entity = createEntity();
        var tag = new EnemyTag(entity);
        AnimEnemy animType = null;
        switch (energyColor) {
            case RED:
                tag.energyColor = EnergyColor.Type.RED;
                animType = AnimEnemy.redShips.get(enemy.ordinal());
                break;
            case GREEN:
                tag.energyColor = EnergyColor.Type.GREEN;
                animType = AnimEnemy.greenShips.get(enemy.ordinal());
                break;
            case BLUE:
                tag.energyColor = EnergyColor.Type.BLUE;
                animType = AnimEnemy.blueShips.get(enemy.ordinal());
                break;
            default:
                tag.energyColor = EnergyColor.Type.RED;
                animType = AnimEnemy.RED_1;
        }
        tag.state = EnemyTag.State.MOVE;
        tag.type = EnemyTag.EnemyType.FLYER;
        var size = 32f;
        var animOrigin = new Vector2(size / 2f, size / 2f);
        var collidesWith = new CollisionMask[] { CollisionMask.SHIELD, CollisionMask.TURRET, CollisionMask.PLAYER_PROJECTILE };

        var name = new Name(energyColor.name() + " " + enemy.name());
        var position = new Position(posX, posY);
        var velocity = new Velocity(velX, velY);
        var animator = new Animator(animType, new Vector2(size, size), animOrigin);
        var collider = Collider.circ(CollisionMask.ENEMY, 0, 0, size / 2f, collidesWith);
        var health = new Health(5);

        entity.add(name);
        entity.add(tag);
        entity.add(position);
        entity.add(velocity);
        entity.add(animator);
        entity.add(collider);
        entity.add(health);
        entity.add(new EnergyColor(energyColor));
        return entity;
    }

    public static Entity baseButton(BaseButton.Type type, float x, float y) {
        var entity = createEntity();

        var size = BaseButton.SIZE;
        var animOrigin = new Vector2(size / 2f, size / 2f);

        var position = new Position(x, y);
        var animator = new Animator(animOrigin);
        var baseButton = new BaseButton(type, entity);
        // NOTE(Brian): Bounds is not Position relative like Collider, Animator
        var bounds = new Bounds(x - size / 2f, y - size / 2f, size, size);

        animator.depth = Base.ANIM_DEPTH + 20;
        animator.size.set(size, size);

        entity.add(position);
        entity.add(animator);
        entity.add(baseButton);
        entity.add(bounds);

        return entity;
    }

    public static Entity view(OrthographicCamera worldCamera) {
        var entity = createEntity();

        var name = new Name("VIEW");
        var viewer = new Viewer(worldCamera);
        var interp = new Interp(1f);

        entity.add(name);
        entity.add(viewer);
        entity.add(interp);

        return entity;
    }

    public static Entity background(ImageType imageType, Vector2 pos, Vector2 size) {
        var entity = createEntity();

        var position = new Position(pos.x, pos.y);

        var region = new TextureRegion(imageType.get());
        var image = new Image(region, size);
        image.tint.a = 0.75f;
        image.depth = -1000;

        entity.add(position);
        entity.add(image);

        return entity;
    }

    public static Entity emitter(EmitterType type, ParticleEffectParams params) {
        var entity = createEntity();

        var name = new Name("emitter-"+type.name().toLowerCase());
        var emitter = new Emitter(type, params);

        entity.add(name);
        entity.add(emitter);

        return entity;
    }
}
