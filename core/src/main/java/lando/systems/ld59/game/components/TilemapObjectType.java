package lando.systems.ld59.game.components;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

public interface TilemapObjectType {

    String mapTypeName();

    class Registry {
        private static final Map<String, TilemapObjectType> MAP = new HashMap<>();
        static {
            for (var value : Enemies.values()) MAP.put(value.mapTypeName(), value);
            for (var value : Blocks.values())  MAP.put(value.mapTypeName(), value);
            for (var value : Pickups.values()) MAP.put(value.mapTypeName(), value);
            for (var value : Misc.values())    MAP.put(value.mapTypeName(), value);
        }
        public static TilemapObjectType get(String mapTypeName) {
            return MAP.get(mapTypeName);
        }
    }

    enum Enemies implements TilemapObjectType {
          ANGRY_SUN("sun")
        , BULLET_BILL("bullet")
        , CAPTAIN_LOU("lou")
        , GOOMBA_CYBORG("goomba")
        , HAMMER_BRO("hammer")
        , KOOPA("koopa")
        , LAKITU("lakitu")
        , MARIO("mario")
        , MISTY("misty")
        ;
        public final String mapTypeName;

        Enemies(String mapTypeName) {
            this.mapTypeName = mapTypeName;
        }

        public String mapTypeName() { return mapTypeName; }
    }

    enum Blocks implements TilemapObjectType {
          BREAK("block")
        , COIN("coin-block")
        , SPIKE("spike")
        , LAVA("lava")
        ;
        public final String mapTypeName;

        Blocks(String mapTypeName) {
            this.mapTypeName = mapTypeName;
        }
        public String mapTypeName() { return mapTypeName; }
    }

    enum Pickups implements TilemapObjectType {
          COIN("coin")
        , RELIC_PLUNGER("plunger")
        , RELIC_TORCH("torch")
        , RELIC_WRENCH("wrench")
        , SHROOM("shroom")
        ;
        public final String mapTypeName;

        Pickups(String mapTypeName) {
            this.mapTypeName = mapTypeName;
        }
        public String mapTypeName() { return mapTypeName; }
    }

    enum Misc implements TilemapObjectType {
        TRIGGER("trigger")
        ;
        public final String mapTypeName;
        Misc(String mapTypeName) {
            this.mapTypeName = mapTypeName;
        }
        public String mapTypeName() { return mapTypeName; }
    }
}
