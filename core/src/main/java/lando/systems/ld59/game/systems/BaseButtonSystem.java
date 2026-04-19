package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.BaseButton;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.game.signals.ConnectionEvent;

public class BaseButtonSystem extends IteratingSystem {

    public BaseButtonSystem() {
        super(Family.one(BaseButton.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var baseButton = Components.get(entity, BaseButton.class);
        var animator = Components.get(entity, Animator.class);

        var currentAnim = baseButton.getCurrentAnim();
        animator.play(currentAnim);

        if (animator.isComplete()) {
            if (baseButton.isIdle() && baseButton.shimmerTimer <= 0) {
                baseButton.shimmerTimer = MathUtils.random(2f, 8f);
                animator.start(currentAnim);
            }
            if (baseButton.isPressed()) {
                baseButton.setToActive();
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
                if (baseButton.isIdle()) {
                    baseButton.setToPressed();
                    return true;
                }
                if (baseButton.isActive()) {
                    baseButton.setToIdle();
                    return true;
                }
            }
        }
        return false;
    }
}
