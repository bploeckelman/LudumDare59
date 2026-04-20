package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import lando.systems.ld59.utils.Callbacks;

public class Timer implements Component {

    public final Entity entity;

    public float duration;
    public Callbacks.NoArg onEnd;

    public Timer(Entity entity) {
        this(entity, 0, null);
    }

    public Timer(Entity entity, float duration) {
        this(entity, duration, null);
    }

    public Timer(Entity entity, float duration, Callbacks.NoArg onEnd) {
        this.entity = entity;
        this.onEnd = onEnd;
        start(duration);
    }

    public void start(float duration) {
        this.duration = duration;
    }
}
