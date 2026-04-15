package lando.systems.ld59.game.signals;

import lando.systems.ld59.game.components.Cooldowns;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class CooldownEvent implements SignalEvent {

    public Cooldowns cooldowns;
    public Cooldowns cooldowns() { return cooldowns; }

    public static void reset(Cooldowns cooldowns, String name) { signal.dispatch(new Reset(cooldowns, name)); }

    public static final class Reset extends CooldownEvent {
        public String name;
        public Reset(Cooldowns cooldowns, String name) {
            super(cooldowns);
            this.name = name;
        }
    }
}
