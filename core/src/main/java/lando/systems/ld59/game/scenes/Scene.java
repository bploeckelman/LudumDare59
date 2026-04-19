package lando.systems.ld59.game.scenes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import lando.systems.ld59.game.Components;
import lando.systems.ld59.game.Factory;
import lando.systems.ld59.game.components.*;
import lando.systems.ld59.game.signals.EntityEvent;
import lando.systems.ld59.game.signals.SignalEvent;
import lando.systems.ld59.screens.BaseScreen;
import lando.systems.ld59.utils.Util;

import static lando.systems.ld59.game.Constants.*;

public abstract class Scene<ScreenType extends BaseScreen> implements Listener<SignalEvent> {

    public final ScreenType screen;

    public Entity player;
    public Entity map;
    public Entity view;

    public Scene(ScreenType screen) {
        this.screen = screen;
        SignalEvent.addListener(this);
    }

    public ScreenType screen() { return screen; }
    public Engine engine()     { return screen.engine; }
    public Entity player()     { return player; }
    public Entity map()        { return map; }
    public Entity view()       { return view; }

    @Override
    public void receive(Signal<SignalEvent> signal, SignalEvent event) {
        if (event instanceof EntityEvent.Remove) {
            var remove = (EntityEvent.Remove) event;
            engine().removeEntity(remove.entity);
        }
    }

    protected void createView(int viewportWidth, int viewportHeight) {
        // configure the camera to emulate a low res display
        // TODO: continue playing with some options here,
        //  probably best to stick with integer multiples of window size (1280x720)
//        var width  = 360; // window size / 4  ;  // old: 240;
//        var height = 180; // window size / 4  ;  // old: 160;
        var camera = screen.worldCamera;
        camera.setToOrtho(false, viewportWidth, viewportHeight);
        camera.update();

        // Set up the map view
        view = Factory.view(screen.worldCamera);
        screen.engine.addEntity(view);
    }
}
