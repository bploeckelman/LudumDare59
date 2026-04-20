package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Interpolation;
import lando.systems.ld59.assets.anims.AnimBaseTurret;

public class TurretPattern implements Component {
    public enum Type { LINE, FAN, SWEEP }

    public static TurretPattern fan() { return new TurretPattern(Type.FAN); }
    public static TurretPattern line() { return new TurretPattern(Type.LINE); }
    public static TurretPattern sweep() { return new TurretPattern(Type.SWEEP); }

    public final Type type;

    public TurretPattern(Type type) {
        this.type = type;
    }

    public AnimBaseTurret getCannonBarrelAnim() {
        switch (type) {
            case LINE:  return AnimBaseTurret.CANNON_BARREL_C;
            case FAN:   return AnimBaseTurret.CANNON_BARREL_B;
            case SWEEP: return AnimBaseTurret.CANNON_BARREL_A;
            default:    return AnimBaseTurret.CANNON_BARREL_E;
        }
    }

    public Interp getInterp() {
        switch (type) {
            case LINE: return new Interp(1f, Interpolation.linear, Interp.Repeat.LOOP);
            case FAN: return new Interp(4f, Interpolation.linear, Interp.Repeat.LOOP);
            case SWEEP: return new Interp(2f, Interpolation.linear, Interp.Repeat.PINGPONG);
            default: return new Interp(1f, Interpolation.linear, Interp.Repeat.LOOP);
        }
    }

    public float angleExtents() {
        switch (type) {
            case LINE: return 30;
            case FAN: return 35;
            case SWEEP: return 40;
            default: return 0;
        }
    }

    public float bulletSpeed() {
        switch (type) {
            case LINE: return 300f;
            case FAN: return 150f;
            case SWEEP: return 150f;
            default: return 100f;
        }
    }

    public int getShotMod() {
        switch (type) {
            case LINE: return 4;
            case FAN: return 12;
            case SWEEP: return 3;
            default: return 1;
        }
    }
}
