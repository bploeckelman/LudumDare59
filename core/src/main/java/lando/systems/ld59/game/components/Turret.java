package lando.systems.ld59.game.components;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.AnimDepths;import lando.systems.ld59.Config;
import lando.systems.ld59.Main;
import lando.systems.ld59.assets.SoundType;
import lando.systems.ld59.assets.anims.AnimBaseTurret;
import lando.systems.ld59.assets.anims.AnimMisc;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.collision.CollisionCirc;
import lando.systems.ld59.game.components.collision.CollisionMask;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.game.components.renderable.Outline;
import lando.systems.ld59.game.signals.AudioEvent;
import lando.systems.ld59.utils.FramePool;

import java.util.Optional;

import static lando.systems.ld59.game.Constants.PLAYER_PROJECTILE_DAMAGE;

public class Turret implements Component {

    public static final float WIDTH = 160f;
    public static final float HEIGHT = 128f;
    public static final CollisionMask[] COLLIDES_WITH = new CollisionMask[] {
            CollisionMask.ENEMY, CollisionMask.ENEMY_PROJECTILE
    };

    public final float rotation;
    public final MutableFloat cannonRotation;
    public float repairTimer;

    private final Engine engine;

    public final Entity entity;
    public final Position pos;

    public final Entity base;
    public final Entity cannon;
    public final Entity door;
    public final Entity rockOverlay;
    public final Entity portArrowOverlay;
    public final Entity portLightOverlay;

    public final Collider baseCollider;
    public final Collider cannonCollider;
    private final Outline baseOutline;
    private final Outline cannonOutline;

    public boolean swappingCannonBarrel = false;

    public Turret(Engine engine, Entity entity, Position pos, float rot, Health turretHealth) {
        this.engine = engine;
        this.entity = entity;
        this.pos = pos;
        this.rotation = rot;
        this.cannonRotation = new MutableFloat(0);
        this.base = Factory.createEntity();
        this.cannon = Factory.createEntity();
        this.door = Factory.createEntity();
        this.rockOverlay = Factory.createEntity();
        this.portArrowOverlay = Factory.createEntity();
        this.portLightOverlay = Factory.createEntity();

        this.baseOutline    = new Outline(Color.CLEAR_WHITE, Color.CLEAR_WHITE, 1f);
        this.cannonOutline  = new Outline(Color.CLEAR_WHITE, Color.CLEAR_WHITE, 2f);

        var baseCollRotX = MathUtils.cosDeg(rotation) * 60f;
        var baseCollRotY = MathUtils.sinDeg(rotation) * 60f;
        this.baseCollider   = Collider.circ(CollisionMask.TURRET, baseCollRotX, baseCollRotY, 40, COLLIDES_WITH);
        this.cannonCollider = Collider.circ(CollisionMask.TURRET, 10, 2, 22, COLLIDES_WITH);

        var width = WIDTH;
        var height = HEIGHT;

        //
        // Cannon sits 'behind' base so that it can retract...
        //

        var cannonAnim = new Animator(AnimBaseTurret.CANNON_BARREL_A, new Vector2(width / 2f, height / 2f));
        cannonAnim.depth = AnimDepths.TURRETS + 1;
        cannonAnim.size.set(width, height);
        // NOTE: smaller turret anim means the cannon origin moved from halfway mark in source image
//        cannonAnim.rotationOrigin.set(width / 2f, height / 2f);
        cannonAnim.rotationOrigin.set(width * 0.5625f, height / 2f);

        var baseAnim = new Animator(AnimBaseTurret.BASE_IDLE, new Vector2(0, height / 2f));
        baseAnim.depth = AnimDepths.TURRETS + 2;
        baseAnim.size.set(width, height);
        baseAnim.rotation = rotation;

        //
        // Door anim, port and rock overlays sit on top of base
        //

        var doorAnim = new Animator(AnimBaseTurret.DOOR_OPEN, new Vector2(0, height / 2f));
        doorAnim.depth = AnimDepths.TURRETS + 3;
        doorAnim.size.set(width, height);
        doorAnim.rotation = rotation;

        // Overlays are at the same anim depth
        var overlayDepth = AnimDepths.TURRETS + 4;
        var rockAnim = new Animator(AnimBaseTurret.ROCK_OVERLAY, new Vector2(0, height / 2f));
        rockAnim.depth = overlayDepth;
        rockAnim.size.set(width, height);
        rockAnim.rotation = rotation;
        var portArrowAnim = new Animator(AnimBaseTurret.PORT_ARROW_LIGHT_OVERLAY, new Vector2(0, height / 2f));
        portArrowAnim.depth = overlayDepth;
        portArrowAnim.size.set(width, height);
        portArrowAnim.rotation = rotation;
        var portLightAnim = new Animator(AnimBaseTurret.PORT_ARROW_LIGHT_OVERLAY, new Vector2(0, height / 2f));
        portLightAnim.depth = overlayDepth;
        portLightAnim.size.set(width, height);
        portLightAnim.rotation = rotation;

        var cannonOffset = 92f;
        cannon.add(turretHealth);
        cannon.add(cannonAnim);
        cannon.add(cannonOutline);
        cannon.add(cannonCollider);
        cannon.add(new TurretPart());
        cannon.add(new Position(
                pos.x + MathUtils.cosDeg(rotation) * cannonOffset - 10f,
                pos.y + MathUtils.sinDeg(rotation) * cannonOffset));
        cannon.add(new Interp(1f, Interpolation.linear, Interp.Repeat.PINGPONG));

        base.add(turretHealth);
        base.add(baseAnim);
        base.add(baseOutline);
        base.add(baseCollider);
        base.add(new TurretPart());
        base.add(new Position(pos));

        door.add(doorAnim);
        door.add(new Position(pos));

        rockOverlay.add(rockAnim);
        rockOverlay.add(new Position(pos));

        portArrowOverlay.add(portArrowAnim);
        portArrowOverlay.add(new Position(pos));

        portLightOverlay.add(portArrowAnim);
        portLightOverlay.add(new Position(pos));

        engine.addEntity(base);
        engine.addEntity(cannon);
        engine.addEntity(door);
        engine.addEntity(rockOverlay);
        engine.addEntity(portArrowOverlay);
        engine.addEntity(portLightOverlay);
    }

