package cn.anecansaitin.cameraanim.client.ide;

import cn.anecansaitin.cameraanim.client.enums.ControlType;
import cn.anecansaitin.cameraanim.client.enums.Mode;
import cn.anecansaitin.cameraanim.client.enums.MoveType;
import cn.anecansaitin.cameraanim.common.animation.*;
import cn.anecansaitin.cameraanim.common.animation.interpolation.Vec3BezierController;
import cn.anecansaitin.cameraanim.common.animation.interpolation.types.PathInterpolator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.joml.*;

public class CameraAnimIdeCache {
    public static final float POINT_PICK_EXPAND = 0.2f;
    public static boolean EDIT;
    public static boolean VIEW;
    public static boolean PREVIEW;
    private static Mode MODE = Mode.MOVE;
    public static final MoveModeData MOVE_DATA = new MoveModeData();
    public static GlobalCameraPath PATH = new GlobalCameraPath("new");
    public static final SelectedPoint SELECTED_POINT = new SelectedPoint();
    public static final float BEZIER_PICK_EXPAND = 0.1f;

    private static final Vector3f NATIVE_POS = new Vector3f();
    private static final Vector3f NATIVE_ROT = new Vector3f();

    /*static {
        // 测试数据
        TRACK = new GlobalCameraPath("test");
        TRACK.add(new CameraKeyframe(new Vector3f(1, 56, 3), new Vector3f(), 70, PathInterpolator.LINEAR));
        TRACK.add(new CameraKeyframe(new Vector3f(3, 56, 5), new Vector3f(), 70, PathInterpolator.LINEAR));
        TRACK.add(new CameraKeyframe(new Vector3f(7, 56, 8), new Vector3f(), 70, PathInterpolator.LINEAR));
        TRACK.add(new CameraKeyframe(new Vector3f(5, 56, 0), new Vector3f(), 70, PathInterpolator.LINEAR));

        TRACK.add(new CameraKeyframe(new Vector3f(1, 58, 3), new Vector3f(), 70, PathInterpolator.SMOOTH));
        TRACK.add(new CameraKeyframe(new Vector3f(3, 58, 5), new Vector3f(), 70, PathInterpolator.SMOOTH));
        TRACK.add(new CameraKeyframe(new Vector3f(5, 58, 0), new Vector3f(), 70, PathInterpolator.SMOOTH));
        TRACK.add(new CameraKeyframe(new Vector3f(7, 58, 8), new Vector3f(), 70, PathInterpolator.SMOOTH));

        TRACK.add(new CameraKeyframe(new Vector3f(1, 59, 3), new Vector3f(), 70, PathInterpolator.STEP));
        TRACK.add(new CameraKeyframe(new Vector3f(3, 59, 5), new Vector3f(), 70, PathInterpolator.STEP));
        TRACK.add(new CameraKeyframe(new Vector3f(5, 59, 0), new Vector3f(), 70, PathInterpolator.STEP));
        TRACK.add(new CameraKeyframe(new Vector3f(7, 59, 8), new Vector3f(), 70, PathInterpolator.STEP));

        CameraKeyframe b1 = new CameraKeyframe(new Vector3f(1, 60, 3), new Vector3f(), 70, PathInterpolator.BEZIER);
        CameraKeyframe b2 = new CameraKeyframe(new Vector3f(3, 60, 5), new Vector3f(), 70, PathInterpolator.BEZIER);
        CameraKeyframe b3 = new CameraKeyframe(new Vector3f(5, 60, 0), new Vector3f(), 70, PathInterpolator.BEZIER);
        CameraKeyframe b4 = new CameraKeyframe(new Vector3f(7, 60, 8), new Vector3f(), 70, PathInterpolator.BEZIER);
        TRACK.add(b1);
        TRACK.add(b2);
        TRACK.add(b3);
        TRACK.add(b4);
        b1.getPathBezier().getRight().add(0, 1, 0);
        b2.getPathBezier().getRight().add(0, -1, 0);
        b3.getPathBezier().getRight().add(1, 0, 1);
        b4.getPathBezier().getRight().add(-1, 1, -1);
        b4.setPosTimeInterpolator(TimeInterpolator.BEZIER);
        b4.getPosBezier().easyInOut();
    }*/

