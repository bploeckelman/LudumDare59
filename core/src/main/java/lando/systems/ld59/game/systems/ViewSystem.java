package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.Config;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.utils.Calc;
import lando.systems.ld59.utils.Util;
import lombok.RequiredArgsConstructor;

public class ViewSystem extends IteratingSystem {

    private static final String TAG = ViewSystem.class.getSimpleName();
    private static final float KILL_ZONE_MARGIN = 200f;

    private final Rectangle killZone = new Rectangle();

    public ViewSystem() {
        super(Family.one(Position.class).get());
        killZone.set(-KILL_ZONE_MARGIN, -KILL_ZONE_MARGIN, Config.window_width + KILL_ZONE_MARGIN, Config.window_height + KILL_ZONE_MARGIN * 2);
    }

    @Override
    public void update (float dt) {
        for (int i = getEntities().size() - 1; i>= 0;  i--) {
            var entity = getEntities().get(i);
            var pos = Components.get(entity, Position.class);
            if (!killZone.contains(pos.x, pos.y)) {
                getEngine().removeEntity(entity);
//                Util.log(TAG, "Removing entity outside kill zone: " + entity);
                continue;
            }
            processEntity(entity, dt);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // NOOP
    }


}
