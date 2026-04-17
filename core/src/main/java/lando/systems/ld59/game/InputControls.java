package lando.systems.ld59.game;

import com.badlogic.gdx.Input;
import lando.systems.ld59.game.components.Player;
import lombok.RequiredArgsConstructor;

public class InputControls {

    public final int left;
    public final int right;
    public final int down;
    public final int jump;
    public final int enter;
    public final int debug;

    public int left() { return left; }
    public int right() { return right; }
    public int down() { return down; }
    public int jump() { return jump; }
    public int enter() { return enter; }
    public int debug() { return debug; }

    public static InputControls forPlayer(Player player) {
        return new InputControls(Input.Keys.A, Input.Keys.D, Input.Keys.S, Input.Keys.SPACE, Input.Keys.ENTER, Input.Keys.E);
    }

    private InputControls(int left, int right, int down, int jump, int enter, int debug) {
        this.left = left;
        this.right = right;
        this.down = down;
        this.jump = jump;
        this.enter = enter;
        this.debug = debug;
    }
}
