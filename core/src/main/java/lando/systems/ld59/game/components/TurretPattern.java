package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Interpolation;

public class TurretPattern implements Component {
    public enum Type { LINE, FAN, SWEEP }

    public final Type type;

    public TurretPattern(Type type) {
        this.type = type;
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
            case LINE: return 0;
            case FAN: return 60;
            case SWEEP: return 45;
            default: return 0;
        }
    }
}
