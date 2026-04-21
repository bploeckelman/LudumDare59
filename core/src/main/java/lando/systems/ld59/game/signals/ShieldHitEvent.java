package lando.systems.ld59.game.signals;

import com.badlogic.gdx.math.Vector2;

public interface ShieldHitEvent extends SignalEvent{

    String TAG = ShieldHitEvent.class.getSimpleName();

    Vector2 pos();

    static void hit(Vector2 pos) { signal.dispatch(new ShieldHitEvent.Hit(pos)); }
    static void hit(float x, float y) { signal.dispatch(new ShieldHitEvent.Hit(new Vector2(x, y))); }

    class Hit implements ShieldHitEvent {
        public final Vector2 pos;
        public Vector2 pos() { return pos; }
        public Hit(Vector2 pos) { this.pos = pos; }
    }
}
