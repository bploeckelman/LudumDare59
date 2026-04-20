package lando.systems.ld59.game;

public class Stats {

    private static Stats instance;

    public int enemiesKilled;
    public float timeElapsed;
    public int damageDealt;
    public int damageTaken;
    public int goodKills;
    public int badKills;
    public int neutralKills;

    private Stats() {
        reset();
    }

    public static Stats instance() {
        if (instance == null) {
            instance = new Stats();
        }
        return instance;
    }

    public void reset() {
        enemiesKilled = 0;
        timeElapsed = 0f;
        damageDealt = 0;
        damageTaken = 0;
        goodKills = 0;
        neutralKills = 0;
        badKills = 0;
    }

    public void update(float dt) {
        timeElapsed += dt;
    }
}
