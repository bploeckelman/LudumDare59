package lando.systems.ld59.game.components.renderable;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld59.assets.ImageType;
import lando.systems.ld59.assets.ShaderType;
import lando.systems.ld59.game.components.Connection;
import lando.systems.ld59.utils.FramePool;
import lando.systems.ld59.utils.RopePath;

public class CableShaderRenderable extends ShaderRenderable implements Component {

    private static final int NUM_COMPONENTS_POSITION = 2;
    private static final int NUM_COMPONENTS_TEXTURE = 2;
    private static final int NUM_COMPONENTS_COLOR = 4;
    private static final int NUM_COMPONENTS_PER_VERTEX = NUM_COMPONENTS_POSITION + NUM_COMPONENTS_TEXTURE + NUM_COMPONENTS_COLOR;
    private static final int MAX_TRIANGLES = 1000;
    private static final int MAX_NUM_VERTICES = MAX_TRIANGLES * 3;

    public static final float THICKNESS = 12;

    public RopePath path;
    public Array<Vector2> points;
    private Mesh mesh;
    private float[] vertices;
    private int verticesIndex;

    public Color color;
    public Connection connection;
    public boolean flowing;


    public CableShaderRenderable(Connection connection, RopePath path) {
        this.connection = connection;
        points = path.positions;
        this.path = path;
        this.shaderProgram = ShaderType.CABLE.get();
        color = Color.WHITE;

        this.mesh = new Mesh(false, MAX_NUM_VERTICES, 0,
            new VertexAttribute(VertexAttributes.Usage.Position,           NUM_COMPONENTS_POSITION, "a_position"),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, NUM_COMPONENTS_TEXTURE,  "a_texCoord0"),
            new VertexAttribute(VertexAttributes.Usage.ColorUnpacked,      NUM_COMPONENTS_COLOR,    "a_color")
        );

        this.verticesIndex = 0;
        this.vertices = new float[MAX_NUM_VERTICES * NUM_COMPONENTS_PER_VERTEX];

    }

    public void render() {

        this.color = connection.getColor();
        var turrent = connection.getTurret();
        if (turrent != null) {
            flowing = turrent.hasPattern();
        }

        populateVertexArray();

        if (verticesIndex == 0) return;

        int vertexCount = verticesIndex / NUM_COMPONENTS_PER_VERTEX;
        mesh.setVertices(vertices);
        shaderProgram.setUniformf("u_flow", flowing ? 1f : 0f);
        mesh.render(shaderProgram, GL20.GL_TRIANGLE_STRIP, 0, vertexCount);

        verticesIndex = 0;

    }

    public void populateVertexArray() {
        verticesIndex = 0;
        int numVertices = points.size;
        for (int i = 0; i < numVertices -1 ; i++) {
            int i2 = i + 1;

            float interp1 = (float) i  / (float) numVertices;
            float interp2 = (float) i2 / (float) numVertices;

            interp1 *= path.getCurrentLength() * .001f;

            Vector2 p1 = points.get(i);
            Vector2 p2 = points.get(i2);

            var perp = FramePool.vec2();

            // normalize direction
            perp.set(p1).sub(p2).nor();

            // perpendicularized
            perp.set(-perp.y, perp.x);

            float taper = MathUtils.clamp(Math.min(5, (numVertices - i)) / 5f, 0f, 1f);

            float thick = (2f * THICKNESS * taper);

            float alpha = 1f;

            // extrude by thickness
            perp.scl(thick / 2f);

            // populate vertex array with values to create triangle strip...

            // vertex 1
            vertices[verticesIndex++] = p1.x + perp.x;
            vertices[verticesIndex++] = p1.y + perp.y;
            vertices[verticesIndex++] = interp1;
            vertices[verticesIndex++] = 1f;
            vertices[verticesIndex++] = color.r;
            vertices[verticesIndex++] = color.g;
            vertices[verticesIndex++] = color.b;
            vertices[verticesIndex++] = alpha;

            // vertex 2
            vertices[verticesIndex++] = p1.x - perp.x;
            vertices[verticesIndex++] = p1.y - perp.y;
            vertices[verticesIndex++] = interp1;
            vertices[verticesIndex++] = 0f;
            vertices[verticesIndex++] = color.r;
            vertices[verticesIndex++] = color.g;
            vertices[verticesIndex++] = color.b;
            vertices[verticesIndex++] = alpha;
        }

    }
}
