package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.assets.AnimType;
import lando.systems.ld59.assets.anims.AnimBaseButton;
import lando.systems.ld59.game.Components;

public class BaseButton implements Component {

    private enum State { IDLE, PRESSED, ACTIVE, DISABLED }

    public enum Type {
        BLUE, GREEN, RED,
        CIRCLE, SQUARE, TRIANGLE
        ;
        public boolean isColor() { return this == BLUE || this == RED || this == GREEN; }
        public boolean isShape() { return this == CIRCLE || this == SQUARE || this == TRIANGLE; }
    }

    // NOTE: actual size 72x72, slight shrink to fit in the button board slots better
//    public static final float SIZE = 72f;
    public static final float SIZE = 70f;

    public final Entity entity;
    public final Type type;

    public float stateDuration;
    public float shimmerTimer;

    private AnimType animIdle;
    private AnimType animPressed;
    private AnimType animActive;
    private State state;
    private boolean isAnimating;

    public BaseButton(BaseButton.Type type, Entity entity) {
        this.type = type;
        this.entity = entity;
        this.state = State.IDLE;
        this.stateDuration = 0f;
        this.shimmerTimer = MathUtils.random(5f);
        this.isAnimating = false;
        setAnimations();
    }

    public boolean isEnergy()  { return type == Type.BLUE || type == Type.GREEN || type == Type.RED; }
    public boolean isPattern() { return type == Type.CIRCLE || type == Type.SQUARE || type == Type.TRIANGLE; }

    public EnergyColor getEnergyColor() {
        switch (type) {
            case BLUE: return EnergyColor.blue();
            case GREEN: return EnergyColor.green();
            case RED: return EnergyColor.red();
            default: return null;
        }
    }

    public TurretPattern getTurretPattern() {
        switch (type) {
            case CIRCLE: return TurretPattern.sweep();
            case SQUARE: return TurretPattern.line();
            case TRIANGLE: return TurretPattern.fan();
            default: return null;
        }
    }

    public void setCurrentAnim(Connection connection) {
        if (connection == null) {
            state = State.IDLE;;
        } else if (connection.isPending()){
            state = State.PRESSED;
        } else {
            state = State.ACTIVE;
        }
    }

    public AnimType getCurrentAnim() {
        AnimType currentAnim = null;
        switch (state) {
            case IDLE:     currentAnim = animIdle;    break;
            case PRESSED:  currentAnim = animPressed; break;
            case ACTIVE:   currentAnim = animActive;  break;
            case DISABLED: currentAnim = null;        break;
        }
        return currentAnim;
    }

    public boolean isIdle()     { return state == State.IDLE; }
    public boolean isPressed()  { return state == State.PRESSED; }
    public boolean isActive()   { return state == State.ACTIVE; }
    public boolean isDisabled() { return state == State.DISABLED; }

    public void setToIdle()     { setState(State.IDLE); }
    public void setToPressed()  { setState(State.PRESSED); }
    public void setToActive()   { setState(State.ACTIVE); }
    public void setToDisabled() { setState(State.DISABLED); }

    private void setState(State newState) {
        state = newState;
        stateDuration = 0f;
    }

    public boolean contains(float worldX, float worldY) {
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
                animPressed = AnimBaseButton.CIRCLE_HIT;
                animActive = AnimBaseButton.CIRCLE_ON;
            } break;
            case SQUARE: {
                animIdle = AnimBaseButton.SQUARE_IDLE;
                animPressed = AnimBaseButton.SQUARE_HIT;
                animActive = AnimBaseButton.SQUARE_ON;
            } break;
            case TRIANGLE: {
                animIdle = AnimBaseButton.TRIANGLE_IDLE;
                animPressed = AnimBaseButton.TRIANGLE_HIT;
                animActive = AnimBaseButton.TRIANGLE_ON;
            } break;
        }
    }
}
