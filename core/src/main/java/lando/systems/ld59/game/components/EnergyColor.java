package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;

public class EnergyColor implements Component {
    public enum energy {RED, GREEN, BLUE}

    public final energy type;

    public EnergyColor(energy type) {
        this.type = type;
    }

    public static EnergyColor red() {return new EnergyColor(energy.RED);}
    public static EnergyColor green() {return new EnergyColor(energy.GREEN);}
    public static EnergyColor blue() {return new EnergyColor(energy.BLUE);}
}
