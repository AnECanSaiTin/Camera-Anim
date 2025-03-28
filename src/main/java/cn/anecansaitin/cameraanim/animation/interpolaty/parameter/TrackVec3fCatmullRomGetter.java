package cn.anecansaitin.cameraanim.animation.interpolaty.parameter;

import cn.anecansaitin.cameraanim.animation.IKeyframe;
import cn.anecansaitin.cameraanim.animation.ITrack;
import org.joml.Vector3f;

import java.util.Map;

public class TrackVec3fCatmullRomGetter implements IParameterGetter<ITrack<Vector3f>, Vector3f[]>{
    public static final TrackVec3fCatmullRomGetter INSTANCE = new TrackVec3fCatmullRomGetter();

    private TrackVec3fCatmullRomGetter() {
    }

    /// 返回参数数组
    /// result[0] = k0，若为null，则取k1
    /// result[1] = k1，当前帧
    /// result[2] = k2
    /// result[3] = k3，若为null，则取k2
    @Override
    public Vector3f[] getParameters(int time, ITrack<Vector3f> track) {
        Vector3f[] result = new Vector3f[4];
        IKeyframe<Vector3f> k1 = track.getKeyframe(time);

        if (k1 == null) {
            return result;
        }

        result[1] = k1.getValue();
        Map.Entry<Integer, IKeyframe<Vector3f>> k2 = track.getNextKeyframe(time);

        if (k2 == null) {
            return result;
        }

        result[2] = k2.getValue().getValue();
        Map.Entry<Integer, IKeyframe<Vector3f>> k0 = track.getPrevKeyframe(time);
        Map.Entry<Integer, IKeyframe<Vector3f>> k3 = track.getNextKeyframe(k2.getKey());

        if (k0 == null) {
            result[0] = k1.getValue();
        } else {
            result[0] = k0.getValue().getValue();
        }

        if (k3 == null) {
            result[3] = result[2];
        } else {
            result[3] = k3.getValue().getValue();
        }

        return result;
    }
}
