package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import lando.systems.ld59.assets.AnimType;
import lando.systems.ld59.assets.anims.AnimBaseButton;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.renderable.Animator;

public class BaseButton implements Component {

    public enum Type { BLUE, GREEN, RED, CIRCLE, SQUARE, TRIANGLE }
    private enum State { IDLE, PRESSED, ACTIVE, DISABLED }

    public static final float SIZE = 72f;

    public final Entity entity;
    public final Type type;

    public Runnable onClick;

    private AnimType animIdle;
    private AnimType animPressed;
    private AnimType animActive;
    private State state;
    private boolean isAnimating;

    public BaseButton(BaseButton.Type type, Entity entity) {
        this.type = type;
        this.entity = entity;
        this.state = State.IDLE;
        this.isAnimating = false;
        setAnimations();
    }

    // TODO: switch states / anims on touch, create a BaseButtonSystem to manage,
    // TODO: temporary, remove after adding a BaseButtonSystem that can manage this fiddly crap ------------------------
    public void updateAnimator() {
        AnimType currentAnim = null;

        switch (state) {
            case IDLE:     currentAnim = animIdle;    break;
            case PRESSED:  currentAnim = animPressed; break;
            case ACTIVE:   currentAnim = animActive;  break;
            case DISABLED: currentAnim = null;        break;
        }

        if (currentAnim != null) {
            var animator = Components.get(entity, Animator.class);
            animator.start(currentAnim);
        }
    }
    // TODO: -----------------------------------------------------------------------------------------------------------

    public boolean isInBounds(float worldX, float worldY) {
        var bounds = Components.get(entity, Bounds.class);
        return bounds.contains(worldX, worldY);
    }

    public void startAnimating() { isAnimating = true; }
    public void stopAnimating() { isAnimating = false; }
    public void toggleAnimating() { isAnimating = !isAnimating; }

    private void setAnimations() {
        switch (type) {
            case BLUE: {
                animIdle = AnimBaseButton.BLUE_IDLE;
                animPressed = AnimBaseButton.BLUE_HIT;
                animActive = AnimBaseButton.BLUE_ON;
            } break;
            case GREEN: {
                animIdle = AnimBaseButton.GREEN_IDLE;
                animPressed = AnimBaseButton.GREEN_HIT;
                animActive = AnimBaseButton.GREEN_ON;
            } break;
            case RED: {
                animIdle = AnimBaseButton.RED_IDLE;
                animPressed = AnimBaseButton.RED_HIT;
                animActive = AnimBaseButton.RED_ON;
            } break;
            case CIRCLE: {
                animIdle = AnimBaseButton.CIRCLE_IDLE;
//                animPressed = AnimBaseButton.CIRCLE_HIT;
//                animActive = AnimBaseButton.CIRCLE_ON;
            } break;
            case SQUARE: {
                animIdle = AnimBaseButton.SQUARE_IDLE;
//                animPressed = AnimBaseButton.SQUARE_HIT;
//                animActive = AnimBaseButton.SQUARE_ON;
            } break;
            case TRIANGLE: {
                animIdle = AnimBaseButton.TRIANGLE_IDLE;
//                animPressed = AnimBaseButton.TRIANGLE_HIT;
//                animActive = AnimBaseButton.TRIANGLE_ON;
            } break;
        }
    }
}
