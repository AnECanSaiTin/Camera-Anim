package cn.anecansaitin.cameraanim;

import net.minecraft.util.Mth;
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
        float oneMinusT = 1.0f - delta;

        dest.x = oneMinusT * oneMinusT * oneMinusT * p0.x +
                3 * oneMinusT * oneMinusT * delta * p1.x +
                3 * oneMinusT * delta * delta * p2.x +
                delta * delta * delta * p3.x;

        dest.y = oneMinusT * oneMinusT * oneMinusT * p0.y +
                3 * oneMinusT * oneMinusT * delta * p1.y +
                3 * oneMinusT * delta * delta * p2.y +
                delta * delta * delta * p3.y;

        dest.z = oneMinusT * oneMinusT * oneMinusT * p0.z +
                3 * oneMinusT * oneMinusT * delta * p1.z +
                3 * oneMinusT * delta * delta * p2.z +
                delta * delta * delta * p3.z;

        return dest;
    }
}
