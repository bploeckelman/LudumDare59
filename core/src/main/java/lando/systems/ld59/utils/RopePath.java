package lando.systems.ld59.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld59.Config;
import lando.systems.ld59.Main;
import lando.systems.ld59.game.components.SceneContainer;

public class RopePath {

    private static final Vector2 DEFAULT_GRAVITY = new Vector2(0, -100f);
    private static final float DEFAULT_DAMPING = 0.99f; // 1 = no damping
    private static final int CONSTRAINT_ITERATIONS = 4;
    private static final float CONSTRAINT_STIFFNESS = 0.3f;


    private static final float FIXED_TIMESTEP = 1f / 60f;
    private static final int MAX_STEPS_PER_FRAME = 5; // prevent spiral of death

    private float accumulator = 0f;

    public final Array<Vector2> positions = new Array<>();
    public final Vector2 gravity = new Vector2(DEFAULT_GRAVITY);
    public float damping = DEFAULT_DAMPING;

    private final Array<Vector2> oldPositions = new Array<>();
    private final Array<Float> restDistances = new Array<>();
    private boolean[] pinned;

    private Array<Circle> colliders = new Array<>();

    public void addCircleCollider(float x, float y, float radius) {
        colliders.add(new Circle(x, y, radius));
    }

    public void clearColliders() {
        colliders.clear();
    }

    public RopePath(Array<Vector2> initialPositions) {
        addCircleCollider(Config.window_width/2, 75, 100);
        for (int i = 0; i < initialPositions.size; i++) {
            var pos = initialPositions.get(i);
            positions.add(new Vector2(pos));
            oldPositions.add(new Vector2(pos));
        }

        for (int i = 0; i < initialPositions.size - 1; i++) {
            float dist = initialPositions.get(i).dst(initialPositions.get(i + 1));
            restDistances.add(dist);
        }

        pinned = new boolean[initialPositions.size];
        pinPoint(0);
        pinPoint(initialPositions.size - 1);
    }

    public void jostle() {
        float defaultStrength = 10f;
        jostle(defaultStrength);
    }

    public void jostle(float strength) {
        if (positions.size < 3) return;

        // Get the rope's main axis for perpendicular displacement
        var start = positions.get(0);
        var end = positions.get(positions.size - 1);
        float ax = end.x - start.x;
        float ay = end.y - start.y;
        float len = (float) Math.sqrt(ax * ax + ay * ay);
        if (len < 0.001f) return;

        // Perpendicular direction
        float px = -ay / len;
        float py =  ax / len;

        for (int i = 1; i < positions.size - 1; i++) {
            if (pinned[i]) continue;
            // Parabolic falloff: max in middle, zero at endpoints
            float t = (float) i / (positions.size - 1);
            float falloff = 4f * t * (1f - t);

            float offset = MathUtils.random(-strength, strength) * falloff;
            positions.get(i).add(px * offset, py * offset);
        }
    }

    public void pinPoint(int index) {
        if (index >= 0 && index < pinned.length) {
            pinned[index] = true;
        }
    }

    public void unpinPoint(int index) {
        if (index >= 0 && index < pinned.length) {
            pinned[index] = false;
        }
    }

    public void update(float deltaTime) {
        // Clamp delta to avoid huge jumps if the game freezes
        deltaTime = Math.min(deltaTime, 0.25f);
        accumulator += deltaTime;

        int steps = 0;
        while (accumulator >= FIXED_TIMESTEP && steps < MAX_STEPS_PER_FRAME) {
            step(FIXED_TIMESTEP);
            accumulator -= FIXED_TIMESTEP;
            steps++;
        }
    }

