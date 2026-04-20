package lando.systems.ld59.game.signals;

import com.badlogic.ashley.core.Entity;
import lando.systems.ld59.game.components.Turret;

public interface ScreenShakeEvent extends SignalEvent{

    String TAG = ScreenShakeEvent.class.getSimpleName();

    float amount();

    static void shake(float amount) { signal.dispatch(new ScreenShakeEvent.Shake(amount)); }

    class Shake implements ScreenShakeEvent {
        public final float amount;
        public float amount() { return amount; }
        public Shake(float amount) { this.amount = amount; }
    }
}
