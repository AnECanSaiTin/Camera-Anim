package cn.anecansaitin.cameraanim.animation.interpolaty;

import cn.anecansaitin.cameraanim.animation.ITrack;
import cn.anecansaitin.cameraanim.animation.interpolaty.parameter.IParameterGetter;
import cn.anecansaitin.cameraanim.animation.interpolaty.parameter.TrackVec3fCatmullRomGetter;
import cn.anecansaitin.cameraanim.animation.util.InterpolationMath;
import org.joml.Vector3f;

public class TrackVec3fCatmullRomInterpolator implements IInterpolator<ITrack<Vector3f>, Vector3f[], Vector3f> {
    public static final TrackVec3fCatmullRomInterpolator INSTANCE = new TrackVec3fCatmullRomInterpolator();

    private TrackVec3fCatmullRomInterpolator() {
    }

    @Override
    public Vector3f interpolated(float t, Vector3f dest, Vector3f[] parameters) {
        return InterpolationMath.catmullRom(t, parameters[0], parameters[1], parameters[2], parameters[3], dest);
    }

    @Override
    public IParameterGetter<ITrack<Vector3f>, Vector3f[]> getParameterGetter() {
        return TrackVec3fCatmullRomGetter.INSTANCE;
    }
}
