package lando.systems.ld59.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class RopePath {

    private static final Vector2 DEFAULT_GRAVITY = new Vector2(0, -1000f);
    private static final float DEFAULT_DAMPING = 0.98f; // 1 = no damping
    private static final int CONSTRAINT_ITERATIONS = 10;

    public final Array<Vector2> positions = new Array<>();
    public final Vector2 gravity = new Vector2(DEFAULT_GRAVITY);
    public float damping = DEFAULT_DAMPING;

    private final Array<Vector2> oldPositions = new Array<>();
    private final Array<Float> restDistances = new Array<>();
    private boolean[] pinned;

    public RopePath(Array<Vector2> initialPositions) {
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
        pinPoint(initialPositions.size - 2);
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
        // Verlet integration
        for (int i = 0; i < positions.size; i++) {
            if (pinned[i]) continue;

            var pos = positions.get(i);
            var old = oldPositions.get(i);

            float vx = (pos.x - old.x) * damping;
            float vy = (pos.y - old.y) * damping;

            old.set(pos);

            pos.x += vx + gravity.x * deltaTime * deltaTime;
            pos.y += vy + gravity.y * deltaTime * deltaTime;
        }

        // Satisfy distance constraints
        for (int iter = 0; iter < CONSTRAINT_ITERATIONS; iter++) {
            for (int i = 0; i < positions.size - 1; i++) {
                var p1 = positions.get(i);
                var p2 = positions.get(i + 1);

                float dx = p2.x - p1.x;
                float dy = p2.y - p1.y;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                float restDist = restDistances.get(i);

                if (dist < 0.0001f) continue;

                float constraintStiffness = 0.2f; // 1.0 = rigid, 0.1 = very stretchy
                float diff = (dist - restDist) / dist;
                float offsetX = dx * 0.5f * diff * constraintStiffness;
                float offsetY = dy * 0.5f * diff * constraintStiffness;

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
