package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import lando.systems.ld59.game.Components;

public class EnemyTag implements Component {
    public boolean alive;
    public EnergyColor.Type energyColor;
    public EnemyType type;
    public State state;


    public enum State {
        IDLE,
        MOVE,
        SHOOT
    }
    public enum EnemyType {
        FLYER,
        RUNNER
    }

    public EnemyTag() {
        alive = true;
    }


    @SuppressWarnings("unchecked")
    public static <E extends EnemyTag> E getEnemyComponent(Entity entity) {
        var enemy = Components.get(entity, EnemyTag.class);
        if (enemy != null) {
            return (E) enemy;
        }
        return null;
    }
}
