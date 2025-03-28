package cn.anecansaitin.cameraanim.animation.interpolaty;

import cn.anecansaitin.cameraanim.animation.ITrack;
import cn.anecansaitin.cameraanim.animation.interpolaty.parameter.IParameterGetter;
import cn.anecansaitin.cameraanim.animation.interpolaty.parameter.TrackVec2fBezierGetter;
import cn.anecansaitin.cameraanim.animation.util.InterpolationMath;
import org.joml.Vector2f;

public class TrackVec2fBezierInterpolator implements IInterpolator<ITrack<Vector2f>, Vector2f[], Vector2f> {
    private final Vector2f controlPoint1;
    private final Vector2f controlPoint2;

    public TrackVec2fBezierInterpolator(Vector2f controlPoint1, Vector2f controlPoint2) {
        this.controlPoint1 = controlPoint1;
        this.controlPoint2 = controlPoint2;
    }

    @Override
    public Vector2f interpolated(float t, Vector2f dest, Vector2f[] parameter) {
        if (parameter[0] == null || parameter[1] == null) {
            return dest;
        }

        return InterpolationMath.bezier(t, parameter[0], controlPoint1, controlPoint2, parameter[1], dest);
    }

    @Override
    public IParameterGetter<ITrack<Vector2f>, Vector2f[]> getParameterGetter() {
        return TrackVec2fBezierGetter.INSTANCE;
    }

    public void setControlPoint1(Vector2f controlPoint1) {
        this.controlPoint1.set(controlPoint1);
    }

    public void setControlPoint2(Vector2f controlPoint2) {
        this.controlPoint2.set(controlPoint2);
    }

    public Vector2f getControlPoint1() {
        return controlPoint1;
    }

    public Vector2f getControlPoint2() {
        return controlPoint2;
    }
}