    // 每一帧的更新
    public static void tick() {
        if (MODE == Mode.MOVE) {
            MOVE_DATA.move();
        }
    }

    public static void leftPick(Vector3f origin, Vector3f direction, float length) {
        if (MODE == Mode.MOVE && MOVE_DATA.getMoveType() != MoveType.NONE) {
            return;
        }

        length += 0.1f;
        length *= length;

        if (MODE == Mode.MOVE && MOVE_DATA.pickMoveModule(origin, direction, true)) return;
        length = pickBezier(length, origin, direction);
        pickPoint(length, origin, direction);
    }

    public static void rightPick(Vector3f origin, Vector3f direction, float length) {
        if (MODE == Mode.MOVE && MOVE_DATA.getMoveType() != MoveType.NONE) {
            return;
        }

        if (SELECTED_POINT.getPointTime() < 0) {
            return;
        }

        if (MODE != Mode.MOVE) {
            return;
        }

        MOVE_DATA.pickMoveModule(origin, direction, false);
    }

    private static float pickBezier(float length, Vector3f origin, Vector3f direction) {
        int selectedTime = SELECTED_POINT.getPointTime();

        if (selectedTime <= 0) {
            return length;
        }
        // 检查是否为贝塞尔曲线控制点
        CameraKeyframe point = PATH.getPoint(selectedTime);

        if (point == null || point.getPathInterpolator() != PathInterpolator.BEZIER) {
            return length;
        }

        Vec3BezierController controller = point.getPathBezier();
        Vector3f right = controller.getRight();
        float rightL = right.distanceSquared(origin);

        if (rightL <= length && Intersectionf.testRayAab(origin, direction, new Vector3f(right).sub(BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND), new Vector3f(right).add(BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND))) {
            length = rightL;
            SELECTED_POINT.setControl(ControlType.RIGHT);
        }

        CameraKeyframe pre = PATH.getPrePoint(selectedTime);

        if (pre == null) {
            return length;
        }

        Vector3f left = controller.getLeft();
        float leftL = left.distanceSquared(origin);

        if (leftL <= length && Intersectionf.testRayAab(origin, direction, new Vector3f(left).sub(BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND), new Vector3f(left).add(BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND))) {
            length = leftL;
            SELECTED_POINT.setControl(ControlType.LEFT);
        }

        return length;
    }

    private static void pickPoint(float length, Vector3f origin, Vector3f direction) {
        int time = -1;

        for (Int2ObjectMap.Entry<CameraKeyframe> entry : PATH.getEntries()) {
            Vector3f position = entry.getValue().getPos();
            float d = position.distanceSquared(origin);

            if (d > length) {
                continue;
            }

            if (Intersectionf.testRayAab(origin, direction, new Vector3f(position).sub(POINT_PICK_EXPAND, POINT_PICK_EXPAND, POINT_PICK_EXPAND), new Vector3f(position).add(POINT_PICK_EXPAND, POINT_PICK_EXPAND, POINT_PICK_EXPAND))) {
                if (!(d < length)) {
                    continue;
                }

                length = d;
                time = entry.getIntKey();
            }
        }

        if (time >= 0) {
            SELECTED_POINT.setSelected(time);
        }
    }

    public static GlobalCameraPath getPath() {
        return PATH;
    }

    public static void setPath(GlobalCameraPath path) {
        PATH = path;
        SELECTED_POINT.reset();
    }

    public static SelectedPoint getSelectedPoint() {
        return SELECTED_POINT;
    }

    public static Mode getMode() {
        return MODE;
    }

    public static MoveModeData getMoveMode() {
        return MOVE_DATA;
    }

    public static void setNative(Vector3f pos, Vector3f rot) {
        NATIVE_POS.set(pos);
        NATIVE_ROT.set(rot);
        PATH.setNativeMode(true);
    }

    public static Vector3f getNativePos() {
        return NATIVE_POS;
    }

    public static Vector3f getNativeRot() {
        return NATIVE_ROT;
    }
}
