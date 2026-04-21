package lando.systems.ld59.game.components.renderable;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld59.assets.ImageType;
import lando.systems.ld59.assets.ShaderType;

public class ShieldShaderRenderable extends ShaderRenderable implements Component {

    public static final int MAX_IMPACTS = 8;

    public Texture noiseTexture;

    public float accum;
    public Array<Vector3> activeImpacts = new Array<>();
    public final float[] impacts = new float[MAX_IMPACTS*3];
    public int numActiveImpacts = 0;


    public ShieldShaderRenderable() {
        this.shaderProgram = ShaderType.SHIELD.get();
        this.texture = ImageType.SHIELD.get();
        this.bounds.set(-725, -1080, 1460, 1460);
        this.noiseTexture = ImageType.NOISE.get();
    }

    public void update(float delta) {
        accum += delta;
        for (int i = activeImpacts.size - 1; i >= 0; i--) {
            var impact = activeImpacts.get(i);
            impact.z += delta;
            if (impact.z > 2) {
                activeImpacts.removeIndex(i);
            }
        }

        // flatten to float array for shader
        numActiveImpacts = Math.min(activeImpacts.size, MAX_IMPACTS);
        for (int i = 0; i < numActiveImpacts; i++) {
            Vector3 imp = activeImpacts.get(i);
            int idx = i * 3;
            impacts[idx] = imp.x; // u
            impacts[idx + 1] = imp.y; // v
            impacts[idx + 2] = imp.z; // age
        }
        // zero out the rest so old data doesn't linger
        for (int i = numActiveImpacts * 3; i < impacts.length; i++) {
            impacts[i] = 0f;
        }
    }


    public void addImpact(Vector3 impact) {
        activeImpacts.add(impact);
        if (activeImpacts.size > MAX_IMPACTS) {
            activeImpacts.removeIndex(0);
        }
    }

}
