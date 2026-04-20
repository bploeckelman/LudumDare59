package lando.systems.ld59.game.components;

import com.badlogic.ashley.core.Component;

public class Gem implements Component {

    float angle;

    public Gem(float angle) {
        this.angle = angle;
    }
}
