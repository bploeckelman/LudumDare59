package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld59.AnimDepths;import lando.systems.ld59.assets.SoundType;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.renderable.CableShaderRenderable;
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
    private CableShaderRenderable cableShaderRenderable;

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
        cableShaderRenderable = null;
    }

    public void complete() {
        var endPenUltimateLocation = turret.colorPortPenUltimateLocation;
        var endLocation = turret.colorPortLocation;
        if (baseButton.isEnergy()) {
            var energyColor = baseButton.getEnergyColor();
            turret.connectEnergy(energyColor);
            state = State.CONNECTED;
            AudioEvent.playSound(SoundType.PLUGIN, 1f);
            Util.log(TAG, "Connected: energy '" + energyColor.type + "' to turret");
        }
        else if (baseButton.isPattern()) {
            var turretPattern = baseButton.getTurretPattern();
            turret.connectPattern(turretPattern);
            state = State.CONNECTED;
            AudioEvent.playSound(SoundType.PLUGIN, 1f);
            endPenUltimateLocation = turret.patternPortPenUltimateLocation;
            endLocation = turret.patternPortLocation;
            Util.log(TAG, "Connected: pattern '" + turretPattern.type + "' to turret");
        }

        // Create a 'path' FlatShape renderable for this connection if there isn't already one created
        if (flatShape == null) {
            var buttonPos = Components.get(baseButton.entity, Position.class);

            var start = FramePool.vec2(buttonPos.x, buttonPos.y);
            var points = Util.generateStraightPath(start, endPenUltimateLocation);
            points.add(endLocation);

            var lineWidth = 10f;
            var cannonColor = turret.getCannonColor();
            var pathColor = FramePool.color(cannonColor.r, cannonColor.g, cannonColor.b, 0.9f);

            // NOTE: FlatShape takes RopePath points by ref so it should stay up to date as RopePath updates run
            ropePath = new RopePath(points);
            // TODO: replace this FlatShape.path with a shader based electricity thing, and wire up jostling based on screen shake triggering events
            flatShape = FlatShape.path(AnimDepths.CABLES, ropePath.positions, pathColor, lineWidth);
//            entity.add(flatShape);

            cableShaderRenderable = new CableShaderRenderable(this, ropePath);
            entity.add(cableShaderRenderable);
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

    public Color getColor() {
        if (turret != null) {
            return turret.getCannonColor();
        }
        return Color.GRAY;
    }

    public void removeConnection() {
        if (baseButton != null && turret != null) {
            if (baseButton.isEnergy()) {
                turret.connectEnergy(null);
            } else if (baseButton.isPattern()) {
                turret.connectPattern(null);
            }
            flatShape = null;
            cableShaderRenderable = null;
            turret = null;
            baseButton = null;
            AudioEvent.playSound(SoundType.PLUGOUT);
            EntityEvent.remove(entity);
        }
    }
}
