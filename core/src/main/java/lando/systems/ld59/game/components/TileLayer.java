package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.renderable.Renderable;

public class TileLayer extends Renderable implements Component {

    public final Tilemap tilemap;
    public final TiledMapTileLayer tileLayer;

    public TileLayer(Tilemap tilemap, TiledMapTileLayer tileLayer, float depth) {
        this.tilemap = tilemap;
        this.tileLayer = tileLayer;
        this.depth = depth;
    }

    @Override
    public void render(SpriteBatch batch, Position position) {
        throw new GdxRuntimeException(getClass().getSimpleName() + " does not implement 'render()', see RenderSystem.renderTileLayer()");
    }

    public boolean isBackground() { return tileLayer.getName().equals("background"); }
    public boolean isMiddle()     { return tileLayer.getName().equals("middle"); }
    public boolean isForeground() { return tileLayer.getName().equals("foreground"); }

    // ------------------------------------------------------------------------
    // Convenience methods for stream filtering
    // ------------------------------------------------------------------------

    public static boolean isBackground(Entity entity) {
        return Components.optional(entity, TileLayer.class).map(TileLayer::isBackground).orElse(false);
    }
    public static boolean isMiddle(Entity entity) {
        return Components.optional(entity, TileLayer.class).map(TileLayer::isMiddle).orElse(false);
    }
    public static boolean isForeground(Entity entity) {
        return Components.optional(entity, TileLayer.class).map(TileLayer::isForeground).orElse(false);
    }
}
