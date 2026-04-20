package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld59.assets.anims.AnimEnemy;

public class EnergyColor implements Component {

    public static final Color COLOR_RED   = new Color(170/255f,  51/255f, 119/255f, 1);
    public static final Color COLOR_GREEN = new Color( 35/255f, 136/255f,  51/255f, 1);
    public static final Color COLOR_BLUE  = new Color( 68/255f, 119/255f, 170/255f, 1);

    public enum Type {
        RED, GREEN, BLUE;
        public static Type getRandom() {
            return values()[MathUtils.random(values().length - 1)];
        }
        public Color getColor() {
            switch (this) {
                case RED: return COLOR_RED;
                case GREEN: return COLOR_GREEN;
                case BLUE: return COLOR_BLUE;
                default: return Color.MAGENTA;
            }
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
        return type.getColor();
    }

    public static EnergyColor.Type of(EnemyTag.EnemyType enemyType) {
        var energyColorType = EnergyColor.Type.RED;
        switch (enemyType) {
            case FLYER: energyColorType = EnergyColor.Type.RED; break;
            case KAMIKAZE: energyColorType = EnergyColor.Type.BLUE; break;
            case SPLITTER: energyColorType = EnergyColor.Type.GREEN; break;
        }
        return energyColorType;
    }
}
