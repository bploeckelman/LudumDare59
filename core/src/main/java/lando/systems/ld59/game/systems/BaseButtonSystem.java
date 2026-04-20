package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.BaseButton;
import lando.systems.ld59.game.components.Connection;
import lando.systems.ld59.game.components.Health;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.game.signals.ConnectionEvent;

public class BaseButtonSystem extends IteratingSystem {

    ImmutableArray<Entity> connections;

    public BaseButtonSystem() {
        super(Family.one(BaseButton.class).get());
    }

    @Override
    public void update (float dt) {
        connections = getEngine().getEntitiesFor(Family.all(Connection.class).get());

        for (int i = getEntities().size() - 1; i >= 0;  i--) {
            var entity = getEntities().get(i);
            processEntity(entity, dt);
        }
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var baseButton = Components.get(entity, BaseButton.class);
        var animator = Components.get(entity, Animator.class);

        var currentAnim = baseButton.getCurrentAnim();
        animator.play(currentAnim);

        Connection connection = null;

        for (var connectionEntity : connections) {
            var connectionComp = Components.get(connectionEntity, Connection.class);
            if (connectionComp.getBaseButton() == baseButton) {
                connection = connectionComp;
                break;
            }
        }

        baseButton.setCurrentAnim(connection);

        if (animator.isComplete()) {
            if (baseButton.isIdle() && baseButton.shimmerTimer <= 0) {
                baseButton.shimmerTimer = MathUtils.random(2f, 8f);
                animator.start(currentAnim);
            }
        }

        baseButton.stateDuration += delta;
        baseButton.shimmerTimer -= delta;
    }

    public boolean handleTouchUp(float worldX, float worldY, int pointer, int button) {
        // NOTE(Brian): I remember for-each not playing nice with the getEntities() collection type, hence normal for loop
        var entities = getEntities();
        for (int i = 0; i < entities.size(); i++) {
            var entity = entities.get(i);
            var baseButton = Components.get(entity, BaseButton.class);
            var isTouched = baseButton.contains(worldX, worldY);
            if (isTouched) {
                ConnectionEvent.touchedBaseButton(baseButton);
                baseButton.stateDuration = 0;
                return true;
            }
        }
        return false;
    }
}
