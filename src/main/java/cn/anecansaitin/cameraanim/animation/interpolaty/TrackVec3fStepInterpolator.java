package cn.anecansaitin.cameraanim.animation.interpolaty;

import cn.anecansaitin.cameraanim.animation.ITimeSlice;
import cn.anecansaitin.cameraanim.animation.interpolaty.parameter.IParameterGetter;
import cn.anecansaitin.cameraanim.animation.interpolaty.parameter.TrackVec3fStepGetter;
import org.joml.Vector3f;

public class TrackVec3fStepInterpolator implements IInterpolator<ITimeSlice<Vector3f>, Vector3f[], Vector3f> {
    public static final TrackVec3fStepInterpolator INSTANCE = new TrackVec3fStepInterpolator();

    private TrackVec3fStepInterpolator() {
    }

    @Override
    public Vector3f interpolated(float t, Vector3f dest, Vector3f[] parameters) {
        return dest.set(parameters[0]);
    }

    @Override
    public IParameterGetter<ITimeSlice<Vector3f>, Vector3f[]> getParameterGetter() {
        return TrackVec3fStepGetter.INSTANCE;
    }
}
