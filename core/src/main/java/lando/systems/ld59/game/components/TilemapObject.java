package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lombok.AllArgsConstructor;

public interface TilemapObject {

    String TAG = TilemapObject.class.getSimpleName();

    Tilemap tilemap();
    MapObject mapObject();

    static Entity createEntity(Tilemap tilemap, MapObject mapObject) {
        var component
            = mapObject.getName().equals("spawner") ? new Spawner(tilemap, mapObject)
            : mapObject.getName().equals("trigger") ? new Trigger(tilemap, mapObject)
            : new Simple(tilemap, mapObject);

        var entity = Factory.createEntity();
        entity.add(component);
        return entity;
    }

    class Simple implements TilemapObject, Component {

        public final Tilemap tilemap;
        public final MapObject mapObject;

        public Simple(Tilemap tilemap, MapObject mapObject) {
            this.tilemap = tilemap;
            this.mapObject = mapObject;
        }

        @Override
        public Tilemap tilemap() {
            return tilemap;
        }

        @Override
        public MapObject mapObject() {
            return mapObject;
        }
    }

    class Spawner implements TilemapObject, Component {

        /**
         * Convenience method for stream operations on entities
         */
        public static Spawner get(Entity entity) {
            return Components.get(entity, Spawner.class);
        }

        public final Tilemap tilemap;
        public final MapObject mapObject;
        public final String type;
        public final int id;
        public final int x;
        public final int y;

        public Spawner(Tilemap tilemap, MapObject object) {
            this(tilemap, object,
                object.getProperties().get("type", "", String.class),
                object.getProperties().get("id", -1, Integer.class),
                object.getProperties().get("x", 0f, Float.class).intValue(),
                object.getProperties().get("y", 0f, Float.class).intValue());
        }

        public Spawner(Tilemap tilemap, MapObject object, String type, int id, int x, int y) {
            this.tilemap = tilemap;
            this.mapObject = object;
            this.type = type;
            this.id = id;
            this.x = x;
            this.y = y;
        }

        public String type() { return type; }
        public int id() { return id; }
        public int x() { return x; }
        public int y() { return y; }

        @SuppressWarnings("unchecked")
        public <T extends MapObject> T mapObject(Class<T> mapObjectClass) {
            if (!ClassReflection.isInstance(mapObjectClass, mapObject)) {
                throw new GdxRuntimeException(Stringf.format("%s: %s is not an instance of %s",
                    TAG, mapObject.getClass().getSimpleName(), mapObjectClass.getSimpleName()));
            }
            return (T) mapObject;
        }

        @Override
        public Tilemap tilemap() {
            return tilemap;
        }

        @Override
        public MapObject mapObject() {
            return mapObject;
        }
    }

    class Trigger implements TilemapObject, Component {

        public final Tilemap tilemap;
        public final MapObject mapObject;
        public final String type;
        public final int id;
        public final Rectangle bounds;

        public boolean activated;

        public Trigger(Tilemap tilemap, MapObject object) {
            this(tilemap, object,
                object.getProperties().get("type", "", String.class),
                object.getProperties().get("id", -1, Integer.class),
                ((RectangleMapObject) object).getRectangle(),
                false);
        }

        public Trigger(Tilemap tilemap, MapObject object, String type, int id, Rectangle bounds, boolean activated) {
            this.tilemap = tilemap;
            this.mapObject = object;
            this.type = type;
            this.id = id;
            this.bounds = bounds;
            this.activated = activated;
        }

        public String type() { return type; }
        public int id() { return id; }
        public Rectangle bounds() { return bounds; }

        @Override
        public Tilemap tilemap() {
            return tilemap;
        }

        @Override
        public MapObject mapObject() {
            return mapObject;
        }
    }
}
