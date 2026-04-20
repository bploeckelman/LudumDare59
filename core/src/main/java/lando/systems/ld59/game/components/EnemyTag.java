package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

public class EnemyTag implements Component {

    public enum EnemyType { FLYER, KAMIKAZE, SPLITTER, MUNCHER, SUCKER }

    private final Engine engine;

    public final Entity entity;
    public final EnemyType enemyType;
    public final EnergyColor.Type energyColorType;

    public float FIRE_RATE = 6f;
    public float fireTimer = 0f;
    public float accumTimer = 0f;
    public int split = 0;
    public int MAX_SPLIT = 1;

    public EnemyTag(Engine engine, Entity entity, EnemyType enemyType, EnergyColor.Type energyColorType) {
        this.engine = engine;
        this.entity = entity;
        this.enemyType = enemyType;
        this.energyColorType = energyColorType;
    }
}