    private void step(float dt) {
        // Verlet integration — now dt is always 1/60
        for (int i = 0; i < positions.size; i++) {
            if (pinned[i]) continue;

            var pos = positions.get(i);
            var old = oldPositions.get(i);

            float vx = (pos.x - old.x) * damping;
            float vy = (pos.y - old.y) * damping;

            old.set(pos);

            pos.x += vx + gravity.x * dt * dt;
            pos.y += vy + gravity.y * dt * dt;
        }



        // Satisfy distance constraints — also runs at fixed rate now
        for (int iter = 0; iter < CONSTRAINT_ITERATIONS; iter++) {
            for (int i = 0; i < positions.size - 1; i++) {
                var p1 = positions.get(i);
                var p2 = positions.get(i + 1);

                float dx = p2.x - p1.x;
                float dy = p2.y - p1.y;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                float restDist = restDistances.get(i);

                if (dist < 0.0001f) continue;

                float diff = (dist - restDist) / dist;
                float offsetX = dx * 0.5f * diff * CONSTRAINT_STIFFNESS;
                float offsetY = dy * 0.5f * diff * CONSTRAINT_STIFFNESS;

                if (!pinned[i]) {
                    p1.x += offsetX;
                    p1.y += offsetY;
                }
                if (!pinned[i + 1]) {
                    p2.x -= offsetX;
                    p2.y -= offsetY;
                }
            }
        }
        for (int pass = 0; pass < 3; pass++) {
            for (int i = 0; i < positions.size; i++) {
                if (pinned[i]) continue;

                var pos = positions.get(i);
                var old = oldPositions.get(i);

                for (int c = 0; c < colliders.size; c++) {
                    Circle circle = colliders.get(c);
                    float dx = pos.x - circle.x;
                    float dy = pos.y - circle.y;
                    float distSq = dx * dx + dy * dy;
                    float minDist = circle.radius + 0.5f; // small epsilon prevents sticking

                    if (distSq < minDist * minDist && distSq > 0.0001f) {
                        float dist = (float) Math.sqrt(distSq);
                        float penetration = minDist - dist;

                        // Push out along normal
                        float nx = dx / dist;
                        float ny = dy / dist;
                        pos.x += nx * penetration;
                        pos.y += ny * penetration;

                        // Adjust old position too so Verlet doesn't snap it back in
                        old.x += nx * penetration;
                        old.y += ny * penetration;
                    }
                    // Handle exact center case
                    else if (distSq < 0.0001f) {
                        pos.x += minDist;
                        old.x += minDist;
                    }
                }
            }
        }
    }

    public void pinEnds() {
        pinPoint(0);
        pinPoint(positions.size - 1);
        pinPoint(positions.size - 2);
    }

    public void setPointPosition(int index, float x, float y) {
        if (index < 0 || index >= positions.size) return;

        var pos = positions.get(index);
        var old = oldPositions.get(index);

        // Move both current and old so there's no velocity spike
        pos.set(x, y);
        old.set(x, y);
    }

    /**
     * Get a reference to a point if you want to read it.
     * Don't modify this directly unless you call setPointPosition after.
     */
    public Vector2 getPoint(int index) {
        if (index < 0 || index >= positions.size) return null;
        return positions.get(index);
    }

    /**
     * Returns the current length of the rope by summing distances between adjacent points.
     * This changes as the rope stretches and simulates.
     */
    public float getCurrentLength() {
        if (positions.size < 2) return 0f;

        float length = 0f;
        for (int i = 0; i < positions.size - 1; i++) {
            length += positions.get(i).dst(positions.get(i + 1));
        }
        return length;
    }




