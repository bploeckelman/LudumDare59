package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.Config;
import lando.systems.ld59.assets.anims.AnimMisc;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.BackgroundAnimSpawner;
import lando.systems.ld59.game.components.Position;
import lando.systems.ld59.game.components.Timer;
import lando.systems.ld59.game.components.Velocity;
import lando.systems.ld59.game.components.renderable.Image;

public class BackgroundAnimSpawnerSystem extends IteratingSystem {

    private static final float PADDING = 20f;
    private final Family backgroundAnimFamily;

    public BackgroundAnimSpawnerSystem() {
        super(Family.all(BackgroundAnimSpawner.class).get());
        backgroundAnimFamily = Family.all(Position.class, Velocity.class, Image.class)
                .exclude(BackgroundAnimSpawner.class).get();
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var spawner = Components.get(entity, BackgroundAnimSpawner.class);

        spawner.spawnTimer -= delta;
        if (spawner.spawnTimer > 0) return;

        spawnBackgroundAnim();
        spawner.spawnTimer = spawner.spawnInterval;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        removeOutOfBoundsEntities();
    }

    private void removeOutOfBoundsEntities() {
        ImmutableArray<Entity> entities = getEngine().getEntitiesFor(backgroundAnimFamily);
        for (Entity entity : entities) {
            var position = Components.get(entity, Position.class);
            if (position == null) continue;

            if (position.x < -PADDING || position.x > Config.window_width + PADDING ||
                position.y < -PADDING || position.y > Config.window_height + PADDING) {
                getEngine().removeEntity(entity);
            }
        }
    }

    private void spawnBackgroundAnim() {
        AnimMisc[] types = {AnimMisc.PLANET, AnimMisc.ASTROID, AnimMisc.GALAXY};
        AnimMisc animType = types[MathUtils.random(types.length - 1)];

        var anim = animType.get();
        var keyFrameIndex = MathUtils.random(anim.getKeyFrames().length - 1);
        var region = anim.getKeyFrames()[keyFrameIndex];

        var edge = MathUtils.random(3);
        float x, y, vx, vy;

        switch (edge) {
            case 0: // Left
                x = -10;
                y = MathUtils.random(Config.window_height);
                vx = MathUtils.random(10f, 30f);
                vy = MathUtils.random(-15f, 15f);
                break;
            case 1: // Right
                x = Config.window_width + 10;
                y = MathUtils.random(Config.window_height);
                vx = MathUtils.random(-30f, -10f);
                vy = MathUtils.random(-15f, 15f);
                break;
            case 2: // Top
                x = MathUtils.random(Config.window_width);
                y = Config.window_height + 10;
                vx = MathUtils.random(-15f, 15f);
                vy = MathUtils.random(-30f, -10f);
                break;
            default: // bottom
                x = MathUtils.random(Config.window_width);
                y = -10;
                vx = MathUtils.random(-15f, 15f);
                vy = MathUtils.random(10f, 30f);
                break;
        }

        var entity = Factory.createEntity();
        var position = new Position(x, y);
        var velocity = new Velocity(vx, vy);

        float scale = MathUtils.random(0.5f, 1.5f);
        Vector2 size = new Vector2(region.getRegionWidth() * scale, region.getRegionHeight() * scale);

        var image = new Image(region, size);
        image.tint.a = .5f;
        image.depth = -900;
        image.rotation = MathUtils.random(360f);

        entity.add(position);
        entity.add(velocity);
        entity.add(image);

        getEngine().addEntity(entity);
    }
}
