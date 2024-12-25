package cn.anecansaitin.cameraanim.client;

import cn.anecansaitin.cameraanim.common.animation.CameraPoint;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraTrack;
import cn.anecansaitin.cameraanim.common.animation.PointInterpolationType;
import org.joml.Intersectionf;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TrackCache {
    public static boolean EDIT;
    public static boolean VIEW;
    public static Mode MODE = Mode.MOVE;
    private static GlobalCameraTrack TRACK = GlobalCameraTrack.NULL;
    private static final SelectedPoint SELECTED_POINT = new SelectedPoint();

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

    public static void pick(Vector3f origin, Vector3f direction, float length) {
        length += 0.1f;
        length *= length;
        int selectedIndex = SELECTED_POINT.getPointIndex();

        if (selectedIndex > 0) {
            // 检查是否为贝塞尔曲线控制点
            CameraPoint point = TRACK.getPoint(selectedIndex);

            if (point.getType() == PointInterpolationType.BEZIER) {
                CameraPoint pre = TRACK.getPoint(selectedIndex - 1);
                Vector3f leftBezierControl = point.getLeftBezierControl();
                float leftL = leftBezierControl.distanceSquared(origin);
                Vector3f rightBezierControl = pre.getRightBezierControl();
                float rightL = rightBezierControl.distanceSquared(origin);

                if (leftL <= length && Intersectionf.testRayAab(origin, direction, new Vector3f(leftBezierControl).sub(0.06f, 0.06f, 0.06f), new Vector3f(leftBezierControl).add(0.06f, 0.06f, 0.06f))) {
                    length = leftL;
                    SELECTED_POINT.setControl(ControlType.LEFT);
                }

                if (rightL <= length && Intersectionf.testRayAab(origin, direction, new Vector3f(rightBezierControl).sub(0.06f, 0.06f, 0.06f), new Vector3f(rightBezierControl).add(0.06f, 0.06f, 0.06f))) {
                    length = rightL;
                    SELECTED_POINT.setControl(ControlType.RIGHT);
                }
            }
        }

        int index = -1;

        for (int i = 0, count = TRACK.getCount(); i < count; i++) {
            Vector3f position = TRACK.getPoint(i).getPosition();
            float d = position.distanceSquared(origin);

            if (d > length) {
                continue;
            }

            if (Intersectionf.testRayAab(origin, direction, new Vector3f(position).sub(0.12f, 0.12f, 0.12f), new Vector3f(position).add(0.12f, 0.12f, 0.12f))) {
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
        MOVE
    }
}
