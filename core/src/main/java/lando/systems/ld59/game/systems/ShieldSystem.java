package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld59.Config;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.CityShield;
import lando.systems.ld59.game.components.Health;
import lando.systems.ld59.game.components.SceneContainer;
import lando.systems.ld59.game.components.renderable.ShieldShaderRenderable;
import lando.systems.ld59.game.signals.ScreenShakeEvent;
import lando.systems.ld59.game.signals.ShieldHitEvent;
import lando.systems.ld59.game.signals.SignalEvent;

import static lando.systems.ld59.game.Constants.CITY_SHIELD_REPAIR_TIME;

public class ShieldSystem extends IteratingSystem implements Listener<SignalEvent> {


    public ShieldSystem() {
        super(Family.one(CityShield.class).get());
        SignalEvent.addListener(this);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var health = Components.get(entity, Health.class);
        var cityShield = Components.get(entity, CityShield.class);
        var shieldShaderRender = Components.optional(entity, ShieldShaderRenderable.class).orElse(null);
        if (shieldShaderRender != null) {
            shieldShaderRender.update(deltaTime);
        }
        if (health == null) return;
        if (health.isDead()) {
            if (cityShield.repairTimer < 0) {
                cityShield.repairTimer = CITY_SHIELD_REPAIR_TIME;
            }

            cityShield.repairTimer -= deltaTime;

            if (cityShield.repairTimer <= 0) {
                health.currentHealth = health.maxHealth;
                entity.add(cityShield.collider);
            }
        }
    }

    @Override
    public void receive(Signal<SignalEvent> signal, SignalEvent event) {
        var isShieldHitEvent = event instanceof ShieldHitEvent;
        if (!isShieldHitEvent) return;

        var shieldHit = (ShieldHitEvent) event;
        var entities = getEntities();
        float x = MathUtils.map(Config.window_width/2f - 725, Config.window_width/2f + 725, 0, 1000, shieldHit.pos().x);
        float y = MathUtils.map(-1080, -1080 + 1460, 0, 1000, shieldHit.pos().y);
        Vector3 impact = new Vector3(x, y, 0);

        // translate this
        impact.x /= 1000f;
        impact.y /= 1000f;
        impact.y = 1 - impact.y;
        for (int i = 0; i < entities.size(); i++) {
            var shieldShaderRender = entities.get(i).getComponent(ShieldShaderRenderable.class);
            if (shieldShaderRender != null) {
                shieldShaderRender.addImpact(impact);
            }
        }
    }
}
