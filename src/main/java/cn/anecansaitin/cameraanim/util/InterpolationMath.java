package cn.anecansaitin.cameraanim.util;

import net.minecraft.util.Mth;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class InterpolationMath {
    public static Vector3f line(float delta, Vector3f p1, Vector3f p2, Vector3f dest) {
        return dest.set(Mth.lerp(delta, p1.x, p2.x), Mth.lerp(delta, p1.y, p2.y), Mth.lerp(delta, p1.z, p2.z));
    }

    public static Vector3f catmullRom(float delta, Vector3f pre, Vector3f p1, Vector3f p2, Vector3f after, Vector3f dest) {
        return dest.set(
                Mth.catmullrom(delta, pre.x, p1.x, p2.x, after.x),
                Mth.catmullrom(delta, pre.y, p1.y, p2.y, after.y),
                Mth.catmullrom(delta, pre.z, p1.z, p2.z, after.z)
        );
    }

    public static Vector3f bezier(float delta, Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f dest) {
        return dest.set(bezier(delta, p0.x, p1.x, p2.x, p3.x), bezier(delta, p0.y, p1.y, p2.y, p3.y), bezier(delta, p0.z, p1.z, p2.z, p3.z));
    }

    public static Vector2f line(float delta, Vector2f p1, Vector2f p2, Vector2f dest) {
        return dest.set(Mth.lerp(delta, p1.x, p2.x), Mth.lerp(delta, p1.y, p2.y));
    }

    public static Vector2f bezier(float delta, Vector2f p0, Vector2f p1, Vector2f p2, Vector2f p3, Vector2f dest) {
        return dest.set(bezier(delta, p0.x, p1.x, p2.x, p3.x), bezier(delta, p0.y, p1.y, p2.y, p3.y));
    }

    public static float bezier(float delta, float p0, float p1, float p2, float p3) {
        float oneMinusT = 1.0f - delta;

        return oneMinusT * oneMinusT * oneMinusT * p0 +
                3 * oneMinusT * oneMinusT * delta * p1 +
                3 * oneMinusT * delta * delta * p2 +
                delta * delta * delta * p3;
    }
}