package lando.systems.ld59.assets.anims;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld59.assets.AnimType;

import java.util.EnumMap;
import java.util.List;

public enum AnimEnemy implements AnimType {
      BLACK_1, BLACK_2, BLACK_3, BLACK_4, BLACK_5
    , BLUE_1, BLUE_2, BLUE_3, BLUE_4, BLUE_5
    , GREEN_1, GREEN_2, GREEN_3, GREEN_4, GREEN_5
    , RED_1, RED_2, RED_3, RED_4, RED_5
    , BOSS
    ;

    private static final String BASE_PATH = "characters/enemies/";
    private static final EnumMap<AnimEnemy, Animation<TextureRegion>> container = AnimType.createAndRegisterContainer(AnimEnemy.class);
    private static final EnumMap<AnimEnemy, AnimType.AnimConfig> configs = AnimType.createConfigs(
            AnimEnemy.values(), BASE_PATH,
            e -> e.name().toLowerCase().replace("_", "-"),
            e -> new AnimType.Data()
    );
    public static final List<AnimEnemy> redShips = List.of(RED_1, RED_2, RED_3, RED_4, RED_5);
    public static final List<AnimEnemy> blueShips = List.of(BLUE_1, BLUE_2, BLUE_3, BLUE_4, BLUE_5);
    public static final List<AnimEnemy> greenShips = List.of(GREEN_1, GREEN_2, GREEN_3, GREEN_4, GREEN_5);
    public static final List<AnimEnemy> blackShips = List.of(BLACK_1, BLACK_2, BLACK_3, BLACK_4, BLACK_5);

    @Override
    public AnimType.AnimConfig getConfig() {
        return configs.get(this);
    }
}
