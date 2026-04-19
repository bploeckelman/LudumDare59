package lando.systems.ld59.game.components.renderable;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

public class Outline implements Component {

    public static final Outline CLEAR = new Outline();

    private Color fillColor;
    private Color outlineColor;
    private float outlineThickness;

    public Outline(){
        this(Color.CLEAR, Color.CLEAR, .5f);
    }

    public Outline(Color outlineColor, Color fillColor, float outlineThickness) {
        this.outlineColor = new Color(outlineColor);
        this.fillColor = new Color(fillColor);
        this.outlineThickness = outlineThickness;
    }

    public Color fillColor() { return fillColor; }
    public Color outlineColor() { return outlineColor; }
    public float outlineThickness() { return outlineThickness; }

    public void fillColor(Color fillColor) { this.fillColor = fillColor; }
    public void outlineColor(Color outlineColor) { this.outlineColor = outlineColor; }
    public void outlineThickness(float outlineThickness) { this.outlineThickness = outlineThickness; }
}
