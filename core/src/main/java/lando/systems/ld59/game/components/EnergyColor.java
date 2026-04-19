package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class EnergyColor implements Component {

    public static final Color RED = new Color(1, .3f, .3f, 1);
    public static final Color GREEN = new Color(.3f, 1, .3f, 1);
    public static final Color BLUE = new Color(.3f, .3f, 1, 1);

    public enum Type {
        RED, GREEN, BLUE;
        public static Type getRandom() {
            return values()[MathUtils.random(values().length - 1)];
        }
    }

    public static EnergyColor red() {return new EnergyColor(Type.RED);}
    public static EnergyColor green() {return new EnergyColor(Type.GREEN);}
    public static EnergyColor blue() {return new EnergyColor(Type.BLUE);}

    public final Type type;

    public EnergyColor(Type type) {
        this.type = type;
    }

    public Color getColor() {
        switch (type) {
            case RED: return RED;
            case GREEN: return GREEN;
            case BLUE: return BLUE;
            default: return Color.WHITE;
        }
    }
}
