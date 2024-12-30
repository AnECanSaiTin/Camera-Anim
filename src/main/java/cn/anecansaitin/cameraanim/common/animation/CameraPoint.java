package cn.anecansaitin.cameraanim.common.animation;

import org.joml.Quaternionf;
import org.joml.Vector3f;

/// 相机点位
public class CameraPoint {
    public static final CameraPoint NULL = new CameraPoint(new Vector3f(Float.MIN_VALUE), new Vector3f(), 0, PointInterpolationType.STEP);
    private final Vector3f position;
    private final Vector3f rotation;
    private float fov;
    private PointInterpolationType type;
    private final Vector3f leftBezierControl;
    private final Vector3f rightBezierControl;

    public CameraPoint(Vector3f position, Vector3f rotation, float fov, PointInterpolationType type) {
        this.position = position;
        this.rotation = rotation;
        this.fov = fov;
        this.type = type;
        this.leftBezierControl = new Vector3f();
        this.rightBezierControl = new Vector3f();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public PointInterpolationType getType() {
        return type;
    }

    public void setType(PointInterpolationType type) {
        this.type = type;
    }

    public Vector3f getLeftBezierControl() {
        return leftBezierControl;
    }

    public void setLeftBezierControl(float x, float y, float z) {
        leftBezierControl.set(x, y, z);
    }

    public Vector3f getRightBezierControl() {
        return rightBezierControl;
    }

    public void setRightBezierControl(float x, float y, float z) {
        rightBezierControl.set(x, y, z);
    }
}
