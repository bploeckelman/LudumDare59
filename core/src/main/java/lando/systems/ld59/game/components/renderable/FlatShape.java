package lando.systems.ld59.game.components.renderable;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld59.Main;
import lando.systems.ld59.game.components.Position;
import lando.systems.ld59.utils.Calc;
import space.earlygrey.shapedrawer.JoinType;import space.earlygrey.shapedrawer.ShapeDrawer;

public class FlatShape extends Renderable implements Component {

    private final ShapeDrawer shapes;
    private final TypeData typeData;

    // TODO: add more shape types as they become useful by implementing TypeData and a factory method for each

    public static FlatShape line(float depth, float x1, float y1, float x2, float y2, Color color, float width) {
        var data = new LineData();
        data.start.set(x1, y1);
        data.end.set(x2, y2);
        data.width = Calc.abs(width);
        return new FlatShape(depth, data, color);
    }

    public static FlatShape path(float depth, Array<Vector2> points, Color color, float width) {
        var data = new PathData();
        data.points = points; // NOTE: setting reference on purpose rather than copying, so updates to SpringPath will apply
        data.width = width;
        return new FlatShape(depth, data, color);
    }

    private FlatShape(float depth, TypeData typeData) {
        this(depth, typeData, null);
    }

    private FlatShape(float depth, TypeData typeData, Color color) {
        this.depth = depth;
        this.shapes = Main.game.assets.shapes;
        this.typeData = typeData;
        if (color != null) {
            this.tint.set(color);
        }
    }

    @Override
    public void render(SpriteBatch batch, Position position) {
        if (typeData instanceof LineData) {
            var line = (LineData) typeData;
            shapes.line(line.start, line.end, tint, line.width);
        } else if (typeData instanceof PathData) {
            var path = (PathData) typeData;
            shapes.setColor(tint);
            shapes.path(path.points, path.width, path.joinType, path.open);
            shapes.setColor(Color.WHITE);
        }
    }

    private interface TypeData {}

    private static class LineData implements TypeData {
        public Vector2 start = new Vector2();
        public Vector2 end = new Vector2();
        public float width = 1f;
    }

    private static class PathData implements TypeData {
        public Array<Vector2> points = new Array<>();
        public JoinType joinType = JoinType.SMOOTH;
        public float width = 1f;
        public boolean open = true;
    }
}
