package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.Position;
import lando.systems.ld59.game.components.renderable.Animator;
import lando.systems.ld59.game.components.renderable.Image;
import lando.systems.ld59.game.components.renderable.Renderable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class RenderSystem extends SortedIteratingSystem {

    private static final Family RENDERABLES = Family
        .one(Image.class, Animator.class).get();

    private static final Comparator<Entity> comparator = (e1, e2) -> {
        var r1 = Renderable.getRenderable(e1);
        var r2 = Renderable.getRenderable(e2);
        float e1Depth = r1 == null ? 0 : r1.depth;
        float e2Depth = r2 == null ? 0 : r2.depth;
        return (int)(e1Depth - e2Depth);
    };

    private final Map<Entity, TiledMapRenderer> mapRenderers;
    private float accum = 0;

    public RenderSystem() {
        super(RENDERABLES, comparator);
        this.mapRenderers = new HashMap<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        accum += deltaTime;
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
    }

    public void draw(SpriteBatch batch) {
        for (var entity : getEntities()) {
            var pos = Components.optional(entity, Position.class).orElse(Position.ZERO);

            // Draw simple renderables
            Components.optional(entity, Image.class).ifPresent(img -> img.render(batch, pos));
            Components.optional(entity, Animator.class).ifPresent(anim -> anim.render(batch, pos));

        }
    }

    /**
     * For drawing stuff in 'window' space rather than 'world' space, typically for shader effects
     */
    public void drawInWindowSpace(SpriteBatch batch, OrthographicCamera camera) {
    }


}
