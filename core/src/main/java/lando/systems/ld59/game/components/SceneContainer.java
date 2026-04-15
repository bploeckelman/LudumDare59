package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.scenes.Scene;
import lombok.RequiredArgsConstructor;

/**
 * Access to {@link Scene} object via {@link com.badlogic.ashley.core.Engine}
 */
@RequiredArgsConstructor
public class SceneContainer implements Component {

    public final Scene scene;

    public Scene scene() { return scene; }

    public static SceneContainer get(Entity entity) {
        return Components.get(entity, SceneContainer.class);
    }
}
