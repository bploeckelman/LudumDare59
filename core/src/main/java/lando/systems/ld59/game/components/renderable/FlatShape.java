package lando.systems.ld59.game.components.renderable;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.Main;
import lando.systems.ld59.game.components.Position;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class FlatShape extends Renderable implements Component {

    public final Vector2 start;
    public final Vector2 end;
    public float width;

    private final ShapeDrawer shapes;

    // TODO: just line to start, add more shape types if they become useful...

    public FlatShape(float x1, float y1, float x2, float y2, Color color, float width) {
        this.start = new Vector2(x1, y1);
        this.end = new Vector2(x2, y2);
        this.width = width;
        this.shapes = Main.game.assets.shapes;
        this.tint.set(color);
    }

    @Override
    public void render(SpriteBatch batch, Position position) {
        shapes.line(start, end, tint, width);
    }
}
