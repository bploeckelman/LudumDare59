package lando.systems.ld59.game;

import aurelienribon.tweenengine.Tween;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld59.AnimDepths;import lando.systems.ld59.Main;
import lando.systems.ld59.assets.EmitterType;
import lando.systems.ld59.assets.ImageType;
import lando.systems.ld59.assets.anims.AnimBaseButton;
import lando.systems.ld59.assets.anims.AnimEnemy;
import lando.systems.ld59.assets.anims.AnimMisc;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.collision.CollisionMask;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.game.components.renderable.Image;
import lando.systems.ld59.particles.ParticleEffectParams;
import lando.systems.ld59.utils.Callbacks;
import lando.systems.ld59.utils.accessors.ColorAccessor;

import java.util.List;

import static lando.systems.ld59.game.Constants.*;

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
        var base = new Base(Main.game.engine, entity, position);

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

        var health = new Health(TURRET_MAX_HEALTH);
        var turret = new Turret(Main.game.engine, entity, position, rotation, health);

        entity.add(position);
        entity.add(turret);
        entity.add(health);

        return entity;
    }

    public static Entity alienBody(float x, float y) {
        var entity = Factory.createEntity();

        var animator = new Animator(AnimEnemy.ALIEN_FLAIL);
        animator.depth = AnimDepths.SHIPS - 10;

        // 1. flail -> dead
        // 2. dead -> skeleton
        // 3. skeleton -> fade-out
        // 4. fade-out -> remove entity
        var timer = new Timer(entity, 3f);
        timer.onEnd = new Callbacks.NoArg() {
            int state = 1;
            @Override
            public void run() {
                switch (state) {
                    case 1: {
                        state = 2;
                        animator.start(AnimEnemy.getRandomDeadAlien());
                        timer.start(2f);
                    } break;
                    case 2: {
                        state = 3;
                        animator.start(AnimMisc.SKELETON);
                        timer.start(2f);
                    } break;
                    case 3: {
                        state = 4;
                        Tween.to(animator.tint, ColorAccessor.A, 1f).target(0f).start(Main.game.tween);
                        timer.start(1f);
                    } break;
                    case 4: {
                        Main.game.engine.removeEntity(entity);
                    } break;
                }
            }
        };

        entity.add(new Position(x, y));
        entity.add(new Velocity(MathUtils.random(-10f, 10f), MathUtils.random(-10f, 10f)));
        entity.add(animator);
        entity.add(timer);

        return entity;
    }

    public static Entity bullet(Entity enemyShip) {
        var defaultDiameter = 10f;
        return bullet(enemyShip, defaultDiameter);
    }

    public static Entity bullet(Entity enemyShip, float diameter) {
        var entity = Factory.createEntity();

        var enemyShipPos = enemyShip.getComponent(Position.class);
        var enemyShipEnergyColor = enemyShip.getComponent(EnergyColor.class);
        var radius = diameter / 2f;

        var baseAnim = new Animator(AnimMisc.PROJECTILE);
        baseAnim.depth = AnimDepths.BULLETS;
        baseAnim.size.set(diameter, diameter);
        baseAnim.origin.set(radius, radius);
        baseAnim.tint.set(enemyShipEnergyColor.getColor());

        var collidesWith = new CollisionMask[] { CollisionMask.TURRET, CollisionMask.CITY, CollisionMask.SHIELD };
        var bulletCollider = Collider.circ(CollisionMask.ENEMY_PROJECTILE, 0,  0, 2f, collidesWith);

        entity.add(baseAnim);
        entity.add(bulletCollider);
        entity.add(enemyShipEnergyColor);
        entity.add(new Position(enemyShipPos.x, enemyShipPos.y - diameter));
        entity.add(new Velocity(0, -100));
        entity.add(new Projectile(ENEMY_PROJECTILE_DAMAGE));
        entity.add(new Health(1));

        return entity;
    }

    public static Entity enemySpawner(float x, float y, List<EnemyTag.EnemyType> enemyType) {
        var entity = createEntity();
        var position = new Position(x, y);
        var spawner = new EnemySpawner(enemyType);
        entity.add(position);
        entity.add(spawner);
        return entity;
    }

    public static Entity enemyShip(EnemyTag.EnemyType enemyType, EnergyColor.Type energyColorType, float posX, float posY, float velX, float velY) {
        var animEnemy = AnimEnemy.of(energyColorType);
        // NOTE: all AnimEnemy ship types have the same size / dimensions 80x80
        var size = animEnemy.get().getKeyFrame(0f).getRegionWidth();
        return enemyShip(enemyType, energyColorType, posX, posY, velX, velY, size);
    }

    public static Entity enemyShip(EnemyTag.EnemyType enemyType, EnergyColor.Type energyColorType, float posX, float posY, float velX, float velY, float size) {
        var entity = createEntity();

        var position = new Position(posX, posY);
        var animator = new Animator(AnimEnemy.of(energyColorType), new Vector2(size, size), new Vector2(size / 2f, size / 2f));
        animator.scale.set(0.3f, 0.3f);
        animator.tint.a = 0f;
        var collidesWith = new CollisionMask[] { CollisionMask.SHIELD, CollisionMask.TURRET, CollisionMask.PLAYER_PROJECTILE, CollisionMask.CITY };
        var collider = Collider.circ(CollisionMask.ENEMY, 0, 0, size / 3f, collidesWith);

        var enemyTag = new EnemyTag(Main.game.engine, entity, position, animator, enemyType, energyColorType);

        entity.add(enemyTag);
        entity.add(animator);
        entity.add(collider);
        entity.add(position);
        entity.add(new Velocity(velX, velY));
        entity.add(new Health(ENEMY_MAX_HEALTH));
        entity.add(new EnergyColor(energyColorType));
        entity.add(new Name(energyColorType.name() + " " + enemyType.name()));

        return entity;
    }

    public static Entity baseButtonBoard(AnimBaseButton animType, float leftOrRightEdge, float yCenter) {
        var entity = createEntity();

        var isRight = animType == AnimBaseButton.BOARD_RIGHT;
        var isLeft = animType == AnimBaseButton.BOARD_LEFT;
        if (!isLeft && !isRight) {
            throw new GdxRuntimeException("Button board animation must be AnimBaseButton.[BOARD_LEFT|BOARD_RIGHT], got: " + animType.name());
        }

        var keyframe = animType.get().getKeyFrame(0f);
        var width = keyframe.getRegionWidth();
        var height = keyframe.getRegionHeight();
        var x = leftOrRightEdge + (isRight ? -width : 0);
        var y = yCenter - height / 2f;

        var position = new Position(x, y);
        var animator = new Animator(animType, new Vector2(0, 0));
        animator.depth = AnimDepths.BUTTONS - 10;

        entity.add(position);
        entity.add(animator);

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

        // NOTE: buttons are rotated so that they 'push in' to the appropriate side, left or right
        animator.depth = AnimDepths.BUTTONS;
        animator.size.set(size, size);
        animator.rotationOrigin.set(size / 2f, size / 2f);
        if      (type.isColor()) animator.rotation = -90f;
        else if (type.isShape()) animator.rotation = 90f;

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
