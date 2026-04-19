package lando.systems.ld59.game.signals;

import lando.systems.ld59.game.components.BaseButton;
import lando.systems.ld59.game.components.Turret;

public interface ConnectionEvent extends SignalEvent {

    default Turret turret() { return null; }
    default BaseButton baseButton() { return null; }

    // T_T - I miss record types...

    static void touchedTurret(Turret turret) { signal.dispatch(new TouchedTurret(turret)); }
    static void touchedBaseButton(BaseButton baseButton) { signal.dispatch(new TouchedBaseButton(baseButton)); }

    class TouchedTurret implements ConnectionEvent {
        private final Turret turret;
        public TouchedTurret(Turret turret) { this.turret = turret; }
        @Override public Turret turret() { return turret; }
    }

    class TouchedBaseButton implements ConnectionEvent {
        private final BaseButton baseButton;
        public TouchedBaseButton(BaseButton baseButton) { this.baseButton = baseButton; }
        @Override public BaseButton baseButton() { return baseButton; }
    }
}
