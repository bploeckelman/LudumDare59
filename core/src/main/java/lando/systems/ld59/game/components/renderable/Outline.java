package lando.systems.ld59.game.components.renderable;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

public class Outline implements Component {

    public static final Outline CLEAR = new Outline();

    private static final float DEFAULT_THICKNESS = 0.5f;

    private Color fillColor;
    private Color outlineColor;
    private float outlineThickness;

    public Outline(){
        this(Color.CLEAR, Color.CLEAR_WHITE, DEFAULT_THICKNESS);
    }

    public Outline(Color outlineColor) {
        this(outlineColor, DEFAULT_THICKNESS);
    }

    public Outline(Color outlineColor, float outlineThickness) {
        this(outlineColor, Color.CLEAR_WHITE, outlineThickness);
    }

    public Outline(Color outline, Color fill, float outlineThickness) {
        this.outlineColor = new Color(outline);
        this.fillColor = new Color(fill);
        this.outlineThickness = outlineThickness;
    }

    public Color fillColor() { return fillColor; }
    public Color outlineColor() { return outlineColor; }
    public float outlineThickness() { return outlineThickness; }

    public void fillColor(Color fillColor) { this.fillColor.set(fillColor); }
    public void outlineColor(Color outlineColor) { this.outlineColor.set(outlineColor); }
    public void outlineThickness(float outlineThickness) { this.outlineThickness = outlineThickness; }
}
