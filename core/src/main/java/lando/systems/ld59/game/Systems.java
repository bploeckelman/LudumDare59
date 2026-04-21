package lando.systems.ld59.game;

import com.badlogic.ashley.core.Engine;
import lando.systems.ld59.game.systems.*;
import lando.systems.ld59.screens.BaseScreen;

public class Systems {

    public static AnimationSystem        animation;
    public static AudioSystem            audio;
    public static BackgroundAnimSpawnerSystem backgroundAnimSpawner;
    public static BaseButtonSystem       baseButtons;
    public static CityBaseSystem         cityBase;
    public static CollisionCheckSystem   collisionCheck;
    public static CollisionHandlerSystem collisionHandler;
    public static ConnectionSystem       connections;
    public static CooldownSystem         cooldown;
    public static InterpSystem           interp;
    public static MovementSystem         movement;
    public static ParticleSystem         particles;
    public static ProjectileSystem       projectiles;
    public static RenderDebugSystem      renderDebug;
    public static RenderSystem           render;
    public static ShieldSystem           shield;
    public static ViewSystem             view;
    public static TimerSystem            timer;
    public static TurretSystem           turret;
    public static EnemySystem            enemy;
    public static EnemySpawnerSystem     enemySpawner;
    public static WaveScheduleSystem     waveSchedule;

    public static void init(Engine engine) {
        Systems.animation        = new AnimationSystem();
        Systems.audio            = new AudioSystem();
        Systems.backgroundAnimSpawner = new BackgroundAnimSpawnerSystem();
        Systems.baseButtons      = new BaseButtonSystem();
        Systems.cityBase         = new CityBaseSystem();
        Systems.collisionCheck   = new CollisionCheckSystem();
        Systems.collisionHandler = new CollisionHandlerSystem();
        Systems.connections      = new ConnectionSystem();
        Systems.cooldown         = new CooldownSystem();
        Systems.interp           = new InterpSystem();
        Systems.movement         = new MovementSystem();
        Systems.particles        = new ParticleSystem();
        Systems.projectiles      = new ProjectileSystem();
        Systems.renderDebug      = new RenderDebugSystem();
        Systems.render           = new RenderSystem();
        Systems.shield           = new ShieldSystem();
        Systems.view             = new ViewSystem();
        Systems.timer            = new TimerSystem();
        Systems.turret           = new TurretSystem();
        Systems.enemy            = new EnemySystem();
        Systems.enemySpawner     = new EnemySpawnerSystem();
        Systems.waveSchedule     = new WaveScheduleSystem();

        engine.addSystem(animation);
        engine.addSystem(audio);
        engine.addSystem(backgroundAnimSpawner);
        engine.addSystem(baseButtons);
        engine.addSystem(cityBase);
        engine.addSystem(collisionCheck);
        engine.addSystem(collisionHandler);
        engine.addSystem(connections);
        engine.addSystem(cooldown);
        engine.addSystem(interp);
        engine.addSystem(movement);
        engine.addSystem(particles);
        engine.addSystem(projectiles);
        engine.addSystem(renderDebug);
        engine.addSystem(render);
        engine.addSystem(shield);
        engine.addSystem(view);
        engine.addSystem(timer);
        engine.addSystem(turret);
        engine.addSystem(enemy);
        engine.addSystem(enemySpawner);
        engine.addSystem(waveSchedule);
    }
}
