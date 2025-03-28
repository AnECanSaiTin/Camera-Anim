package cn.anecansaitin.cameraanim.animation.interpolaty.parameter;

import cn.anecansaitin.cameraanim.animation.IKeyframe;
import cn.anecansaitin.cameraanim.animation.ITrack;
import org.joml.Vector3f;

import java.util.Map;

public class TrackVec3fLineGetter implements IParameterGetter<ITrack<Vector3f>, Vector3f[]>{
    public static final TrackVec3fLineGetter INSTANCE = new TrackVec3fLineGetter();

    private TrackVec3fLineGetter() {
    }

    @Override
    public Vector3f[] getParameters(int time, ITrack<Vector3f> track) {
        Vector3f[] result = new Vector3f[2];
        IKeyframe<Vector3f> k1 = track.getKeyframe(time);

        if (k1 == null) {
            return result;
        }

        result[0] = k1.getValue();
        Map.Entry<Integer, IKeyframe<Vector3f>> k2 = track.getNextKeyframe(time);

        if (k2 == null) {
            return result;
        }

        result[1] = k2.getValue().getValue();
        return result;
    }
}
