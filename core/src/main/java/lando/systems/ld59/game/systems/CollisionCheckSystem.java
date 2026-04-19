package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.Collider;
import lando.systems.ld59.game.components.Position;
import lando.systems.ld59.game.components.collision.CollisionMask;
import lando.systems.ld59.game.signals.CollisionEvent;
import lando.systems.ld59.utils.FramePool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CollisionCheckSystem extends IteratingSystem {

    private static final String TAG = CollisionCheckSystem.class.getSimpleName();

    public CollisionCheckSystem() {
        super(Family.all(Position.class, Collider.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var collider = Components.get(entity, Collider.class);
        var overlaps = getOverlappingEntities(entity, collider.collidesWith(), 0, 0);
        for (var other : overlaps) {
            if (entity == other) continue;
            CollisionEvent.overlap(entity, other);
        }
    }


    public boolean check(Entity entity, List<CollisionMask> collidesWith, int offsetX, int offsetY) {
        return !getOverlappingEntities(entity, collidesWith, offsetX, offsetY).isEmpty();
    }

    public boolean check(Entity entity, int offsetX, int offsetY) {
        return (null != getFirstOverlappingEntity(entity, offsetX, offsetY));
    }

    public Entity getFirstOverlappingEntity(Entity entity, int offsetX, int offsetY) {
        var collidesWith = Components.optional(entity, Collider.class)
            .map(Collider::collidesWith)
            .orElse(Collections.emptyList());

        return getOverlappingEntities(entity, collidesWith, offsetX, offsetY)
            .stream().findFirst().orElse(null);
    }

    public List<Entity> getOverlappingEntities(Entity entity, List<CollisionMask> collidesWith, int offsetX, int offsetY) {
        var hitEntities = new ArrayList<Entity>();

        var a = new CollisionData(entity);
        var offset = FramePool.pi2(offsetX, offsetY);

        for (var other : getEntities()) {
            if (other == entity) continue;

            var b = new CollisionData(other);
            if (!collidesWith.contains(b.collider.mask())) {
                continue;
            }

            var aShape = a.collider.shape();
            var bShape = b.collider.shape();
            if (aShape.overlaps(bShape, a.position, b.position, offset)) {
                hitEntities.add(other);
            }
        }

        return hitEntities;
    }

    /**
     * Wrapper containing {@link Entity} and {@code Component}s used during collision checks and response
     */
    private static class CollisionData {

        public final Entity entity;
        public final Position position;
        public final Collider collider;

        CollisionData(Entity entity) {
            this(entity, Components.get(entity, Position.class), Components.get(entity, Collider.class));
        }

        CollisionData(Entity entity, Position position, Collider collider) {
            this.entity = Objects.requireNonNull(entity);
            this.position = Objects.requireNonNull(position);
            this.collider = Objects.requireNonNull(collider);
        }
    }
}
