package lando.systems.ld59.game.components.renderable;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld59.assets.AnimType;
import lando.systems.ld59.game.components.Position;
import lando.systems.ld59.utils.Util;

public class Animator extends Renderable implements Component {

    public AnimType type = null;
    public TextureRegion keyframe = null;
    public Animation<TextureRegion> animation = null;

    public float stateTime = 0;
    public int facing = 1;

    public Animator(Vector2 origin) {
        this.type = null;
        this.origin.set(origin);
    }

    public Animator(AnimType type) {
        this(type.get());
        this.type = type;
    }

    public Animator(AnimType type, Vector2 origin) {
        this(type);
        this.origin.set(origin);
        this.rotationOrigin.set(origin);
    }

    public Animator(AnimType type, Vector2 size, Vector2 origin) {
        this(type, origin);
        this.size.set(size);
    }

    public Animator(Animation<TextureRegion> animation) {
        this(animation.getKeyFrame(0));
        this.animation = animation;
    }

    public Animator(TextureRegion keyframe) {
        this.keyframe = keyframe;
        this.size.set(keyframe.getRegionWidth(), keyframe.getRegionHeight());
        this.origin.set(keyframe.getRegionWidth() / 2f, keyframe.getRegionHeight() / 2f);
        this.rotationOrigin.set(keyframe.getRegionWidth() / 2f, keyframe.getRegionHeight() / 2f);
    }

    @Override
    public void render(SpriteBatch batch, Position position) {
        if (keyframe == null) return;
        Util.draw(batch, keyframe, rect(position), tint, rotationOrigin.x, rotationOrigin.y, scale.x, scale.y, rotation );
    }

    public TextureRegion keyframe() {
        return keyframe;
    }

    public float start(AnimType type) {
        stateTime = 0;
        return play(type);
    }

    public float play(AnimType type) {
        this.type = type;
        return play(type.get());
    }

    public float play(Animation<TextureRegion> anim) {
        if (anim == null) return 0;
        this.animation = anim;
        return this.animation.getAnimationDuration();
    }

    public boolean isComplete() {
        var isNormal = animation.getPlayMode() == Animation.PlayMode.NORMAL;
        var isFinished = animation.isAnimationFinished(stateTime);
        return isNormal && isFinished;
    }

    public boolean hasAnimation() {
        return animation != null;
    }

    public boolean hasKeyframe() {
        return keyframe != null;
    }

    public boolean isIncomplete() {
        return !isComplete();
    }
}
