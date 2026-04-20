package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.assets.anims.AnimEnemy;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.renderable.Animator;

public class EnemyTag implements Component {

    public enum EnemyType { FLYER, KAMIKAZE, SPLITTER }

    private final Engine engine;

    public final Entity entity;
    public final Entity lightOverlay;
    public final EnemyType enemyType;
    public final EnergyColor.Type energyColorType;

    public float FIRE_RATE = 6f;
    public float fireTimer = 0f;
    public float accumTimer = 0f;
    public float zapTimer = 0f;
    public float randomOffset = 0f;
    public int split = 0;
    public int MAX_SPLIT = 1;

    // Flyer drift behavior
    public Vector2 driftDirection = new Vector2();
    public float driftChangeTimer = 0f;

    public EnemyTag(Engine engine, Entity entity, Position pos, Animator anim, EnemyType enemyType) {
        this.engine = engine;
        this.entity = entity;
        this.enemyType = enemyType;
        this.energyColorType = EnergyColor.of(enemyType);

        this.lightOverlay = Factory.createEntity();
        var energyColor = energyColorType.getColor();
        var lightAnimType = AnimEnemy.of(enemyType).getLightOverlay();
        var lightAnimator = new Animator(lightAnimType, new Vector2(anim.size), new Vector2(anim.origin));
        lightAnimator.scale.set(anim.scale);
        lightAnimator.tint.set(energyColor.r, energyColor.g, energyColor.b, anim.tint.a);
        lightAnimator.depth = anim.depth + 1; // on top of base animator
        lightOverlay.add(new Position(pos));
        lightOverlay.add(lightAnimator);
        engine.addEntity(lightOverlay);
    }
}
