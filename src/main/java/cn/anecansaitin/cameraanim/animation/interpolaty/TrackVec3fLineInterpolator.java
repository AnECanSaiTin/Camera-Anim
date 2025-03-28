package cn.anecansaitin.cameraanim.animation.interpolaty;

import cn.anecansaitin.cameraanim.animation.ITrack;
import cn.anecansaitin.cameraanim.animation.interpolaty.parameter.IParameterGetter;
import cn.anecansaitin.cameraanim.animation.interpolaty.parameter.TrackVec3fLineGetter;
import cn.anecansaitin.cameraanim.animation.util.InterpolationMath;
import org.joml.Vector3f;

public class TrackVec3fLineInterpolator implements IInterpolator<ITrack<Vector3f>, Vector3f[], Vector3f> {
    public static final TrackVec3fLineInterpolator INSTANCE = new TrackVec3fLineInterpolator();

    private TrackVec3fLineInterpolator() {
    }

    @Override
    public Vector3f interpolated(float t, Vector3f dest, Vector3f[] parameters) {
        return InterpolationMath.line(t, parameters[0], parameters[1], dest);
    }

    @Override
    public IParameterGetter<ITrack<Vector3f>, Vector3f[]> getParameterGetter() {
        return TrackVec3fLineGetter.INSTANCE;
    }
}
