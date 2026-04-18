package lando.systems.ld59.assets;

import com.badlogic.gdx.utils.Null;
import lando.systems.ld59.assets.anims.AnimBase;
import lando.systems.ld59.assets.anims.AnimHero;
import lando.systems.ld59.assets.anims.AnimPet;
import lando.systems.ld59.assets.anims.AnimTemp;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public class AssetTypeRegistry {

    private final Map<Class<? extends AssetType<?>>, BiConsumer<Class<?>, Assets>> initializers = new LinkedHashMap<>();
    private final Map<Class<? extends AssetType<?>>, BiConsumer<Class<?>, Assets>> loaders = new LinkedHashMap<>();

    public AssetTypeRegistry() {
        register(AnimHero.class,    AnimType::initEnum);
        register(AnimBase.class, AnimType::initEnum);
        register(AnimTemp.class,    AnimType::initEnum);
        register(AnimPet.class,     AnimType::initEnum);
        register(ColorType.class,   ColorType::initEnum);
        register(EffectType.class,  EffectType::initEnum);
        register(EmitterType.class, EmitterType::initEnum);
        register(FontType.class,    FontType::initEnum, FontType::loadEnum);
        register(IconType.class,    IconType::initEnum);
        register(ImageType.class,   ImageType::initEnum, ImageType::loadEnum);
        register(MusicType.class,   MusicType::initEnum, MusicType::loadEnum);
        register(ShaderType.class,  ShaderType::initEnum, ShaderType::loadEnum);
        register(SkinType.class,    SkinType::initEnum, SkinType::loadEnum);
        register(SoundType.class,   SoundType::initEnum, SoundType::loadEnum);
    }

    public <E extends Enum<E> & AssetType<?>> void register(
            Class<E> enumClass,
            BiConsumer<Class<?>, Assets> initializer
    ) {
        register(enumClass, initializer, null);
    }

    public <E extends Enum<E> & AssetType<?>> void register(
            Class<E> enumClass,
            BiConsumer<Class<?>, Assets> initializer,
            @Null BiConsumer<Class<?>, Assets> loader
    ) {
        Objects.requireNonNull(initializer);
        initializers.put(enumClass, initializer);
        if (loader != null) {
            loaders.put(enumClass, loader);
        }
    }

    public void initAll(Assets assets) {
        for (var entry : initializers.entrySet()) {
            entry.getValue().accept(entry.getKey(), assets);
        }
    }

    public void loadAll(Assets assets) {
        for (var entry : loaders.entrySet()) {
            entry.getValue().accept(entry.getKey(), assets);
        }
    }
}