    public void shoot() {
        if (swappingCannonBarrel) return;
        var turretPattern = cannon.getComponent(TurretPattern.class);

        if (turretPattern.type == TurretPattern.Type.FAN) {
            for (int i = 0; i < 3; i++) {
                createBullet(-20 + i * 20);
            }
        } else {
            createBullet(0);
        }
        var energyColor = Components.get(cannon, EnergyColor.class);
        boolean useFancySounds = energyColor != null;
        float squareVolume = 0.5f;
        float sawVolume = 1f;
        float sineVolume = .8f;
        float panValue = MathUtils.map(
            0, Config.window_width,
            -0.9f, 0.9f,
            Components.get(cannon, Position.class).x);
        int[] chord;
        switch(turretPattern.type){
            case FAN: chord = SoundType.cMaj; break;
            case LINE: chord = SoundType.fMaj; break;
            case SWEEP: chord = SoundType.gMaj; break;
            default: chord = SoundType.cMaj;break;
        }
        if(useFancySounds) {
            switch (energyColor.type) {
                case BLUE:
                    AudioEvent.playSound(
                        SoundType.getRandomSound(chord, SoundType.NoteType.SQUARE),
                        squareVolume,
                        panValue
                    );
                    break;
                case GREEN:
                    AudioEvent.playSound(
                        SoundType.getRandomSound(chord, SoundType.NoteType.SAW),
                        sawVolume,
                        panValue
                    );
                    break;
                case RED:
                    AudioEvent.playSound(
                        SoundType.getRandomSound(chord, SoundType.NoteType.SINE),
                        sineVolume,
                        panValue
                    );
                    break;
                default:
                    break;
            }
        }
        else {
            AudioEvent.playSound(
                SoundType.PEW,
                1f,
                panValue
            );
        }

    }

