package lando.systems.ld59.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld59.AnimDepths;import lando.systems.ld59.Main;
import lando.systems.ld59.assets.ImageType;
import lando.systems.ld59.assets.ShaderType;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.components.Health;
import lando.systems.ld59.game.components.Position;
import lando.systems.ld59.game.components.renderable.*;
import lando.systems.ld59.utils.FramePool;
import lando.systems.ld59.utils.Util;

import java.util.Comparator;

public class RenderSystem extends SortedIteratingSystem {

    private static final Family RENDERABLES = Family
        .one(Image.class, Animator.class, FlatShape.class).get();

    private static final Comparator<Entity> comparator = (e1, e2) -> {
        var r1 = Renderable.getRenderable(e1);
        var r2 = Renderable.getRenderable(e2);
        float e1Depth = r1 == null ? 0 : r1.depth;
        float e2Depth = r2 == null ? 0 : r2.depth;
        return (int)(e1Depth - e2Depth);
    };

    private float accum = 0;
    private Array<Entity> beforeCablesEntities = new Array<>();
    private Array<Entity> afterCablesEntities = new Array<>();

    public RenderSystem() {
        super(RENDERABLES, comparator);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        accum += deltaTime;
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
    }

    public void draw(SpriteBatch batch, OrthographicCamera camera) {
        beforeCablesEntities.clear();
        afterCablesEntities.clear();

        for (var entity : getEntities()) {
            var r = Renderable.getRenderable(entity);
            if (r.depth < AnimDepths.CABLES) {
                beforeCablesEntities.add(entity);
            } else {
                afterCablesEntities.add(entity);
            }
        }

        for (var entity : beforeCablesEntities) {
            var pos = Components.optional(entity, Position.class).orElse(Position.ZERO);

            // Draw simple renderables, apply outline component if exists, otherwise normal rendering
            Components.optional(entity, Outline.class).ifPresentOrElse(
                    outline -> renderWithOutline(batch, entity, outline),
                    () -> {
                        Components.optional(entity, Image.class).ifPresent(img -> img.render(batch, pos));
                        Components.optional(entity, Animator.class).ifPresent(anim -> anim.render(batch, pos));
                        Components.optional(entity, FlatShape.class).ifPresent(shape -> shape.render(batch, pos));
                    }
            );
        }

        batch.end();
        drawCableShader(camera);
        batch.begin();

        for (var entity : afterCablesEntities) {
            var pos = Components.optional(entity, Position.class).orElse(Position.ZERO);

            // Draw simple renderables, apply outline component if exists, otherwise normal rendering
            Components.optional(entity, Outline.class).ifPresentOrElse(
                outline -> renderWithOutline(batch, entity, outline),
                () -> {
                    Components.optional(entity, Image.class).ifPresent(img -> img.render(batch, pos));
                    Components.optional(entity, Animator.class).ifPresent(anim -> anim.render(batch, pos));
                    Components.optional(entity, FlatShape.class).ifPresent(shape -> shape.render(batch, pos));
                }
            );
        }

        drawShieldShader(batch);


    }

    public void drawCableShader(OrthographicCamera camera) {
        var engine = Main.game.engine;
        var cables = engine.getEntitiesFor(Family.one(CableShaderRenderable.class).get());
        var shader = ShaderType.CABLE.get();

        shader.bind();

        shader.setUniformMatrix("u_projTrans", camera.combined);
        shader.setUniformf("u_time", accum);
        shader.setUniformi("u_texture", 0);
        shader.setUniformf("u_edgeColor", Color.BLACK);
        ImageType.NOISE.get().bind(0);
        for (Entity cable : cables) {
            var renderable = Components.get(cable, CableShaderRenderable.class);
            renderable.render();
        }

        // End

    }

    public void drawShieldShader(SpriteBatch batch) {
        var engine = Main.game.engine;
        batch.setColor(Color.WHITE);
        var shields = engine.getEntitiesFor(Family.one(ShieldShaderRenderable.class).get());
        batch.flush();
        for (Entity shield : shields) {
            var pos = Components.optional(shield, Position.class).orElse(Position.ZERO);
            var shieldShader = Components.get(shield, ShieldShaderRenderable.class);
            var health = Components.get(shield, Health.class);
            if (health.isDead()) continue;
            var shader = shieldShader.shaderProgram;
            if (!shader.isCompiled()) {
                Gdx.app.error("ShieldShader", "Compile failed: " + shader.getLog());
                continue; // skip drawing this one instead of crashing
            }
            var rect = shieldShader.rect(pos);
            batch.setShader(shader);

            shader.setUniformf("u_time", accum);
            shader.setUniformf("u_health", health.currentHealth / health.maxHealth);
            shieldShader.noiseTexture.bind(1);
            shader.setUniformi("u_noise", 1);
            shieldShader.texture.bind(0);

            batch.draw(shieldShader.texture, rect.x, rect.y, rect.width, rect.height);
            batch.setShader(null);
        }
    }

    /**
     * For drawing stuff in 'window' space rather than 'world' space, typically for shader effects
     */
    public void drawInWindowSpace(SpriteBatch batch, OrthographicCamera camera) {}

    /**
     * For drawing renderables that have an
     */
    private void renderWithOutline(SpriteBatch batch, Entity entity, Outline outline) {
        var pos      = Components.optional(entity, Position.class).orElse(Position.ZERO);
        var image    = Components.get(entity, Image.class);
        var animator = Components.get(entity, Animator.class);
        // TODO: also include FlatShape renderable?

        var tintColor = FramePool.color().set(Color.WHITE);

        // Get the renderable content for this entity, if any, to draw an outline around
        var region = (TextureRegion) null;
        var texture = (Texture) null;
        var rect = (Rectangle) null;
        var rotationOrigin = (Vector2) null;
        var scale = (Vector2) null;
        var rotation = 0f;

        // Extract render attribs from Image component
        if (image != null) {
            tintColor = image.tint;
            region = image.getTextureRegion();
            texture = image.getTexture();
            rect = image.rect(pos);
            rotationOrigin = image.rotationOrigin;
            scale = image.scale;
            rotation = image.rotation;
        }

        // Extract render attribs from Animator component
        if (animator != null) {
            region = animator.keyframe();
            rect = animator.rect(pos);
            tintColor = animator.tint;
            rotationOrigin = animator.rotationOrigin;
            scale = animator.scale;
            rotation = animator.rotation;
        }

        // No renderable content to outline, bail out
        if (image == null && animator == null) {
            return;
        }

        var prevColor = FramePool.color().set(batch.getColor());
        var shader = ShaderType.OUTLINE.get();

        batch.setColor(tintColor);
        batch.setShader(shader);
        {
            shader.setUniformf("u_fill_color", outline.fillColor());
            shader.setUniformf("u_outline_color", outline.outlineColor());

            if (texture != null) {
                shader.setUniformf("u_thickness",
                        outline.outlineThickness() / (float) texture.getWidth(),
                        outline.outlineThickness() / (float) texture.getHeight());
                Util.draw(batch, texture, rect, tintColor, rotationOrigin.x, rotationOrigin.y, scale.x, scale.y, rotation );
            }

            if (region != null) {
                shader.setUniformf("u_thickness",
                        outline.outlineThickness() / (float) region.getTexture().getWidth(),
                        outline.outlineThickness() / (float) region.getTexture().getHeight());
                Util.draw(batch, region, rect, tintColor, rotationOrigin.x, rotationOrigin.y, scale.x, scale.y, rotation );
            }
        }
        batch.setShader(null);
        batch.setColor(prevColor);
    }
}
