package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Interpolation;
import lando.systems.ld59.utils.Calc;

/**
 * Simple interpolation in a component for any {@code Entity} that can make use of a single interpolated parameter to
 * control value changes over time. If an {@code Entity} has more complicated interpolation needs; multiple values with
 * different speeds, durations, and easing functions, then this is not the right choice.
 */
public class Interp implements Component {

    public enum Repeat {NONE, PINGPONG, LOOP}

    private static final String TAG = Interp.class.getSimpleName();

    private final float duration;
    private final Repeat repeatMode;

    private float elapsed;
    private float value;
    private boolean paused;

    public Interpolation interpolation;

    /**
     * Multiplier for elapsed time accumulator:
     * <strong>must be > 0, if <= 0 reset to 1</strong>
     */
    public float speed;

    public Interp(float durationSecs) {
        this(durationSecs, Interpolation.linear);
    }

    public Interp(float durationSecs, Interpolation interpolation) {
        this(durationSecs, interpolation, Repeat.NONE);
    }

    public Interp(float durationSecs, Interpolation interpolation, Repeat repeatMode) {
        this.duration = durationSecs;
        this.elapsed = 0;
        this.value = 0;
        this.paused = false;
        this.interpolation = interpolation;
        this.speed = 1;
        this.repeatMode = repeatMode;
    }

    /**
     * Interpolate between {@code start} and {@code end} values
     * based on current interpolated percentage [0..1] from this component.
     */
    public float apply(float start, float end) {
        return start + (end - start) * value;
    }

    public void update(float delta) {
        elapsed += speed * delta;

        float alpha = 0f;

        switch (repeatMode) {
            case PINGPONG:
                // How many full durations have we passed
                int cycle = (int)(elapsed / duration);
                // Get position within current duration
                float remainder = elapsed % duration;

                // Even cycles go 0→1, odd cycles go 1→0
                if (cycle % 2 == 0) {
                    alpha = remainder / duration;
                } else {
                    alpha = 1f - (remainder / duration);
                }
                break;
            case LOOP:
                // Wrap elapsed, then normalize
                elapsed %= duration;
                if (elapsed < 0) elapsed += duration; // handle negative speed
                alpha = elapsed / duration;
                break;
            case NONE:
                // Constrain elapsed time to a percentage, ie. in [0..1]
                alpha = Calc.clampf(elapsed() / duration(), 0f, 1f);
                break;
        }

        // Apply the interpolation function to the percentage completed to get an interpolated percent [0..1]
        value = interpolation.apply(alpha);
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void reset() {
        elapsed = 0;
        value = 0;
    }

    public float duration() { return duration; }
    public float elapsed() { return elapsed; }
    public float value() { return value; }
    public float inverseValue() { return 1f - value; }
    public boolean isPaused() { return paused; }
    public boolean isRunning() { return !paused; }
}
