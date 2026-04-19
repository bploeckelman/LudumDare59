package lando.systems.ld59.game.components.renderable;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import lando.systems.ld59.assets.ImageType;
import lando.systems.ld59.assets.ShaderType;

public class ShieldShaderRenderable extends ShaderRenderable implements Component {

    public Texture noiseTexture;

    public ShieldShaderRenderable() {
        this.shaderProgram = ShaderType.SHIELD.get();
        this.texture = ImageType.SHIELD.get();
        this.bounds.set(-725, -1080, 1460, 1460);
        this.noiseTexture = ImageType.NOISE.get();
    }

}
