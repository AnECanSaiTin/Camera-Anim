package cn.anecansaitin.cameraanim.client.ide;

import cn.anecansaitin.cameraanim.client.enums.MoveType;
import cn.anecansaitin.cameraanim.common.animation.CameraKeyframe;
import org.joml.Intersectionf;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static cn.anecansaitin.cameraanim.client.util.ClientUtil.*;

public class MoveModeData {
    private MoveType moveType = MoveType.NONE;
    private final Vector3f delta = new Vector3f();

    public MoveModeData() {
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void reset() {
        moveType = MoveType.NONE;
    }

    public boolean pickMoveModule(Vector3f origin, Vector3f direction, boolean leftClick) {
        int selectedTime = CameraAnimIdeCache.SELECTED_POINT.getPointTime();

        if (selectedTime < 0) {
            return false;
        }

        if (leftClick) {
            Vector3f pos = CameraAnimIdeCache.SELECTED_POINT.getPosition();

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
                CameraAnimIdeCache.MOVE_DATA.moveType = MoveType.X;
            }

            // y
            if (Intersectionf.intersectRayAab(origin, direction, new Vector3f(pos).add(-w, deadZone, -w), new Vector3f(pos).add(w, deadZone + l, w), resultPos) && min > resultPos.x) {
                min = resultPos.x;
                result = true;
                CameraAnimIdeCache.MOVE_DATA.moveType = MoveType.Y;
            }

            // z
            if (Intersectionf.intersectRayAab(origin, direction, new Vector3f(pos).add(-w, -w, deadZone), new Vector3f(pos).add(w, w, deadZone + l), resultPos) && min > resultPos.x) {
                min = resultPos.x;
                result = true;
                CameraAnimIdeCache.MOVE_DATA.moveType = MoveType.Z;
            }

            float spacing = 0.2f,
                    size = 0.3f;
            // xy
            if (Intersectionf.intersectRayAab(origin, direction, new Vector3f(pos).add(spacing, spacing, -w), new Vector3f(pos).add(size + spacing, size + spacing, w), resultPos) && min > resultPos.x) {
                min = resultPos.x;
                result = true;
                CameraAnimIdeCache.MOVE_DATA.moveType = MoveType.XY;
            }

            // xz
            if (Intersectionf.intersectRayAab(origin, direction, new Vector3f(pos).add(spacing, -w, spacing), new Vector3f(pos).add(size + spacing, w, size + spacing), resultPos) && min > resultPos.x) {
                min = resultPos.x;
                result = true;
                CameraAnimIdeCache.MOVE_DATA.moveType = MoveType.XZ;
            }

            // yz
            if (Intersectionf.intersectRayAab(origin, direction, new Vector3f(pos).add(-w, spacing, spacing), new Vector3f(pos).add(w, size + spacing, size + spacing), resultPos) && min > resultPos.x) {
                min = resultPos.x;
                result = true;
                CameraAnimIdeCache.MOVE_DATA.moveType = MoveType.YZ;
            }


            CameraAnimIdeCache.MOVE_DATA.delta.set(direction).mul(min).add(origin).sub(pos).mul(-1);
            return result;
        } else {
            Vector3f pos, min, max;

            switch (CameraAnimIdeCache.SELECTED_POINT.getControl()) {
                case LEFT -> {
                    CameraKeyframe point = CameraAnimIdeCache.PATH.getPoint(CameraAnimIdeCache.SELECTED_POINT.getPointTime());
                    if (point == null) return false;
                    pos = point.getPathBezier().getLeft();
                    min = new Vector3f(pos).sub(CameraAnimIdeCache.BEZIER_PICK_EXPAND, CameraAnimIdeCache.BEZIER_PICK_EXPAND, CameraAnimIdeCache.BEZIER_PICK_EXPAND);
                    max = new Vector3f(pos).add(CameraAnimIdeCache.BEZIER_PICK_EXPAND, CameraAnimIdeCache.BEZIER_PICK_EXPAND, CameraAnimIdeCache.BEZIER_PICK_EXPAND);
                }
                case RIGHT -> {
                    CameraKeyframe point = CameraAnimIdeCache.PATH.getPoint(CameraAnimIdeCache.SELECTED_POINT.getPointTime());
                    if (point == null) return false;
                    pos = point.getPathBezier().getRight();
                    min = new Vector3f(pos).sub(CameraAnimIdeCache.BEZIER_PICK_EXPAND, CameraAnimIdeCache.BEZIER_PICK_EXPAND, CameraAnimIdeCache.BEZIER_PICK_EXPAND);
                    max = new Vector3f(pos).add(CameraAnimIdeCache.BEZIER_PICK_EXPAND, CameraAnimIdeCache.BEZIER_PICK_EXPAND, CameraAnimIdeCache.BEZIER_PICK_EXPAND);
                }
                case NONE -> {
                    CameraKeyframe point = CameraAnimIdeCache.PATH.getPoint(CameraAnimIdeCache.SELECTED_POINT.getPointTime());
                    if (point == null) return false;
                    pos = point.getPos();
                    min = new Vector3f(pos).sub(CameraAnimIdeCache.POINT_PICK_EXPAND, CameraAnimIdeCache.POINT_PICK_EXPAND, CameraAnimIdeCache.POINT_PICK_EXPAND);
                    max = new Vector3f(pos).add(CameraAnimIdeCache.POINT_PICK_EXPAND, CameraAnimIdeCache.POINT_PICK_EXPAND, CameraAnimIdeCache.POINT_PICK_EXPAND);
                }
                case null, default -> {
                    return false;
                }
            }

            Vector2f resultPos = new Vector2f();

            if (Intersectionf.intersectRayAab(origin, direction, min, max, resultPos)) {
                CameraAnimIdeCache.MOVE_DATA.delta.set(resultPos.x);
                CameraAnimIdeCache.MOVE_DATA.moveType = MoveType.XYZ;
                return true;
            } else {
                return false;
            }
        }
    }

    public void move() {
        if (CameraAnimIdeCache.SELECTED_POINT.getPointTime() < 0 || moveType == MoveType.NONE) {
            return;
        }

        Vector3f pos = CameraAnimIdeCache.SELECTED_POINT.getPosition();

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
                        0, 0, org.joml.Math.abs(yRot) >= 90 ? 1 : -1,
                        1e-6f
                );

                float t = (a == 0) ? b : (b == 0) ? a : org.joml.Math.min(a, b);

                if (t < 0) {
                    return;
                }

                t = org.joml.Math.clamp(0, 100, t);
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

                t = org.joml.Math.clamp(0, 100, t);
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

                float t = (a == 0) ? b : (b == 0) ? a : org.joml.Math.min(a, b);

                if (t < 0) {
                    return;
                }

                t = org.joml.Math.clamp(0, 100, t);
                pos.z = view.z * t + origin.z + delta.z;
            }
            case XY -> {
                float t = Intersectionf.intersectRayPlane(
                        origin.x, origin.y, origin.z,
                        view.x, view.y, view.z,
                        pos.x, pos.y, pos.z,
                        0, 0, org.joml.Math.abs(yRot) < 90 ? -1 : 1,
                        1e-6f
                );

                if (t < 0) {
                    return;
                }

                t = org.joml.Math.clamp(0, 100, t);
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

                t = org.joml.Math.clamp(0, 100, t);
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
