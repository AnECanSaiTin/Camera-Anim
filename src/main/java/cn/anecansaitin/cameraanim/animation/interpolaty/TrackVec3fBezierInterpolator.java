package cn.anecansaitin.cameraanim.animation.interpolaty;

import cn.anecansaitin.cameraanim.animation.ITrack;
import cn.anecansaitin.cameraanim.animation.interpolaty.parameter.IParameterGetter;
import cn.anecansaitin.cameraanim.animation.interpolaty.parameter.TrackVec3fBezierGetter;
import cn.anecansaitin.cameraanim.animation.util.InterpolationMath;
import org.joml.Vector3f;

public class TrackVec3fBezierInterpolator implements IInterpolator<ITrack<Vector3f>, Vector3f[], Vector3f> {
    private final Vector3f controlPoint1;
    private final Vector3f controlPoint2;

    public TrackVec3fBezierInterpolator(Vector3f controlPoint1, Vector3f controlPoint2) {
        this.controlPoint1 = controlPoint1;
        this.controlPoint2 = controlPoint2;
    }

    @Override
    public Vector3f interpolated(float t, Vector3f dest, Vector3f[] parameters) {
        return InterpolationMath.bezier(t, parameters[0], controlPoint1, controlPoint2, parameters[1], dest);
    }

    @Override
    public IParameterGetter<ITrack<Vector3f>, Vector3f[]> getParameterGetter() {
        return TrackVec3fBezierGetter.INSTANCE;
    }

    public void setControlPoint1(Vector3f controlPoint1) {
        this.controlPoint1.set(controlPoint1);
    }

    public void setControlPoint2(Vector3f controlPoint2) {
        this.controlPoint2.set(controlPoint2);
    }

    public Vector3f getControlPoint1() {
        return controlPoint1;
    }

    public Vector3f getControlPoint2() {
        return controlPoint2;
    }
}
