package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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

    private Vector2 pendingPoint;
    public RopePath tempPath;
    public RopePath ropePath;

    private State state;
    private Turret turret;
    private BaseButton baseButton;
    private CableShaderRenderable cableShaderRenderable;

    public static Connection createPending(Entity entity, Turret turret) {
        return new Connection(entity, State.PENDING, turret, null, turret.pos);
    }

    public static Connection createPending(Entity entity, BaseButton baseButton) {
        return new Connection(entity, State.PENDING, null, baseButton, baseButton.pos);
    }

    private Connection(Entity entity, State state, Turret turret, BaseButton baseButton, Position startPos) {
        this.entity = entity;
        this.state = state;
        this.turret = turret;
        this.baseButton = baseButton;
        cableShaderRenderable = null;



        var start = FramePool.vec2(startPos.x, startPos.y);
        var end = FramePool.vec2(startPos.x + 10f, startPos.y + 10f);
        var points = Util.generateStraightPath(start, end, 40);
        pendingPoint = new Vector2(end);
        points.add(pendingPoint);

        tempPath = new RopePath(points);
        cableShaderRenderable = new CableShaderRenderable(this, tempPath);
        entity.add(cableShaderRenderable);

    }

    public void complete() {
        entity.remove(CableShaderRenderable.class);
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

        var buttonPos = Components.get(baseButton.entity, Position.class);

        var points = tempPath.positions;
        points.removeIndex(points.size - 1);
        points.add(endPenUltimateLocation);
        points.add(endLocation);

        // NOTE: FlatShape takes RopePath points by ref so it should stay up to date as RopePath updates run
        ropePath = new RopePath(points);
        ropePath.pinEnds();

        cableShaderRenderable = new CableShaderRenderable(this, ropePath);
        entity.add(cableShaderRenderable);

    }

    public void jostle() {
        if (ropePath != null) {
            ropePath.jostle();
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

    public void setPendingPoint(Vector3 point, float dt) {
        pendingPoint.set(point.x, point.y);
        tempPath.setPointPosition(tempPath.positions.size - 1, point.x, point.y);
        tempPath.update(dt);

    }

    public void removeConnection() {
        if (baseButton != null && turret != null) {
            if (baseButton.isEnergy()) {
                turret.connectEnergy(null);
            } else if (baseButton.isPattern()) {
                turret.connectPattern(null);
            }
            cableShaderRenderable = null;
            turret = null;
            baseButton = null;
            AudioEvent.playSound(SoundType.PLUGOUT);
            EntityEvent.remove(entity);
        }
    }
}
