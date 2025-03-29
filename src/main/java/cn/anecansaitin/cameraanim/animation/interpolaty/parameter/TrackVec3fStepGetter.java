package cn.anecansaitin.cameraanim.animation.interpolaty.parameter;

import cn.anecansaitin.cameraanim.animation.IKeyframe;
import cn.anecansaitin.cameraanim.animation.ITimeSlice;
import org.joml.Vector3f;

public class TrackVec3fStepGetter implements IParameterGetter<ITimeSlice<Vector3f>, Vector3f[]> {
    public static final TrackVec3fStepGetter INSTANCE = new TrackVec3fStepGetter();

    private TrackVec3fStepGetter() {
    }

    @Override
    public Vector3f[] getParameters(int time, ITimeSlice<Vector3f> track) {
        Vector3f[] result = new Vector3f[1];
        IKeyframe<Vector3f> current = track.getKeyframe(time);

        if (current == null) {
            return result;
        }

        result[0] = current.getValue();
        return result;
    }
}
