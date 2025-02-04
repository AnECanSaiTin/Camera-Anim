package cn.anecansaitin.cameraanim.client;

import cn.anecansaitin.cameraanim.common.animation.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.Math;

import static cn.anecansaitin.cameraanim.client.ClientUtil.*;

public class CameraAnimIdeCache {
    public static final float POINT_PICK_EXPAND = 0.2f;
    public static boolean EDIT;
    public static boolean VIEW;
    public static boolean PREVIEW;
    private static Mode MODE = Mode.MOVE;
    private static final MoveModeData MOVE_DATA = new MoveModeData();
    private static GlobalCameraPath PATH = new GlobalCameraPath("new");
    private static final SelectedPoint SELECTED_POINT = new SelectedPoint();
    private static final float BEZIER_PICK_EXPAND = 0.1f;

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
        if (MODE == Mode.MOVE && MOVE_DATA.moveType != MoveType.NONE) {
            return;
        }

        length += 0.1f;
        length *= length;

        if (MODE == Mode.MOVE && MOVE_DATA.pickMoveModule(origin, direction, true)) return;
        length = pickBezier(length, origin, direction);
        pickPoint(length, origin, direction);
    }

    public static void rightPick(Vector3f origin, Vector3f direction, float length) {
        if (MODE == Mode.MOVE && MOVE_DATA.moveType != MoveType.NONE) {
            return;
        }

        if (SELECTED_POINT.pointTime < 0) {
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

    public static class SelectedPoint {
        private int pointTime = -1;
        private ControlType control = ControlType.NONE;

        public void setSelected(int time) {
            this.pointTime = time;
            control = ControlType.NONE;
        }

        private void setControl(ControlType control) {
            this.control = control;
        }

        public int getPointTime() {
            return pointTime;
        }

        public ControlType getControl() {
            return control;
        }

        @Nullable
        public Vector3f getPosition() {
            Vector3f pos = null;

            switch (control) {
                case LEFT -> {
                    CameraKeyframe point = PATH.getPoint(pointTime);
                    if (point == null) break;
                    pos = point.getPathBezier().getLeft();
                }
                case RIGHT -> {
                    CameraKeyframe point = PATH.getPoint(pointTime);
                    if (point == null) break;
                    pos = point.getPathBezier().getRight();
                }
                case NONE -> {
                    CameraKeyframe point = PATH.getPoint(pointTime);
                    if (point == null) break;
                    pos = point.getPos();
                }
            }

            return pos;
        }

        public void reset() {
            pointTime = -1;
            control = ControlType.NONE;
        }
    }

    public enum ControlType {
        LEFT,
        RIGHT,
        NONE
    }

    public enum Mode {
        NONE,
        MOVE
    }

    public static class MoveModeData {
        private MoveType moveType = MoveType.NONE;
        private final Vector3f delta = new Vector3f();

        private MoveModeData() {
        }

        public MoveType getMoveType() {
            return moveType;
        }

        public void reset() {
            moveType = MoveType.NONE;
        }

        private boolean pickMoveModule(Vector3f origin, Vector3f direction, boolean leftClick) {
            int selectedTime = SELECTED_POINT.getPointTime();

            if (selectedTime < 0) {
                return false;
            }

            if (leftClick) {
                Vector3f pos = SELECTED_POINT.getPosition();

                if (pos == null) return false;

                float deadZone = 0.1f,
                        l = 0.9f + 0.35f,
                        w = 0.1f;

                // 快速检测是否在范围内
                if (!Intersectionf.testRayAab(origin, direction, new Vector3f(pos).sub(w, w, 0), new Vector3f(pos).add(deadZone + l, deadZone + l, deadZone + l))) {
                    return false;
                }

                float min = Float.MAX_VALUE;
                Vector2f resultPos = new Vector2f();
                boolean result = false;

                // x
                if (Intersectionf.intersectRayAab(origin, direction, new Vector3f(pos).add(deadZone, -w, -w), new Vector3f(pos).add(deadZone + l, w, w), resultPos)) {
                    min = resultPos.x;
                    result = true;
                    MOVE_DATA.moveType = MoveType.X;
                }

                // y
                if (Intersectionf.intersectRayAab(origin, direction, new Vector3f(pos).add(-w, deadZone, -w), new Vector3f(pos).add(w, deadZone + l, w), resultPos) && min > resultPos.x) {
                    min = resultPos.x;
                    result = true;
                    MOVE_DATA.moveType = MoveType.Y;
                }

                // z
                if (Intersectionf.intersectRayAab(origin, direction, new Vector3f(pos).add(-w, -w, deadZone), new Vector3f(pos).add(w, w, deadZone + l), resultPos) && min > resultPos.x) {
                    min = resultPos.x;
                    result = true;
                    MOVE_DATA.moveType = MoveType.Z;
                }

                float spacing = 0.2f,
                        size = 0.3f;
                // xy
                if (Intersectionf.intersectRayAab(origin, direction, new Vector3f(pos).add(spacing, spacing, -w), new Vector3f(pos).add(size + spacing, size + spacing, w), resultPos) && min > resultPos.x) {
                    min = resultPos.x;
                    result = true;
                    MOVE_DATA.moveType = MoveType.XY;
                }

                // xz
                if (Intersectionf.intersectRayAab(origin, direction, new Vector3f(pos).add(spacing, -w, spacing), new Vector3f(pos).add(size + spacing, w, size + spacing), resultPos) && min > resultPos.x) {
                    min = resultPos.x;
                    result = true;
                    MOVE_DATA.moveType = MoveType.XZ;
                }

                // yz
                if (Intersectionf.intersectRayAab(origin, direction, new Vector3f(pos).add(-w, spacing, spacing), new Vector3f(pos).add(w, size + spacing, size + spacing), resultPos) && min > resultPos.x) {
                    min = resultPos.x;
                    result = true;
                    MOVE_DATA.moveType = MoveType.YZ;
                }


                MOVE_DATA.delta.set(direction).mul(min).add(origin).sub(pos).mul(-1);
                return result;
            } else {
                Vector3f pos, min, max;

                switch (SELECTED_POINT.control) {
                    case LEFT -> {
                        CameraKeyframe point = PATH.getPoint(SELECTED_POINT.pointTime);
                        if (point == null) return false;
                        pos = point.getPathBezier().getLeft();
                        min = new Vector3f(pos).sub(BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND);
                        max = new Vector3f(pos).add(BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND);
                    }
                    case RIGHT -> {
                        CameraKeyframe point = PATH.getPoint(SELECTED_POINT.pointTime);
                        if (point == null) return false;
                        pos = point.getPathBezier().getRight();
                        min = new Vector3f(pos).sub(BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND);
                        max = new Vector3f(pos).add(BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND);
                    }
                    case NONE -> {
                        CameraKeyframe point = PATH.getPoint(SELECTED_POINT.pointTime);
                        if (point == null) return false;
                        pos = point.getPos();
                        min = new Vector3f(pos).sub(POINT_PICK_EXPAND, POINT_PICK_EXPAND, POINT_PICK_EXPAND);
                        max = new Vector3f(pos).add(POINT_PICK_EXPAND, POINT_PICK_EXPAND, POINT_PICK_EXPAND);
                    }
                    case null, default -> {
                        return false;
                    }
                }

                Vector2f resultPos = new Vector2f();

                if (Intersectionf.intersectRayAab(origin, direction, min, max, resultPos)) {
                    MOVE_DATA.delta.set(resultPos.x);
                    MOVE_DATA.moveType = MoveType.XYZ;
                    return true;
                } else {
                    return false;
                }
            }
        }

        private void move() {
            if (SELECTED_POINT.pointTime < 0 || moveType == MoveType.NONE) {
                return;
            }

            Vector3f pos = SELECTED_POINT.getPosition();

            if (pos == null) return;

            Vector3f view = playerView();
            Vector3f origin = playerEyePos();
            float yRot = playerYHeadRot();
            float xRot = playerXRot();

            /// 解释下移动的算法
            /// 从视线处发出射线，并与对应平面相交，得到交点
            /// 根据交点的坐标与delta相加，得到目标坐标
            switch (moveType) {
                case X -> {
                    float a = Intersectionf.intersectRayPlane(
                            origin.x, origin.y, origin.z,
                            view.x, view.y, view.z,
                            pos.x, pos.y, pos.z,
                            0, xRot < 0 ? -1 : 1, 0,
                            1e-6f
                    );

                    float b = Intersectionf.intersectRayPlane(
                            origin.x, origin.y, origin.z,
                            view.x, view.y, view.z,
                            pos.x, pos.y, pos.z,
                            0, 0, Math.abs(yRot) >= 90 ? 1 : -1,
                            1e-6f
                    );

                    float t = (a == 0) ? b : (b == 0) ? a : Math.min(a, b);

                    if (t < 0) {
                        return;
                    }

                    t = Math.clamp(0, 100, t);
                    pos.x = view.x * t + origin.x + delta.x;
                }
                case Y -> {
                    float t = Intersectionf.intersectRayPlane(
                            origin.x, origin.y, origin.z,
                            view.x, view.y, view.z,
                            pos.x, pos.y, pos.z,
                            yRot < 0 ? -1 : 1, 0, 0,
                            1e-6f
                    );

                    if (t < 0) {
                        return;
                    }

                    t = Math.clamp(0, 100, t);
                    pos.y = view.y * t + origin.y + delta.y;
                }
                case Z -> {
                    float a = Intersectionf.intersectRayPlane(
                            origin.x, origin.y, origin.z,
                            view.x, view.y, view.z,
                            pos.x, pos.y, pos.z,
                            0, xRot < 0 ? -1 : 1, 0,
                            1e-6f
                    );

                    float b = Intersectionf.intersectRayPlane(
                            origin.x, origin.y, origin.z,
                            view.x, view.y, view.z,
                            pos.x, pos.y, pos.z,
                            yRot >= 0 ? 1 : -1, 0, 0,
                            1e-6f
                    );

                    float t = (a == 0) ? b : (b == 0) ? a : Math.min(a, b);

                    if (t < 0) {
                        return;
                    }

                    t = Math.clamp(0, 100, t);
                    pos.z = view.z * t + origin.z + delta.z;
                }
                case XY -> {
                    float t = Intersectionf.intersectRayPlane(
                            origin.x, origin.y, origin.z,
                            view.x, view.y, view.z,
                            pos.x, pos.y, pos.z,
                            0, 0, Math.abs(yRot) < 90 ? -1 : 1,
                            1e-6f
                    );

                    if (t < 0) {
                        return;
                    }

                    t = Math.clamp(0, 100, t);
                    pos.x = view.x * t + origin.x + delta.x;
                    pos.y = view.y * t + origin.y + delta.y;
                }
                case XZ -> {
                    float t = Intersectionf.intersectRayPlane(
                            origin.x, origin.y, origin.z,
                            view.x, view.y, view.z,
                            pos.x, pos.y, pos.z,
                            0, xRot < 0 ? -1 : 1, 0,
                            1e-6f
                    );

                    if (t < 0) {
                        return;
                    }

                    t = Math.clamp(0, 100, t);
                    pos.x = view.x * t + origin.x + delta.x;
                    pos.z = view.z * t + origin.z + delta.z;
                }
                case YZ -> {
                    float t = Intersectionf.intersectRayPlane(
                            origin.x, origin.y, origin.z,
                            view.x, view.y, view.z,
                            pos.x, pos.y, pos.z,
                            yRot < 0 ? -1 : 1, 0, 0,
                            1e-6f
                    );

                    if (t < 0) {
                        return;
                    }

                    t = Math.clamp(0, 100, t);
                    pos.z = view.z * t + origin.z + delta.z;
                    pos.y = view.y * t + origin.y + delta.y;
                }
                case XYZ -> pos.set(view).mul(delta.x).add(origin);
            }
        }
    }

    public enum MoveType {
        X,
        Y,
        Z,
        XY,
        XZ,
        YZ,
        XYZ,
        NONE
    }
}
