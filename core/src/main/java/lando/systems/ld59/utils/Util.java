package lando.systems.ld59.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld59.Flag;
import lando.systems.ld59.assets.FontType;
import lando.systems.ld59.game.components.SceneContainer;
import lando.systems.ld59.game.scenes.Scene;
import lando.systems.ld59.screens.BaseScreen;

import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Util {

    // ------------------------------------------------------------------------
    // Ashley / ECS related
    // ------------------------------------------------------------------------

    public static final Family SCENE_CONTAINER = Family.one(SceneContainer.class).get();

    @SuppressWarnings("unchecked")
    public static Scene<? extends BaseScreen> findScene(Engine engine) {
        return Util.streamOf(engine.getEntitiesFor(SCENE_CONTAINER))
            .map(SceneContainer::get)
            .map(SceneContainer::scene)
            .findFirst()
            .orElse(null);
    }

    public static Optional<Entity> getPlayerEntity(Engine engine) {
        return Optional.ofNullable(Util.findScene(engine)).map(Scene::player);
    }

    public static String entityString(Entity entity) {
        StringBuilder sb = new StringBuilder();
        entity.getComponents().forEach(c -> sb.append(c.getClass().getSimpleName()).append(", "));
        return sb.toString();
    }

    // ------------------------------------------------------------------------
    // Enum related
    // ------------------------------------------------------------------------

    public <E extends Enum<E>> E randomEnumValue(Class<E> enumClass) {
        var constants = enumClass.getEnumConstants();
        if (constants.length == 0) {
            throw new GdxRuntimeException("Enum has no values!");
        }
        return constants[MathUtils.random(constants.length - 1)];
    }

    // ------------------------------------------------------------------------
    // Path related
    // ------------------------------------------------------------------------

    public static Array<Vector2> generateStraightPath(Vector2 start, Vector2 end) {
        var points = new Array<Vector2>();
        float segmentLength = 20f; // arbitrary, but determines 'density' of points... e.g. 200px rope gets ~10 points, 400px rope gets ~20
        float totalDist = start.dst(end);
        int segments = Math.max(3, MathUtils.round(totalDist / segmentLength));

        for (int i = 0; i <= segments; i++) {
            float t = (float) i / segments;
            points.add(new Vector2(
                    MathUtils.lerp(start.x, end.x, t),
                    MathUtils.lerp(start.y, end.y, t)
            ));
        }
        return points;
    }

    public static Array<Vector2> generateStraightPath(Vector2 start, Vector2 end, int segments) {
        if (segments < 3) {
            throw new IllegalArgumentException("segments must be at least 3");
        }
        var points = new Array<Vector2>();
        for (int i = 0; i <= segments; i++) {
            float t = (float) i / segments;
            points.add(new Vector2(
                MathUtils.lerp(start.x, end.x, t),
                MathUtils.lerp(start.y, end.y, t)
            ));
        }
        return points;
    }

    /**
     * Generates points along a line between start to end, with internal points randomly displaced perpendicularly from
     * the line for variation.
     */
    public static Array<Vector2> generateDisplacedPath(Vector2 start, Vector2 end) {
        var defaultDensity = 0.1f;
        var defaultMaxDisplacement = 20f;
        return generateDisplacedPath(start, end, defaultDensity, defaultMaxDisplacement);
    }

    /**
     * Generates points along a line between start to end, with internal points randomly displaced perpendicularly from
     * the line for variation, customizing the number of points along the length based on {@code density} and the
     * maximum perpendicular displacement distance from the line with {@code maxDisplacement}
     */
    public static Array<Vector2> generateDisplacedPath(Vector2 start, Vector2 end, float density, float maxDisplacement) {
        var points = new Array<Vector2>();

        var direction = FramePool.vec2(end).sub(start);
        var distance = direction.len();
        direction.nor();

        var perpendicular = FramePool.vec2(-direction.y, direction.x);

        var numPoints = Math.max(2, Math.round(distance * density));
        for (int i = 0; i < numPoints; i++) {
            var t = (float) i / (numPoints - 1);
            var point = new Vector2(start).lerp(end, t);

            // Displace this point perpendicularly from line, if it's not the start or end point
            if (i > 0 && i < numPoints - 1) {
                var displacement = (MathUtils.random() - 0.5f) * 2 * maxDisplacement;
                point.add(perpendicular.x * displacement, perpendicular.y * displacement);
            }

            points.add(point);
        }
        return points;
    }

    /**
     * Produce a new array of points by smoothing points in the specified array (reducing the jaggedness between points)
     */
    public static Array<Vector2> smoothPath(Array<Vector2> controlPoints) {
        var defaultSamplingDensity = 1f;
        return smoothPath(controlPoints, defaultSamplingDensity);
    }

    /**
     * Produce a new array of points by smoothing points in the specified array (reducing the jaggedness between points)
     * @param samplingDensity 1f produces a slightly smoothed path, 0.5-1.0 for improved performance,
     *                       2.0-3.0 for a more heavily smoothed path (high fidelity), avoid < 0.5 as it doesn't smooth much at all.
     */
    public static Array<Vector2> smoothPath(Array<Vector2> controlPoints, float samplingDensity) {
        if (controlPoints.size <= 2) {
            return controlPoints;
        }

        var points = controlPoints.toArray(Vector2[]::new);
        var spline = new CatmullRomSpline<>(points, false);

        // Estimate path length
        float estimatedLength = 0;
        for (int i = 0; i < controlPoints.size - 1; i++) {
            estimatedLength += controlPoints.get(i).dst(controlPoints.get(i + 1));
        }

        // Calculate sample count based on density
        int numSamples = Math.max(2, Math.round(estimatedLength * samplingDensity));

        var sample = new Vector2();
        var smoothedPath = new Array<Vector2>();
        for (int i = 0; i < numSamples; i++) {
            float t = (float) i / (numSamples - 1);
            spline.valueAt(sample, t);
            smoothedPath.add(new Vector2(sample));
        }

        return smoothedPath;
    }

    // ------------------------------------------------------------------------
    // Collection / Stream related
    // ------------------------------------------------------------------------

    public static <T> Stream<T> streamOf(Iterable<T> iterable) {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(iterable.iterator(), Spliterator.ORDERED),
            false // parallel = false
        );
    }

    // ------------------------------------------------------------------------
    // Font related
    // ------------------------------------------------------------------------

    // TODO: maybe just do this for all fonts directly in FontType2 instead of rewriting the font after loading?
    public static void addGameIconsToFont(FontType fontType) {
        // sheesh... all this just to initialize the game icon fonts at an appropriate size

        // NOTE: this is only needed if we're using `KnownFonts.addGameIcons(otherFont)`
        // KnownFonts.setAssetPrefix("fonts/textra/");

        // TODO: this could be done in `Skins.load()`, or in `Fonts.init()`
        //  after `Fonts` switches from BitmapFont -> Font (textra)...
        //  except we might not be able to attach game icons to multiple fonts?
        //  there's a single internal instance when using KnownFonts.addGameIcons(...),
        //  but we're doing it live here instead, so :shrug:, just gotta test it out

        var atlas = Gdx.files.internal("fonts/textra/Game-Icons.atlas");
        if (Gdx.files.internal("fonts/textra/Game-Icons.png").exists()) {
            var gameIcons = new TextureAtlas(atlas, atlas.parent(), false);
            var font = fontType.get();
            // NOTE: doing this manually instead of `font = KnownFonts.addGameIcons(font);`
            //  because the `offsetXChange` value in `addAtlas` below shifts left by 20px
            //  for some unknown reason, resulting in icons floating too far left
            // TODO: ping ettinger about this, I wouldn't be surprised if my usage is wrong
            font.addAtlas(gameIcons, "", "", 0, 0, 0);
        }
    }

    // ------------------------------------------------------------------------
    // Logging related
    // ------------------------------------------------------------------------

    public static void log(String msg) {
        if (Flag.LOG_GENERAL.isDisabled()) return;
        Gdx.app.log(Util.class.getSimpleName(), msg);
    }

    public static void log(String tag, String msg) {
        if (Flag.LOG_GENERAL.isDisabled()) return;
        Gdx.app.log(tag, msg);
    }

    public static void warn(String msg) {
        if (Flag.LOG_WARN.isDisabled()) return;
        Util.log("WARN: " + msg);
    }

    public static void warn(String tag, String msg) {
        if (Flag.LOG_WARN.isDisabled()) return;
        Util.log(tag, "WARN: " + msg);
    }

    // ------------------------------------------------------------------------
    // Shader related
    // ------------------------------------------------------------------------

    public static ShaderProgram loadShader(String vertSourcePath, String fragSourcePath) {
        ShaderProgram.pedantic = false;
        var shaderProgram = new ShaderProgram(
            Gdx.files.internal(vertSourcePath),
            Gdx.files.internal(fragSourcePath));
        var log = shaderProgram.getLog();

        if (!shaderProgram.isCompiled()) {
            if (Flag.LOG_GENERAL.isEnabled()) {
                Gdx.app.error("LoadShader", "compilation failed:\n" + log);
            }
            throw new GdxRuntimeException("LoadShader: compilation failed:\n" + log);
        } else if (Flag.LOG_GENERAL.isEnabled()) {
            Gdx.app.debug("LoadShader", "ShaderProgram compilation log: " + log);
        }

        return shaderProgram;
    }

    // ------------------------------------------------------------------------
    // Color related
    // ------------------------------------------------------------------------

    private static final List<Color> colors = List.of(
        /* grayscale */ Color.WHITE, Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY, Color.BLACK,
        /* reds      */ Color.FIREBRICK, Color.RED, Color.SCARLET, Color.CORAL, Color.SALMON,
        /* greens    */ Color.GREEN, Color.CHARTREUSE, Color.LIME, Color.FOREST, Color.OLIVE,
        /* blues     */ Color.BLUE, Color.NAVY, Color.ROYAL, Color.SLATE, Color.SKY, Color.CYAN, Color.TEAL,
        /* yellows   */ Color.YELLOW, Color.GOLD, Color.GOLDENROD, Color.ORANGE, Color.BROWN, Color.TAN,
        /* purples   */ Color.PINK, Color.MAGENTA, Color.PURPLE, Color.VIOLET, Color.MAROON);

    public static Color randomColor() {
        var index = MathUtils.random(colors.size() - 1);
        return colors.get(index);
    }

    public static Color colorWithAlpha(Color color, float alpha) {
        var c = color.cpy();
        c.a = alpha;
        return c;
    }

    public static String colorToHexString(Color color) {
        int r = Math.round(color.r * 255);// << 24;
        int g = Math.round(color.g * 255);// << 16;
        int b = Math.round(color.b * 255);// << 8;
        int a = Math.round(color.a * 255);
        return Stringf.format("%02X%02X%02X%02X", r, g, b, a);
    }

    public static Color hsvToRgb(float hue, float saturation, float value, Color outColor) {
        if (outColor == null) {
            outColor = new Color();
        }

        // rotate hue into positive range
        while (hue < 0) hue += 10f;

        hue = hue % 1f;
        int h = (int) (hue * 6);
        h = h % 6;

        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        //@formatter:off
        switch (h) {
            case 0: outColor.set(value, t, p, 1f); break;
            case 1: outColor.set(q, value, p, 1f); break;
            case 2: outColor.set(p, value, t, 1f); break;
            case 3: outColor.set(p, q, value, 1f); break;
            case 4: outColor.set(t, p, value, 1f); break;
            case 5: outColor.set(value, p, q, 1f); break;
            default: Util.log("HSV->RGB", Stringf.format("Failed to convert HSV->RGB(h: %f, s: %f, v: %f)", hue, saturation, value));
        }
        return outColor;
        //@formatter:on
    }

    // ------------------------------------------------------------------------
    // Drawing related
    // ------------------------------------------------------------------------
    private static final Color prevColor = Color.WHITE.cpy();

    // Circle convenience methods -------------------------

    public static void draw(SpriteBatch batch, TextureRegion texture, Circle circle, Color tint) {
        draw(batch, texture, circle, tint, 1f);
    }

    public static void draw(SpriteBatch batch, TextureRegion texture, Circle circle, Color tint, float scale) {
        var x = circle.x;
        var y = circle.y;
        var r = circle.radius * scale;
        prevColor.set(batch.getColor());
        batch.setColor(tint);
        batch.draw(texture, x - r, y - r, 2 * r, 2 * r);
        batch.setColor(prevColor);
    }

    // Rectangle convenience methods ----------------------

    public static void draw(SpriteBatch batch, TextureRegion texture, Rectangle rect) {
        draw(batch, texture, rect, Color.WHITE);
    }

    public static void draw(SpriteBatch batch, TextureRegion texture, Rectangle rect, Color tint) {
        draw(batch, texture, rect, tint, 1f, 1f);
    }

    public static void draw(SpriteBatch batch, TextureRegion texture, Rectangle rect, Color tint, float scaleX, float scaleY) {
        draw(batch, texture, rect, tint, rect.width / 2f, rect.height / 2f, scaleX, scaleY, 0f);
    }

    public static void draw(SpriteBatch batch, TextureRegion texture, Rectangle rect, Color tint, float ox, float oy, float sx, float sy, float rot) {
        var x = rect.x;
        var y = rect.y;
        var h = rect.height;
        var w = rect.width;
        prevColor.set(batch.getColor());
        batch.setColor(tint);
        batch.draw(texture, x, y, ox, oy, w, h, sx, sy, rot);
        batch.setColor(prevColor);
    }

    public static void draw(SpriteBatch batch, Texture texture, Rectangle rect, Color tint, float ox, float oy, float sx, float sy, float rot) {
        var x = rect.x;
        var y = rect.y;
        var h = rect.height;
        var w = rect.width;
        prevColor.set(batch.getColor());
        batch.setColor(tint);
        batch.draw(texture, x, y, ox, oy, w, h, sx, sy, rot, 0, 0, (int) w, (int) h, false, false);
        batch.setColor(prevColor);
    }

    public static void draw(SpriteBatch batch, NinePatch ninePatch, Rectangle rect) {
        draw(batch, ninePatch, rect, Color.WHITE);
    }

    public static void draw(SpriteBatch batch, NinePatch ninePatch, Rectangle rect, Color tint) {
        draw(batch, ninePatch, rect, tint, 1f);
    }

    public static void draw(SpriteBatch batch, NinePatch ninePatch, Rectangle rect, Color tint, float scale) {
        draw(batch, ninePatch, rect, tint, rect.width / 2f, rect.height / 2f, scale, scale, 0f);
    }

    public static void draw(SpriteBatch batch, NinePatch ninePatch, Rectangle rect, Color tint, float ox, float oy, float sx, float sy, float rot) {
        var x = rect.x;
        var y = rect.y;
        var h = rect.height;
        var w = rect.width;
        prevColor.set(batch.getColor());
        batch.setColor(tint);
        ninePatch.draw(batch, x, y, ox, oy, w, h, sx, sy, rot);
        batch.setColor(prevColor);
    }
}
