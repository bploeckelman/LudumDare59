package lando.systems.ld59.assets;

import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld59.utils.Util;

import java.util.EnumMap;

public enum ImageType implements AssetType<Texture> {
    //@formatter:off
      GDX("libgdx.png")
    , SHIELD("shield.png")
    , NOISE("noise.png")
    , BACKGROUND("background.png")
    , BACKGROUND_TITLE("background-title.png")
    ;
    //@formatter:on

    private static final String TAG = ImageType.class.getSimpleName();
    private static final EnumMap<ImageType, Texture> container = AssetType.createContainer(ImageType.class);

    private final String textureName;

    ImageType(String textureName) {
        this.textureName = "images/" + textureName;
    }

    @Override
    public Texture get() {
        return container.get(this);
    }

    public static void loadEnum(Class<?> enumClass, Assets assets) {
        var texParamsNormal = new TextureLoader.TextureParameter();
        var texParamsRepeat = new TextureLoader.TextureParameter();
        texParamsRepeat.wrapU = Texture.TextureWrap.Repeat;
        texParamsRepeat.wrapV = Texture.TextureWrap.Repeat;
        texParamsRepeat.minFilter = Texture.TextureFilter.Linear;
        texParamsRepeat.magFilter = Texture.TextureFilter.Linear;
        texParamsRepeat.genMipMaps = false;

        var mgr = assets.mgr;
        var values = (ImageType[]) enumClass.getEnumConstants();
        for (var type : values) {
//            var isBackground = type.textureName.contains("background");

//            var params = isBackground ? texParamsRepeat : texParamsNormal;
            mgr.load(type.textureName, Texture.class, texParamsNormal);

//            Util.log(TAG, Stringf.format("texture '%s' loaded for type '%s'", type.textureName, type.name()));
        }
    }

    public static void initEnum(Class<?> enumClass, Assets assets) {
        var mgr = assets.mgr;
        var values = (ImageType[]) enumClass.getEnumConstants();
        for (var type : values) {
            var texture = mgr.get(type.textureName, Texture.class);
            if (texture == null) {
                throw new GdxRuntimeException(Stringf.format("%s: texture '%s' not found for type '%s'", TAG, type.textureName, type.name()));
            }
//            if (type == NOISE) {
//                texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
//                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
//            }
            container.put(type, texture);
        }
    }
}
