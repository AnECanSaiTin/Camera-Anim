package cn.anecansaitin.cameraanim.client;

import cn.anecansaitin.cameraanim.common.animation.CameraPoint;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraTrack;
import cn.anecansaitin.cameraanim.common.animation.PointInterpolationType;
import org.joml.*;
import org.joml.Math;

import static cn.anecansaitin.cameraanim.client.ClientUtil.*;

public class TrackCache {
    public static final float POINT_PICK_EXPAND = 0.2f;
    public static boolean EDIT;
    public static boolean VIEW;
    private static Mode MODE = Mode.MOVE;
    private static final MoveModeData MOVE_DATA = new MoveModeData();
    private static GlobalCameraTrack TRACK = GlobalCameraTrack.NULL;
    private static final SelectedPoint SELECTED_POINT = new SelectedPoint();
    private static final float BEZIER_PICK_EXPAND = 0.1f;

    static {
        // 测试数据
        TRACK = new GlobalCameraTrack("test");
        TRACK.add(new CameraPoint(new Vector3f(1, 56, 3), new Quaternionf(), 70, PointInterpolationType.LINEAR));
        TRACK.add(new CameraPoint(new Vector3f(3, 56, 5), new Quaternionf(), 70, PointInterpolationType.LINEAR));
        TRACK.add(new CameraPoint(new Vector3f(7, 56, 8), new Quaternionf(), 70, PointInterpolationType.LINEAR));
        TRACK.add(new CameraPoint(new Vector3f(5, 56, 0), new Quaternionf(), 70, PointInterpolationType.LINEAR));

        TRACK.add(new CameraPoint(new Vector3f(1, 58, 3), new Quaternionf(), 70, PointInterpolationType.SMOOTH));
        TRACK.add(new CameraPoint(new Vector3f(3, 58, 5), new Quaternionf(), 70, PointInterpolationType.SMOOTH));
        TRACK.add(new CameraPoint(new Vector3f(5, 58, 0), new Quaternionf(), 70, PointInterpolationType.SMOOTH));
        TRACK.add(new CameraPoint(new Vector3f(7, 58, 8), new Quaternionf(), 70, PointInterpolationType.SMOOTH));

        TRACK.add(new CameraPoint(new Vector3f(1, 59, 3), new Quaternionf(), 70, PointInterpolationType.STEP));
        TRACK.add(new CameraPoint(new Vector3f(3, 59, 5), new Quaternionf(), 70, PointInterpolationType.STEP));
        TRACK.add(new CameraPoint(new Vector3f(5, 59, 0), new Quaternionf(), 70, PointInterpolationType.STEP));
        TRACK.add(new CameraPoint(new Vector3f(7, 59, 8), new Quaternionf(), 70, PointInterpolationType.STEP));

        CameraPoint b1 = new CameraPoint(new Vector3f(1, 60, 3), new Quaternionf(), 70, PointInterpolationType.BEZIER);
        CameraPoint b2 = new CameraPoint(new Vector3f(3, 60, 5), new Quaternionf(), 70, PointInterpolationType.BEZIER);
        CameraPoint b3 = new CameraPoint(new Vector3f(5, 60, 0), new Quaternionf(), 70, PointInterpolationType.BEZIER);
        CameraPoint b4 = new CameraPoint(new Vector3f(7, 60, 8), new Quaternionf(), 70, PointInterpolationType.BEZIER);
        TRACK.add(b1);
        TRACK.add(b2);
        TRACK.add(b3);
        TRACK.add(b4);
        b1.getLeftBezierControl().add(0, 1, 0);
        b2.getLeftBezierControl().add(0, -1, 0);
        b3.getLeftBezierControl().add(1, 0, 1);
        b4.getLeftBezierControl().add(-1, 1, -1);
    }