    public boolean hasPattern() {
        var turretPattern = cannon.getComponent(TurretPattern.class);
        return turretPattern != null;
    }

    private void createBullet(float rotationOffset) {
        float diameter = 20f;
        float radius = diameter / 2f;
        float cannonRot = cannonRotation.floatValue();

        var bullet = Factory.createEntity();
        var cannonPos = cannon.getComponent(Position.class);
        var energyColor = cannon.getComponent(EnergyColor.class);
        var turretPattern = cannon.getComponent(TurretPattern.class);

        var barrelTipOffset = 50f;
        var position = new Position(
                cannonPos.x + MathUtils.cosDeg(cannonRot) * barrelTipOffset + radius,
                cannonPos.y + MathUtils.sinDeg(cannonRot) * barrelTipOffset);

        var totalRotation = cannonRot + rotationOffset;
        var speed = turretPattern.bulletSpeed();
        var velocity = new Velocity(
            MathUtils.cosDeg(totalRotation) * speed,
            MathUtils.sinDeg(totalRotation) * speed);

        var animator = new Animator(AnimMisc.PROJECTILE);
        animator.depth = AnimDepths.BULLETS;
        animator.size.set(diameter, diameter);
        animator.origin.set(radius, radius);
        animator.tint.set(Optional.ofNullable(energyColor)
                .map(EnergyColor::getColor).orElse(FramePool.color(1f, 1f, 1f, 1f)));

        var collidesWith = new CollisionMask[] { CollisionMask.ENEMY, CollisionMask.ENEMY_PROJECTILE };
        var bulletCollider = Collider.circ(CollisionMask.PLAYER_PROJECTILE, 0,  0, diameter/2f, collidesWith);

        bullet.add(position);
        bullet.add(animator);
        bullet.add(velocity);
        bullet.add(bulletCollider);
        bullet.add(new Projectile(PLAYER_PROJECTILE_DAMAGE));
        bullet.add(new Health(1));
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

            // Update cannon barrel based on firing pattern;
            // - rotate existing barrel behind base (base turret rotation + 180 == relative 270 deg)
            // - swap barrel animation
            // - rotate new barrel out from behind base (base turret rotation == relative 90 deg)
            var animator = Components.get(cannon, Animator.class);
            Timeline.createSequence()
                    // Set fence so we don't do extra rotation, shooting during swap
                    .push(Tween.call((type, source) -> swappingCannonBarrel = true))
                    // Rotate until barrel is hidden
                    .push(Tween.to(cannonRotation, -1, 0.2f).target(rotation + 180))
                    // Change cannon barrel animation and pause briefly
                    .push(Tween.call((type, source) -> animator.play(pattern.getCannonBarrelAnim())))
                    .pushPause(0.3f)
                    // Return to standard starting position
                    .push(Tween.to(cannonRotation, -1, 0.2f).target(rotation))
                    .pushPause(0.3f)
                    // Free fence to allow normal rotation, shooting now that swap is done
                    .push(Tween.call((type, source) -> swappingCannonBarrel = false))
                    .start(Main.game.tween);
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
            baseOutline.outlineColor(Color.CLEAR_WHITE);
            cannonOutline.outlineColor(Color.CLEAR_WHITE);
            return;
        }
        cannon.add(color);

        baseOutline.outlineColor(color.getColor());
        cannonOutline.outlineColor(color.getColor());
    }

    public Color getCannonColor() {
        return Components.optional(cannon, EnergyColor.class)
                .map(EnergyColor::getColor)
                .orElse(FramePool.color(1, 1, 1));
    }

    public Circle getBaseCollisionCircle() {
        var pos = Components.get(base, Position.class);
        var shape = baseCollider.shape(CollisionCirc.class);
        return shape.circle(pos);
    }

    public Circle getCannonCollisionCircle() {
        var pos = Components.get(cannon, Position.class);
        var shape = cannonCollider.shape(CollisionCirc.class);
        return shape.circle(pos);
    }
}
