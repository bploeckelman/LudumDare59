package lando.systems.ld59.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.PoolManager;
import com.github.tommyettinger.gdcrux.PointF2;
import com.github.tommyettinger.gdcrux.PointI2;
import com.github.tommyettinger.gdcrux.PointI3;
import com.github.tommyettinger.gdcrux.PointI4;
import lando.systems.ld59.game.components.Position;

public class FramePool {

    private final static FramePool instance = new FramePool();

    public static FramePool get() {
        return instance;
    }

    private final PoolManager pools;

    private final Pool<Vector2> vec2;
    private final Pool<Vector3> vec3;
    private final Pool<PointF2> pf2;
    private final Pool<PointI2> pi2;
    private final Pool<PointI3> pi3;
    private final Pool<PointI4> pi4;
    private final Pool<Position> pos;
    private final Pool<Circle> circle;
    private final Pool<Rectangle> rect;
    private final Pool<Color> color;

    private final Array<Vector2> vec2Active;
    private final Array<Vector3> vec3Active;
    private final Array<PointF2> pf2Active;
    private final Array<PointI2> pi2Active;
    private final Array<PointI3> pi3Active;
    private final Array<PointI4> pi4Active;
    private final Array<Position> posActive;
    private final Array<Circle> circleActive;
    private final Array<Rectangle> rectActive;
    private final Array<Color> colorActive;

    private FramePool() {
        this.pools = new PoolManager();
        this.pools.addPool(Vector2::new);
        this.pools.addPool(Vector3::new);
        this.pools.addPool(PointF2::new);
        this.pools.addPool(PointI2::new);
        this.pools.addPool(PointI3::new);
        this.pools.addPool(PointI4::new);
        this.pools.addPool(Position::new);
        this.pools.addPool(Circle::new);
        this.pools.addPool(Rectangle::new);
        this.pools.addPool(Color::new);

        this.vec2   = pools.getPool(Vector2.class);
        this.vec3   = pools.getPool(Vector3.class);
        this.pf2    = pools.getPool(PointF2.class);
        this.pi2    = pools.getPool(PointI2.class);
        this.pi3    = pools.getPool(PointI3.class);
        this.pi4    = pools.getPool(PointI4.class);
        this.pos    = pools.getPool(Position.class);
        this.circle = pools.getPool(Circle.class);
        this.rect   = pools.getPool(Rectangle.class);
        this.color  = pools.getPool(Color.class);

        this.vec2Active   = new Array<>();
        this.vec3Active   = new Array<>();
        this.pf2Active    = new Array<>();
        this.pi2Active    = new Array<>();
        this.pi3Active    = new Array<>();
        this.pi4Active    = new Array<>();
        this.posActive    = new Array<>();
        this.circleActive = new Array<>();
        this.rectActive   = new Array<>();
        this.colorActive  = new Array<>();
    }

    public void resetAll() {
        vec2.freeAll(vec2Active);
        vec3.freeAll(vec3Active);
        pf2.freeAll(pf2Active);
        pi2.freeAll(pi2Active);
        pi3.freeAll(pi3Active);
        pi4.freeAll(pi4Active);
        pos.freeAll(posActive);
        circle.freeAll(circleActive);
        rect.freeAll(rectActive);
        color.freeAll(colorActive);
    }

    public void clear() {
        pools.clear();
    }

    // ------------------------------------------------------------------------
    // Vector types
    // ------------------------------------------------------------------------

    public static Vector2 vec2() {
        var obj = instance.vec2.obtain();
        instance.vec2Active.add(obj);
        return obj;
    }

    public static Vector2 vec2(float x, float y) {
        return vec2().set(x, y);
    }

    public static Vector3 vec3() {
        var obj = instance.vec3.obtain();
        instance.vec3Active.add(obj);
        return obj;
    }

    public static Vector3 vec3(Vector2 xy) {
        return vec3(xy.x, xy.y);
    }

    public static Vector3 vec3(float x, float y) {
        return vec3(x, y, 0);
    }

    public static Vector3 vec3(float x, float y, float z) {
        return vec3().set(x, y, z);
    }

    // ------------------------------------------------------------------------
    // Point types
    // ------------------------------------------------------------------------

    public static PointF2 pf2() {
        var obj = instance.pf2.obtain();
        instance.pf2Active.add(obj);
        return obj;
    }

    public static PointF2 pf2(float x, float y) {
        return pf2().set(x, y);
    }

    public static PointI2 pi2() {
        var obj = instance.pi2.obtain();
        instance.pi2Active.add(obj);
        return obj;
    }

    public static PointI2 pi2(int x, int y) {
        return pi2().set(x, y);
    }

    public static PointI3 pi3() {
        var obj = instance.pi3.obtain();
        instance.pi3Active.add(obj);
        return obj;
    }

    public static PointI3 pi3(int x, int y, int z) {
        return pi3().set(x, y, z);
    }

    public static PointI4 pi4() {
        var obj = instance.pi4.obtain();
        instance.pi4Active.add(obj);
        return obj;
    }

    public static PointI4 pi4(int x, int y, int z, int w) {
        return pi4().set(x, y, z, w);
    }

    // ------------------------------------------------------------------------
    // Components
    // ------------------------------------------------------------------------

    public static Position pos() {
        var obj = instance.pos.obtain();
        instance.posActive.add(obj);
        return obj;
    }

    public static Position pos(int x, int y) {
        return pos().set(x, y);
    }

    public static Position pos(Position other) {
        return pos().set(other);
    }

    // ------------------------------------------------------------------------
    // Circle
    // ------------------------------------------------------------------------

    public static Circle circle() {
        var obj = instance.circle.obtain();
        instance.circleActive.add(obj);
        return obj;
    }

    public static Circle circle(float x, float y, float r) {
        // NOTE: Circle is the only Shape2D that doesn't return 'this' for chaining
        var c = circle();
        c.set(x, y, r);
        return c;
    }

    // ------------------------------------------------------------------------
    // Rect
    // ------------------------------------------------------------------------

    public static Rectangle rect() {
        var obj = instance.rect.obtain();
        instance.rectActive.add(obj);
        return obj;
    }

    public static Rectangle rect(float x, float y, float w, float h) {
        return rect().set(x, y, w, h);
    }

    // ------------------------------------------------------------------------
    // Color
    // ------------------------------------------------------------------------

    public static Color color() {
        var obj = instance.color.obtain();
        instance.colorActive.add(obj);
        return obj;
    }

    public static Color color(float r, float g, float b) {
        return color(r, g, b, 1);
    }

    public static Color color(float r, float g, float b, float a) {
        return color().set(r, g, b, a);
    }
}