    // 每一帧的更新
    public static void tick() {
        switch (MODE) {
            case MOVE -> MOVE_DATA.move();
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

        if (SELECTED_POINT.pointIndex < 0) {
            return;
        }

        if (MODE != Mode.MOVE) {
            return;
        }

        MOVE_DATA.pickMoveModule(origin, direction, false);
    }

    private static float pickBezier(float length, Vector3f origin, Vector3f direction) {
        int selectedIndex = SELECTED_POINT.getPointIndex();

        if (selectedIndex <= 0) {
            return length;
        }
        // 检查是否为贝塞尔曲线控制点
        CameraPoint point = TRACK.getPoint(selectedIndex);

        if (point.getType() != PointInterpolationType.BEZIER) {
            return length;
        }

        CameraPoint pre = TRACK.getPoint(selectedIndex - 1);
        Vector3f leftBezierControl = point.getLeftBezierControl();
        float leftL = leftBezierControl.distanceSquared(origin);
        Vector3f rightBezierControl = pre.getRightBezierControl();
        float rightL = rightBezierControl.distanceSquared(origin);

        if (leftL <= length && Intersectionf.testRayAab(origin, direction, new Vector3f(leftBezierControl).sub(BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND), new Vector3f(leftBezierControl).add(BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND))) {
            length = leftL;
            SELECTED_POINT.setControl(ControlType.LEFT);
        }

        if (rightL <= length && Intersectionf.testRayAab(origin, direction, new Vector3f(rightBezierControl).sub(BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND), new Vector3f(rightBezierControl).add(BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND))) {
            length = rightL;
            SELECTED_POINT.setControl(ControlType.RIGHT);
        }

        return length;
    }

    private static void pickPoint(float length, Vector3f origin, Vector3f direction) {
        int index = -1;

        for (int i = 0, count = TRACK.getCount(); i < count; i++) {
            Vector3f position = TRACK.getPoint(i).getPosition();
            float d = position.distanceSquared(origin);

            if (d > length) {
                continue;
            }

            if (Intersectionf.testRayAab(origin, direction, new Vector3f(position).sub(POINT_PICK_EXPAND, POINT_PICK_EXPAND, POINT_PICK_EXPAND), new Vector3f(position).add(POINT_PICK_EXPAND, POINT_PICK_EXPAND, POINT_PICK_EXPAND))) {
                if (!(d < length)) {
                    continue;
                }

                length = d;
                index = i;
            }
        }

        if (index >= 0) {
            SELECTED_POINT.setSelected(index);
        }
    }

    public static GlobalCameraTrack getTrack() {
        return TRACK;
    }

    public static void setTrack(GlobalCameraTrack track) {
        TRACK = track;
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
        private int pointIndex = -1;
        private ControlType control = ControlType.NONE;

        private void setSelected(int pointIndex) {
            this.pointIndex = pointIndex;
            control = ControlType.NONE;
        }

        private void setControl(ControlType control) {
            this.control = control;
        }

        public int getPointIndex() {
            return pointIndex;
        }

        public ControlType getControl() {
            return control;
        }

        private void reset() {
            pointIndex = -1;
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
        private final Vector3f v3Cache = new Vector3f();
        private final Vector2f v2Cache = new Vector2f();

        private MoveModeData() {
        }

        public MoveType getMoveType() {
            return moveType;
        }

        public void reset() {
            moveType = MoveType.NONE;
        }

        private boolean pickMoveModule(Vector3f origin, Vector3f direction, boolean left) {
            int selectedIndex = SELECTED_POINT.getPointIndex();

            if (selectedIndex < 0) {
                return false;
            }

            if (left) {
                Vector3f pos;

                switch (SELECTED_POINT.getControl()) {
                    case LEFT -> {
                        CameraPoint selectedPoint = TRACK.getPoint(selectedIndex);
                        pos = selectedPoint.getLeftBezierControl();
                    }
                    case RIGHT -> {
                        CameraPoint selectedPoint = TRACK.getPoint(selectedIndex - 1);
                        pos = selectedPoint.getRightBezierControl();
                    }
                    case NONE -> {
                        CameraPoint selectedPoint = TRACK.getPoint(selectedIndex);
                        pos = selectedPoint.getPosition();
                    }
                    case null, default -> {
                        return false;
                    }
                }

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
                        pos = TRACK.getPoint(SELECTED_POINT.pointIndex).getLeftBezierControl();
                        min = new Vector3f(pos).sub(BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND);
                        max = new Vector3f(pos).add(BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND);
                    }
                    case RIGHT -> {
                        pos = TRACK.getPoint(SELECTED_POINT.pointIndex - 1).getRightBezierControl();
                        min = new Vector3f(pos).sub(BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND);
                        max = new Vector3f(pos).add(BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND, BEZIER_PICK_EXPAND);
                    }
                    case NONE -> {
                        pos = TRACK.getPoint(SELECTED_POINT.pointIndex).getPosition();
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
            if (SELECTED_POINT.pointIndex < 0 || moveType == MoveType.NONE) {
                return;
            }

            Vector3f pos;

            switch (SELECTED_POINT.control) {
                case LEFT -> pos = TRACK.getPoint(SELECTED_POINT.pointIndex).getLeftBezierControl();
                case RIGHT -> pos = TRACK.getPoint(SELECTED_POINT.pointIndex - 1).getRightBezierControl();
                case NONE -> pos = TRACK.getPoint(SELECTED_POINT.pointIndex).getPosition();
                case null, default -> {
                    return;
                }
            }

            Vector3f view = playerView();
            Vector3f origin = playerEyePos();
            float yRot = playerYHeadRot();
            float xRot = playerXRot();


            /// 解释下移动的算法
            /// 从视线处发出射线，并与对应平面相交，得到交点
            /// 根据交点的坐标与delta相加，得到目标坐标
            switch (moveType) {
                case X -> {
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
