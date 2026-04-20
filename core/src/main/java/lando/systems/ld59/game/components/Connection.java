package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld59.assets.SoundType;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.renderable.FlatShape;
import lando.systems.ld59.game.signals.AudioEvent;
import lando.systems.ld59.game.signals.EntityEvent;
import lando.systems.ld59.utils.FramePool;
import lando.systems.ld59.utils.RopePath;
import lando.systems.ld59.utils.Util;

public class Connection implements Component {

    private static final String TAG = Connection.class.getSimpleName();

    public enum State { PENDING, CONNECTED }

    public final Entity entity;

    public RopePath ropePath;

    private State state;
    private Turret turret;
    private BaseButton baseButton;
    private FlatShape flatShape;

    public static Connection createPending(Entity entity, Turret turret) {

        return new Connection(entity, State.PENDING, turret, null);
    }

    public static Connection createPending(Entity entity, BaseButton baseButton) {
        return new Connection(entity, State.PENDING, null, baseButton);
    }

    private Connection(Entity entity, State state, Turret turret, BaseButton baseButton) {
        this.entity = entity;
        this.state = state;
        this.turret = turret;
        this.baseButton = baseButton;
        this.flatShape = null;
    }

    public void complete() {
        if (baseButton.isEnergy()) {
            var energyColor = baseButton.getEnergyColor();
            turret.connectEnergy(energyColor);
            state = State.CONNECTED;
            Util.log(TAG, "Connected: energy '" + energyColor.type + "' to turret");
        }
        else if (baseButton.isPattern()) {
            var turretPattern = baseButton.getTurretPattern();
            turret.connectPattern(turretPattern);
            state = State.CONNECTED;
            Util.log(TAG, "Connected: pattern '" + turretPattern.type + "' to turret");
        }

        // Create a 'path' FlatShape renderable for this connection if there isn't already one created
        if (flatShape == null) {
            var turretPos = Components.get(turret.entity, Position.class);
            var buttonPos = Components.get(baseButton.entity, Position.class);

            var start = FramePool.vec2(buttonPos.x, buttonPos.y);
            var end = FramePool.vec2(turretPos.x, turretPos.y);
            var points = Util.generateStraightPath(start, end);

            var lineWidth = 10f;
            var cannonColor = turret.getCannonColor();
            var pathColor = FramePool.color(cannonColor.r, cannonColor.g, cannonColor.b, 0.9f);

            // NOTE: FlatShape takes RopePath points by ref so it should stay up to date as RopePath updates run
            ropePath = new RopePath(points);
            // TODO: replace this FlatShape.path with a shader based electricity thing, and wire up jostling based on screen shake triggering events
            flatShape = FlatShape.path(BaseButton.ANIM_DEPTH - 1, ropePath.positions, pathColor, lineWidth);
            AudioEvent.playSound(SoundType.PLUG1);
            entity.add(flatShape);
        }
    }

    public boolean hasTurret() { return turret != null; }
    public boolean hasBaseButton() { return baseButton != null; }

    public boolean hasBothEndpoints() { return hasTurret() && hasBaseButton(); }
    public boolean isConnected() { return state == State.CONNECTED; }
    public boolean isPending()   { return state == State.PENDING; }

    public Turret getTurret() { return turret; }
    public BaseButton getBaseButton() { return baseButton; }

    public void setTurret(Turret turret) { this.turret = turret; }
    public void setBaseButton(BaseButton baseButton) { this.baseButton = baseButton; }

    public void removeConnection() {
        if (baseButton != null && turret != null) {
            if (baseButton.isEnergy()) {
                turret.connectEnergy(null);
            } else if (baseButton.isPattern()) {
                turret.connectPattern(null);
            }
            flatShape = null;
            turret = null;
            baseButton = null;
            AudioEvent.playSound(SoundType.PLUG2);
            EntityEvent.remove(entity);
        }

    }

    /**
     * Generates points along a line between start to end, with internal points randomly displaced perpendicularly from the line for variation
     */
    private Array<Vector2> generatePathPoints(Vector2 start, Vector2 end) {
        var defaultDensity = 0.1f;
        var defaultMaxDisplacement = 30f;
        return generatePathPoints(start, end, defaultDensity, defaultMaxDisplacement);
    }

    /**
     * Generates points along a line between start to end, with internal points randomly displaced perpendicularly from the line for variation,
     * customizing the number of points along the length based on {@code density} and the maximum perpendicular displacement distance from the line with {@code maxDisplacement}
     */
    private Array<Vector2> generatePathPoints(Vector2 start, Vector2 end, float density, float maxDisplacement) {
        var points = new Array<Vector2>();

        var direction = FramePool.vec2(end).sub(start);
        var distance = direction.len();
        direction.nor();

        var perpendicular = FramePool.vec2(-direction.y, direction.x);

        var numPoints = Math.max(2, Math.round(distance * density));
        for (int i = 0; i < numPoints; i++) {
            var t = (float) i / (numPoints - 1);
            var point = new Vector2(start).lerp(end, t);

            // Displace this point perpendicularly from line, if it's not the start or end point
            if (i > 0 && i < numPoints - 1) {
                var displacement = (MathUtils.random() - 0.5f) * 2 * maxDisplacement;
                point.add(perpendicular.x * displacement, perpendicular.y * displacement);
            }

            points.add(point);
        }
        return points;
    }
}
