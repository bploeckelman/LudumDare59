package lando.systems.ld59.game.systems;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import lando.systems.ld59.Main;
import lando.systems.ld59.assets.MusicType;
import lando.systems.ld59.game.signals.AudioEvent;
import lando.systems.ld59.game.signals.SignalEvent;

public class AudioSystem extends EntitySystem implements Listener<SignalEvent> {

    public static MutableFloat soundVolume;
    public static MutableFloat musicVolume;
    public static final float DEFAULT_VOLUME = 0.5f;
    public static Music currentMusic = null;

    private static Preferences prefs = Main.game.assets.prefs;


    public AudioSystem() {
        musicVolume = new MutableFloat(prefs.getFloat("musicVolume", DEFAULT_VOLUME));
        soundVolume = new MutableFloat(prefs.getFloat("soundVolume", DEFAULT_VOLUME));
        prefs.flush();
        SignalEvent.addListener(this);
    }

    public static void setSoundVolume(float soundVolume) {
        prefs.putFloat("soundVolume", soundVolume);
        prefs.flush();
        AudioSystem.soundVolume.setValue(soundVolume);
    }
    public static void setMusicVolume(float musicVolume) {
        prefs.putFloat("musicVolume", musicVolume);
        prefs.flush();
        if (AudioSystem.currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
        AudioSystem.musicVolume.setValue(musicVolume);
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
            this.currentMusic = music;
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
            this.currentMusic = null;
        }
    }
}
