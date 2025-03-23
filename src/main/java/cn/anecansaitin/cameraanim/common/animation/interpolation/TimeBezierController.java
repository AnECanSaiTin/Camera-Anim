package cn.anecansaitin.cameraanim.common.animation.interpolation;

import cn.anecansaitin.cameraanim.util.InterpolationMath;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import org.joml.Vector2f;

public class TimeBezierController {
    private static final Vector2f ZERO = new Vector2f();
    private static final Vector2f ONE = new Vector2f(1, 1);
    private static final Vector2f V_CACHE = new Vector2f();
    private final Vector2f left;
    private final Vector2f right;

    public TimeBezierController(Vector2f left, Vector2f right) {
        this.left = left;
        this.right = right;
    }

    public TimeBezierController() {
        this(new Vector2f(0.4f, 0.4f), new Vector2f(0.6f, 0.6f));
    }

    public Vector2f getLeft() {
        return left;
    }

    public void setLeft(float x, float y) {
        left.set(x, y);
    }

    public Vector2f getRight() {
        return right;
    }

    public void setRight(float x, float y) {
        right.set(x, y);
    }

    public float interpolate(float f) {
        return InterpolationMath.bezier(f, ZERO, left, right, ONE, V_CACHE).y;
    }

    public void easy() {
        left.set(0.25f, 0.1f);
        right.set(0.25f, 1);
    }

    public void easyIn() {
        left.set(0.42f, 0);
        right.set(1, 1);
    }

    public void easyOut() {
        left.set(0, 0);
        right.set(0.58f, 1);
    }

    public void easyInOut() {
        left.set(0.42f, 0);
        right.set(0.58f, 1);
    }

    public static ListTag toNBT(TimeBezierController controller) {
        ListTag root = new ListTag();
        root.add(FloatTag.valueOf(controller.left.x));
        root.add(FloatTag.valueOf(controller.left.y));
        root.add(FloatTag.valueOf(controller.right.x));
        root.add(FloatTag.valueOf(controller.right.y));
        return root;
    }

    public static TimeBezierController fromNBT(ListTag root) {
        return new TimeBezierController(
                new Vector2f(root.getFloat(0), root.getFloat(1)),
                new Vector2f(root.getFloat(2), root.getFloat(3))
        );
    }
}
