package cn.anecansaitin.cameraanim.common.animation.interpolation;

import cn.anecansaitin.cameraanim.util.InterpolationMath;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import org.joml.Vector3f;

public class Vec3BezierController {
    private final Vector3f left;
    private final Vector3f right;

    public Vec3BezierController(Vector3f left, Vector3f right) {
        this.left = left;
        this.right = right;
    }

    public Vec3BezierController() {
        this(new Vector3f(), new Vector3f());
    }

    public Vector3f getLeft() {
        return left;
    }

    public void setLeft(float x, float y, float z) {
        left.set(x, y, z);
    }

    public Vector3f getRight() {
        return right;
    }

    public void setRight(float x, float y, float z) {
        right.set(x, y, z);
    }

    public void reset(Vector3f start, Vector3f end) {
        left.set(start).lerp(end, 0.4f);
        right.set(end).lerp(start, 0.4f);
    }

    public Vector3f interpolate(float delta, Vector3f start, Vector3f end, Vector3f dest) {
        return InterpolationMath.bezier(delta, start, left, right, end, dest);
    }

    public static ListTag toNBT(Vec3BezierController bezier) {
        ListTag root = new ListTag();
        root.add(FloatTag.valueOf(bezier.left.x));
        root.add(FloatTag.valueOf(bezier.left.y));
        root.add(FloatTag.valueOf(bezier.left.z));
        root.add(FloatTag.valueOf(bezier.right.x));
        root.add(FloatTag.valueOf(bezier.right.y));
        root.add(FloatTag.valueOf(bezier.right.z));
        return root;
    }

    public static Vec3BezierController fromNBT(ListTag root) {
        return new Vec3BezierController(
                new Vector3f(root.getFloat(0), root.getFloat(1), root.getFloat(2)),
                new Vector3f(root.getFloat(3), root.getFloat(4), root.getFloat(5))
        );
    }
}
