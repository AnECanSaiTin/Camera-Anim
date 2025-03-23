package cn.anecansaitin.cameraanim.client.ide;

import cn.anecansaitin.cameraanim.client.enums.ControlType;
import cn.anecansaitin.cameraanim.common.animation.CameraKeyframe;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class SelectedPoint {
    private int pointTime = -1;
    private ControlType control = ControlType.NONE;

    public void setSelected(int time) {
        this.pointTime = time;
        control = ControlType.NONE;
    }

    public void setControl(ControlType control) {
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
                CameraKeyframe point = CameraAnimIdeCache.PATH.getPoint(pointTime);
                if (point == null) break;
                pos = point.getPathBezier().getLeft();
            }
            case RIGHT -> {
                CameraKeyframe point = CameraAnimIdeCache.PATH.getPoint(pointTime);
                if (point == null) break;
                pos = point.getPathBezier().getRight();
            }
            case NONE -> {
                CameraKeyframe point = CameraAnimIdeCache.PATH.getPoint(pointTime);
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
