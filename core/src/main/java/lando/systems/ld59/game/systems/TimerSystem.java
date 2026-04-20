package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.Timer;

public class TimerSystem extends IteratingSystem {

    public TimerSystem() {
        super(Family.one(Timer.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float dt) {
        var timer = Components.get(entity, Timer.class);
        if (timer.duration > 0) {
            timer.duration -= dt;
            if (timer.duration <= 0 && timer.onEnd != null) {
                timer.onEnd.run();
            }
        }
    }
}
