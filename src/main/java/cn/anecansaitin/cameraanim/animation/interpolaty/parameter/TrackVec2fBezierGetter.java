package cn.anecansaitin.cameraanim.animation.interpolaty.parameter;

import cn.anecansaitin.cameraanim.animation.IKeyframe;
import cn.anecansaitin.cameraanim.animation.ITrack;
import org.joml.Vector2f;

import java.util.Map;

public class TrackVec2fBezierGetter implements IParameterGetter<ITrack<Vector2f>, Vector2f[]>{
    public static final TrackVec2fBezierGetter INSTANCE = new TrackVec2fBezierGetter();

    private TrackVec2fBezierGetter() {
    }

    @Override
    public Vector2f[] getParameters(int time, ITrack<Vector2f> track) {
        Vector2f[] result = new Vector2f[3];
        IKeyframe<Vector2f> current = track.getKeyframe(time);

        if (current == null) {
            return result;
        }

        result[0] = current.getValue();
        Map.Entry<Integer, IKeyframe<Vector2f>> nextKeyframe = track.getNextKeyframe(time);

        if (nextKeyframe == null) {
            return result;
        }

        result[1] = nextKeyframe.getValue().getValue();
        return result;
    }
}
