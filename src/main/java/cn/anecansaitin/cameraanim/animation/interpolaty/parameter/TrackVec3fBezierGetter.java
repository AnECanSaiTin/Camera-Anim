package cn.anecansaitin.cameraanim.animation.interpolaty.parameter;

import cn.anecansaitin.cameraanim.animation.IKeyframe;
import cn.anecansaitin.cameraanim.animation.ITrack;
import org.joml.Vector3f;

import java.util.Map;

public class TrackVec3fBezierGetter implements IParameterGetter<ITrack<Vector3f>, Vector3f[]> {
    public static final TrackVec3fBezierGetter INSTANCE = new TrackVec3fBezierGetter();

    private TrackVec3fBezierGetter() {
    }

    @Override
    public Vector3f[] getParameters(int time, ITrack<Vector3f> track) {
        Vector3f[] result = new Vector3f[3];
        IKeyframe<Vector3f> current = track.getKeyframe(time);

        if (current == null) {
            return result;
        }

        result[0] = current.getValue();
        Map.Entry<Integer, IKeyframe<Vector3f>> nextKeyframe = track.getNextKeyframe(time);

        if (nextKeyframe == null) {
            return result;
        }

        result[1] = nextKeyframe.getValue().getValue();
        return result;
    }
}
