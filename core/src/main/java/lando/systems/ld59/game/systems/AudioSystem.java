package lando.systems.ld59.game.systems;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import lando.systems.ld59.assets.MusicType;
import lando.systems.ld59.game.signals.AudioEvent;
import lando.systems.ld59.game.signals.SignalEvent;

public class AudioSystem extends EntitySystem implements Listener<SignalEvent> {

    public static MutableFloat soundVolume = new MutableFloat(.5f);
    public static MutableFloat musicVolume = new MutableFloat(.5f);

    public AudioSystem() {
        SignalEvent.addListener(this);
    }

    @Override
    public void receive(Signal<SignalEvent> signal, SignalEvent event) {
        if (event instanceof AudioEvent.PlaySound) {
            var play = (AudioEvent.PlaySound) event;
            play.soundType.get().play(play.volume * soundVolume.floatValue());
        }
        else if (event instanceof AudioEvent.PlayMusic) {
            var play = (AudioEvent.PlayMusic) event;
            var music = play.musicType.get();
            music.setVolume(play.volume * musicVolume.floatValue());
            music.setLooping(true);
            music.play();
        }
        else if (event instanceof AudioEvent.StopMusic) {
            var stop = (AudioEvent.StopMusic) event;
            if (stop.musicType != null) {
                // Stop the specified music
                var music = stop.musicType.get();
                music.stop();
            } else {
                // Stop all musics
                for (var type : MusicType.values()) {
                    type.get().stop();
                }
            }
        }
    }
}
