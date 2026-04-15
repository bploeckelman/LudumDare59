package lando.systems.ld59.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

public interface AnimType extends AssetType<Animation<TextureRegion>> {

    AnimConfig getConfig();

    default String basePath() { return getConfig().basePath; }
    default String animName() { return getConfig().animName; }
    default Data data() { return getConfig().data; }

    static final Map<Class<?>, EnumMap<?, Animation<TextureRegion>>> CONTAINERS = new IdentityHashMap<>();

    @AllArgsConstructor
    class AnimConfig {
        public final String basePath;
        public final String animName;
        public final Data data;
    }

    class Data implements Serializable {
        private static final float DEFAULT_FRAME_DURATION = 0.1f;
        private static final Animation.PlayMode DEFAULT_PLAY_MODE = Animation.PlayMode.LOOP;

        public final float frameDuration;
        public final Animation.PlayMode playMode;

        public Data() { this(DEFAULT_FRAME_DURATION, DEFAULT_PLAY_MODE); }
        public Data(float frameDuration) { this(frameDuration, DEFAULT_PLAY_MODE); }
        public Data(float frameDuration, Animation.PlayMode playMode) {
            this.frameDuration = (frameDuration > 0) ? frameDuration : DEFAULT_FRAME_DURATION;
            this.playMode = (playMode != null) ? playMode : DEFAULT_PLAY_MODE;
        }
    }

    @SuppressWarnings("unchecked")
    default Animation<TextureRegion> get() {
        // Safe cast: CONTAINERS is only populated by enums implementing AnimType
        var container = (EnumMap<? extends Enum<?>, Animation<TextureRegion>>) CONTAINERS.get(getClass());
        return container.get(this);
    }

    static <E extends Enum<E>> EnumMap<E, Animation<TextureRegion>> createAndRegisterContainer(Class<E> enumClass) {
        var container = new EnumMap<E, Animation<TextureRegion>>(enumClass);
        CONTAINERS.put(enumClass, container);
        return container;
    }

    static <E extends Enum<E>> EnumMap<E, AnimConfig> createConfigs(
            E[] enumValues,
            String basePath,
            Function<E, String> animNameMapper,
            Function<E, Data> dataMapper
    ) {
        var configs = new EnumMap<E, AnimConfig>(enumValues[0].getDeclaringClass());
        for (var value : enumValues) {
            configs.put(value, new AnimConfig(basePath, animNameMapper.apply(value), dataMapper.apply(value)));
        }
        return configs;
    }

    @SuppressWarnings("unchecked")
    static void initEnum(Class<?> enumClass, Assets assets) {
        var atlas = assets.atlas;
        var enumValues = enumClass.getEnumConstants();

        Object rawContainer = CONTAINERS.get(enumClass);
        var container = (Map<Enum<?>, Animation<TextureRegion>>) rawContainer;

        for (var enumValue : enumValues) {
            var type = (AnimType) enumValue;
            var data = type.data();
            var regions = atlas.findRegions(type.basePath() + type.animName());
            var animation = new Animation<TextureRegion>(data.frameDuration, regions, data.playMode);
            container.put((Enum<?>) enumValue, animation);
        }
    }
}
