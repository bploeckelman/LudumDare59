package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import lando.systems.ld59.Main;
import lando.systems.ld59.assets.SoundType;
import lando.systems.ld59.assets.anims.AnimBaseCity;
import lando.systems.ld59.assets.anims.AnimBaseTurret;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Stats;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.game.signals.AudioEvent;
import lando.systems.ld59.game.signals.ScreenShakeEvent;
import lando.systems.ld59.screens.GameScreen;

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
            ScreenShakeEvent.shake(1f);
            if (Main.game.currentScreen instanceof GameScreen) {
                GameScreen gameScreen = (GameScreen) Main.game.currentScreen;
                gameScreen.cityDeathModal.show();
            }
            Engine engine = Main.game.engine;
            // kill it all with fire....
            var enemies = engine.getEntitiesFor(enemyFamily);
            for (int i = enemies.size() -1; i>= 0; i--) {
                var isBoss = Components.has(enemies.get(i), Boss.class);
                if (!isBoss) {
                    var enemyTag = Components.get(enemies.get(i), EnemyTag.class);
                    engine.removeEntity(enemyTag.lightOverlay);
                    engine.removeEntity(enemies.get(i));
                }
            }

            engine.removeAllEntities(bulletFamily);
            cityHealth.currentHealth = cityHealth.maxHealth;

            // Fix shields
            var sheildHealth = Components.get(shield, Health.class);
            sheildHealth.currentHealth = sheildHealth.maxHealth;

            // Remove all connections
            var connectionEntities = engine.getEntitiesFor(Family.all(Connection.class).get());
            for (Entity connectionEntity : connectionEntities) {
                var connection = Components.get(connectionEntity, Connection.class);
                connection.removeConnection();
            }

            // Fix Turrets
            var turrets = engine.getEntitiesFor(Family.all(Turret.class).get());
            for (int i = turrets.size() -1; i>= 0; i--) {
                var turret = Components.get(turrets.get(i), Turret.class);
                var turretHealth = Components.get(turrets.get(i), Health.class);
                var baseAnim = Components.get(turret.base, Animator.class);
                baseAnim.start(AnimBaseTurret.BASE_IDLE);
                turret.base.add(turret.baseCollider);
                turret.cannon.add(turret.cannonCollider);
                turretHealth.currentHealth = turretHealth.maxHealth;
            }
            Stats.instance().cityLost++;

            // Let the user know what happened
            if (Main.game.currentScreen instanceof GameScreen) {
                GameScreen gameScreen = (GameScreen) Main.game.currentScreen;
                gameScreen.cityDeathModal.show();
            }
        }

        // update shield behavior
        var shieldHealth = Components.get(shield, Health.class);
        if (shieldHealth.isDead()) {
            shield.remove(Collider.class);
        } else {
            shield.add(base.shieldCollider);
        }

        // set City animation
        var cityHealthPercent = cityHealth.currentHealth / cityHealth.maxHealth;
        var cityAnimator = Components.get(city, Animator.class);
        var cityAnimBase = (AnimBaseCity) cityAnimator.type;
        var cityAnim = cityAnimBase.getFromPercent(cityHealthPercent);
//
//        if(Gdx.input.isButtonJustPressed(Input.Keys.T)) {
//            AudioEvent.playSound(SoundType.SHATTER, 0.5f);
//        }
        var oldCityAnime = (AnimBaseCity) cityAnimator.type;
        if (oldCityAnime != cityAnim) {
            switch (cityAnim) {
                case GLASS_BREAK:
                    AudioEvent.playSound(SoundType.SHATTER, 0.5f);
                    break;
                case CRACK_1:
                    AudioEvent.playSound(SoundType.CRACK1, 0.5f);
                    break;
                case CRACK_2:
                    AudioEvent.playSound(SoundType.CRACK2, 0.5f);
                    break;
                case CRACK_3:
                    AudioEvent.playSound(SoundType.CRACK1, 0.5f);
                    break;
                case CRACK_4:
                    AudioEvent.playSound(SoundType.CRACK2, 0.5f);
                    break;
            }
            if(cityAnim == AnimBaseCity.GLASS_BREAK ) {
                AudioEvent.playSound(SoundType.SHATTER, 0.5f);
            }
            cityAnimator.start(cityAnim);
        }

    }
}
