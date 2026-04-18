package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;

public class EnergyColor implements Component {
    public enum Type {RED, GREEN, BLUE}

    public final Type type;

    public EnergyColor(Type type) {
        this.type = type;
    }

    public static EnergyColor red() {return new EnergyColor(Type.RED);}
    public static EnergyColor green() {return new EnergyColor(Type.GREEN);}
    public static EnergyColor blue() {return new EnergyColor(Type.BLUE);}
}
