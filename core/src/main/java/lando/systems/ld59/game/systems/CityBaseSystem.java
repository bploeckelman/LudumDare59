package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import lando.systems.ld59.Main;
import lando.systems.ld59.assets.anims.AnimBaseCity;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.renderable.Animator;

public class CityBaseSystem extends IteratingSystem {

    public static final Family enemyFamily = Family.one(EnemyTag.class).get();
    public static final Family bulletFamily = Family.one(Projectile.class).get();

    public CityBaseSystem() {
        super(Family.one(CityBase.class).get());
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var base = Components.get(entity, Base.class);
        var shield = base.shield;
        var city = base.city;

        var cityHealth = Components.get(city, Health.class);
        if (cityHealth.isDead()) {
            Engine engine = Main.game.engine;
            // kill it all with fire....
            var enemies = engine.getEntitiesFor(enemyFamily);
            for (int i = enemies.size() -1; i>= 0; i--) {
                var isBoss = Components.has(enemies.get(i), Boss.class);
                if (!isBoss) {
                    engine.removeEntity(enemies.get(i));
                }
            }

            engine.removeAllEntities(bulletFamily);
            cityHealth.currentHealth = cityHealth.maxHealth;

            // Fix shields
            var sheildHealth = Components.get(shield, Health.class);
            sheildHealth.currentHealth = sheildHealth.maxHealth;

            // Fix Turrets
            var turrets = engine.getEntitiesFor(Family.all(Turret.class).get());
            for (int i = turrets.size() -1; i>= 0; i--) {
                var turretHealth = Components.get(turrets.get(i), Health.class);
                turretHealth.currentHealth = turretHealth.maxHealth;
            }


            // Let the user know what happened
        }

        // update shield behavior


        // set City animation
        var cityHealthPercent = cityHealth.currentHealth / cityHealth.maxHealth;
        var cityAnimator = Components.get(city, Animator.class);
        var cityAnimBase = (AnimBaseCity) cityAnimator.type;
        var cityAnim = cityAnimBase.getFromPercent(cityHealthPercent);

        var oldCityAnime = (AnimBaseCity) cityAnimator.type;
        if (oldCityAnime != cityAnim) {
            cityAnimator.start(cityAnim);
        }

    }
}