    // original --------------------------------------------------------------------------------------------------------

//    private static final Vector2 DEFAULT_GRAVITY = new Vector2(0, -10f);
//    private static final float DEFAULT_STIFFNESS = 5f; // stiffness: higher = faster settling, strong resistance to displacement
//    private static final float DEFAULT_DAMPING = 0.1f; // damping: 0-1, higher = less jiggling, faster settling
//    private static final float DEFAULT_MASS = 1f; // mass: per point, lower mass = faster response to forces
//
//    public final Array<Vector2> positions = new Array<>();
//
//    public final Vector2 gravity = DEFAULT_GRAVITY;
//    public float stiffness = DEFAULT_STIFFNESS;
//    public float damping = DEFAULT_DAMPING;
//    public float mass = DEFAULT_MASS;
//
//    private final Array<Float> restDistances = new Array<>();
//    private final Array<Vector2> velocities = new Array<>();
//
//    private boolean[] pinned;
//
//    public RopePath(Array<Vector2> initialPositions) {
//        for (int i = 0; i < initialPositions.size - 1; i++) {
//            var pos = initialPositions.get(i);
//            var nextPos = initialPositions.get(i + 1);
//
//            positions.add(new Vector2(pos));
//            velocities.add(new Vector2());
//
//            // Store 'at rest' distances between adjacent pairs
//            float distance = pos.dst(nextPos);
//            restDistances.add(distance);
//        }
//
//        // Make sure the last point is added too (can't add above because that loop gets pairs of points, current + next)
//        var lastIdx = initialPositions.size - 1;
//        var last = initialPositions.get(lastIdx);
//        positions.add(new Vector2(last));
//        velocities.add(new Vector2());
//
//        // Pin the start and end points bey default
//        this.pinned = new boolean[initialPositions.size];
//        pinPoint(0);
//        pinPoint(lastIdx);
//    }
//
//    public void jostle() {
//        var range = 10f;
//        var defaultImpulseX = MathUtils.random(-range, range);
//        var defaultImpulseY = MathUtils.random(-range, range);
//        jostle(defaultImpulseX, defaultImpulseY);
//    }
//
//    public void jostle(float impulseX, float impulseY) {
//        var pointIndex = MathUtils.random(0, positions.size - 1);
//        jostle(pointIndex, impulseX, impulseY);
//    }
//
//    public void jostle(int pointIndex, float impulseX, float impulseY) {
//        if (pointIndex >= 0 && pointIndex < positions.size && !pinned[pointIndex]) {
//            velocities.get(pointIndex).add(impulseX, impulseY);
//        }
//    }
//
//    public void pinPoint(int index) {
//        if (index >= 0 && index < pinned.length) {
//            pinned[index] = true;
//        }
//    }
//
//    public void unpinPoint(int index) {
//        if (index >= 0 && index < pinned.length) {
//            pinned[index] = false;
//        }
//    }
//
//    public void update(float deltaTime) {
//        Array<Vector2> forces = new Array<>();
//        for (int i = 0; i < positions.size; i++) {
//            forces.add(new Vector2(gravity).scl(mass));
//        }
//
//        // Spring forces between adjacent points
//        for (int i = 0; i < positions.size - 1; i++) {
//            var p1 = positions.get(i);
//            var p2 = positions.get(i + 1);
//
//            var delta = FramePool.vec2(p2).sub(p1);
//            float currentDist = delta.len();
//            float restDist = restDistances.get(i);
//
//            if (currentDist > 0.001f) {
//                delta.nor();
//
//                // Hooke's law: F = -k(x - x0)
//                float displacement = currentDist - restDist;
//                float forceMagnitude = -stiffness * displacement;
//
//                // Pull p1 and p2 closer together based on distance
//                var springForce = FramePool.vec2(delta).scl(forceMagnitude);
//                forces.get(i).sub(springForce);
//                forces.get(i + 1).add(springForce);
//            }
//        }
//
//        // Integrate physics
//        for (int i = 0; i < positions.size; i++) {
//            if (pinned[i]) continue;
//
//            var v = velocities.get(i);
//            var p = positions.get(i);
//            var f = forces.get(i);
//
//            v.scl(1f - damping * deltaTime);
//            v.add(f.x * deltaTime / mass, f.y * deltaTime / mass);
//            p.add(v.x * deltaTime, v.y * deltaTime);
//        }
//    }
}
