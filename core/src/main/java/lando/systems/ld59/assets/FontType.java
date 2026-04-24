package lando.systems.ld59.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.github.tommyettinger.textra.Font;

import java.util.EnumMap;
import java.util.HashSet;

import static com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public enum FontType implements AssetType<Font> {
      ATKINSON_HYPERLEGIBLE      ("atkinson-hyperlegible-regular.ttf")
    , CHEVYRAY_RISE              ("chevyray-rise.ttf")
    , COUSINE                    ("cousine-regular.ttf")
    , DROID_SANS_MONO            ("droid-sans-mono.ttf")
    , FEASFB                     ("feasfb-regular.ttf")
    , INCONSOLATA                ("inconsolata.otf")
    , NOTO_SANS                  ("noto-sans-cjk-jp-medium.otf")
    , ROBOTO                     ("roboto-regular.ttf")
    , ROBOTO_SMALL               ("roboto-regular.ttf", 16)
    , ROBOTO_LARGE               ("roboto-regular.ttf", 32)
    , ROUNDABOUT                 ("chevyray-roundabout.ttf")
    , ROUNDABOUT_LARGE           ("chevyray-roundabout.ttf", 32)
    , SOURCE_CODE_PRO            ("source-code-pro-regular.otf")
    , SOURCE_CODE_PRO_OUTLINED   ("source-code-pro-regular.otf", ParamBuilder.withSize(20).border(2, Color.DARK_GRAY).build())
    , HEMI_HEAD                  ("hemihead.otf", 32)
    , HEMI_HEAD_CREDITS          ("hemihead.otf", 24)
    , HEMI_HEAD_SMALL            ("hemihead.otf", 20)
    ;

    private static final EnumMap<FontType, Font> container = AssetType.createContainer(FontType.class);

    public static final int DEFAULT_SIZE = 24;

    public final FreeTypeFontParameter params;
    public final String fontFilePath;
    public final String labelStyleName;
    public final String textraLabelStyleName;

    FontType(String fontFilePath) {
        this(fontFilePath, DEFAULT_SIZE);
    }

    FontType(String fontFilePath, int size) {
        this(fontFilePath, ParamBuilder.withSize(size).build());
    }

    FontType(String fontFilePath, FreeTypeFontParameter params) {
        this.fontFilePath = "fonts/" + fontFilePath;
        this.params = params;
        this.labelStyleName = "label-" + name().toLowerCase();
        this.textraLabelStyleName = "textra-label-" + name().toLowerCase();
    }

    @Override
    public Font get() {
        return container.get(this);
    }

    /**
     * Queue each unique font file for async loading through AssetManager.
     * Uses real file paths as keys so TeaVM's file handle resolver can find them.
     */
    public static void loadEnum(Class<?> enumClass, Assets assets) {
        var mgr = assets.mgr;
        var resolver = mgr.getFileHandleResolver();
        mgr.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));

        var values = (FontType[]) enumClass.getEnumConstants();
        for (var type : values) {
            if (!mgr.isLoaded(type.fontFilePath, FreeTypeFontGenerator.class)) {
                mgr.load(type.fontFilePath, FreeTypeFontGenerator.class);
            }
        }
    }

    /**
     * Generate font variants from loaded generators, then release the generators.
     */
    public static void initEnum(Class<?> enumClass, Assets assets) {
        var mgr = assets.mgr;
        var values = (FontType[]) enumClass.getEnumConstants();

        for (var type : values) {
            var generator = mgr.get(type.fontFilePath, FreeTypeFontGenerator.class);
            var bmpFont = generator.generateFont(type.params);
            assets.disposables.add(bmpFont);

            var font = new Font(bmpFont);
            container.put(type, font);
            assets.disposables.add(font);
        }

        // Unload each generator exactly once
        var uniquePaths = new HashSet<String>();
        for (var type : values) {
            uniquePaths.add(type.fontFilePath);
        }
        for (var path : uniquePaths) {
            mgr.unload(path);
        }
    }

    /**
     * Builder for FreeTypeFontParameter (convenience wrapper)
     */
    private static class ParamBuilder {
        private final FreeTypeFontParameter params;

        private static ParamBuilder withSize(int size) {
            return new ParamBuilder(size);
        }

        private ParamBuilder(int size) {
            this.params = new FreeTypeFontParameter();
            params.size = size;
            params.color = Color.WHITE.cpy();
            params.borderWidth = 0;
            params.borderColor = Color.WHITE.cpy();
            params.shadowOffsetX = 0;
            params.shadowOffsetY = 0;
            params.shadowColor = new Color(0, 0, 0, 0.75f);
            params.genMipMaps = false;
            params.minFilter = Texture.TextureFilter.Linear;
            params.magFilter = Texture.TextureFilter.Linear;
        }

        public ParamBuilder color(Color color) {
            params.color = color;
            return this;
        }

        public ParamBuilder border(float width, Color color) {
            params.borderWidth = width;
            params.borderColor = color;
            return this;
        }

        public ParamBuilder shadow(int x, int y, Color color) {
            params.shadowOffsetX = x;
            params.shadowOffsetY = y;
            params.shadowColor = color;
            return this;
        }

        public ParamBuilder mipmaps(boolean enable) {
            params.genMipMaps = enable;
            return this;
        }

        public ParamBuilder filters(Texture.TextureFilter min, Texture.TextureFilter mag) {
            params.minFilter = min;
            params.magFilter = mag;
            return this;
        }

        public FreeTypeFontParameter build() {
            return params;
        }
    }
}
