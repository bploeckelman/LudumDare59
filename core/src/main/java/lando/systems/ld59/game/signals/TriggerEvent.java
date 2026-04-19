package lando.systems.ld59.game.signals;

import lando.systems.ld59.game.components.TilemapObject;

public interface TriggerEvent extends SignalEvent {

    static void dialog(TilemapObject.Trigger trigger) { signal.dispatch(new Dialog(trigger)); }

    class Dialog implements TriggerEvent {
        public final String key;
        private Dialog(TilemapObject.Trigger trigger) {
            this.key = trigger.type;
            trigger.activated = true;
        }
    }

}
