package lando.systems.ld59.game.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.Name;
import lando.systems.ld59.game.components.Position;
import lando.systems.ld59.game.components.Tilemap;

public class MapFactory {

    // ------------------------------------------------------------------------
    // Map
    // ------------------------------------------------------------------------

    public static Entity map(String tmxFilePath, OrthographicCamera worldCamera) {
        var entity = Factory.createEntity();

        var name = new Name("map:" + tmxFilePath);
        var position = new Position(0, 0);
        var tilemap = new Tilemap(tmxFilePath, worldCamera);
        var collider = tilemap.newGridCollider();
        var bounds = tilemap.newBounds();

        entity.add(name);
        entity.add(position);
        entity.add(tilemap);
        entity.add(collider);
        entity.add(bounds);

        // TODO: Parse platforms for AI navigation, must be done after other components are added to entity
//        var platforms = NavPlatformParser.extractPlatforms(entity);
//        entity.add(new Platforms(entity, platforms));

        return entity;
    }
}
