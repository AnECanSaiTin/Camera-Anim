package cn.anecansaitin.cameraanim.animation.interpolaty;

import cn.anecansaitin.cameraanim.animation.slice.ITimeSlice;
import cn.anecansaitin.cameraanim.animation.interpolaty.parameter.IParameterGetter;
import cn.anecansaitin.cameraanim.animation.interpolaty.parameter.TrackVec3fStepGetter;
import org.joml.Vector3f;

public class TrackVec3fStepInterpolator implements IInterpolator<ITimeSlice<Vector3f>, Vector3f[], Vector3f> {
    public static final TrackVec3fStepInterpolator INSTANCE = new TrackVec3fStepInterpolator();

    private TrackVec3fStepInterpolator() {
    }

    @Override
    public boolean interpolated(float t, Vector3f dest, Vector3f[] parameters) {
        dest.set(parameters[0]);
        return true;
    }

    @Override
    public IParameterGetter<ITimeSlice<Vector3f>, Vector3f[]> getParameterGetter() {
        return TrackVec3fStepGetter.INSTANCE;
    }
}
