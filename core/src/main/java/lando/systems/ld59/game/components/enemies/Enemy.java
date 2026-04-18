package lando.systems.ld59.game.components.enemies;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import lando.systems.ld59.assets.anims.AnimEnemy;
import lando.systems.ld59.game.Components;

import java.util.List;

public abstract class Enemy {

    public enum Type {
        RED (AnimEnemy.RED_2),
        BLACK (AnimEnemy.BLACK_3);

        final AnimEnemy animType;
        Type(AnimEnemy animType) {
            this.animType = animType;
        }

        public AnimEnemy getAnimType() {
            return animType;
        }
    }

    public static final List<Class<? extends Component>> ENEMY_COMPONENT_TYPES = List.of(
        EnemyShipRed.class,
        EnemyShipBlack.class
    );

    @SuppressWarnings("unchecked")
    public static <E extends Enemy> E getEnemyComponent(Entity entity) {
        for (var enemyType : ENEMY_COMPONENT_TYPES) {
            var enemy = Components.get(entity, enemyType);
            if (enemy != null) {
                return (E) enemy;
            }
        }
        return null;
    }

}
