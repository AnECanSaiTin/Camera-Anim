package cn.anecansaitin.cameraanim.animation.interpolaty.parameter;

import cn.anecansaitin.cameraanim.animation.IVersion;
import cn.anecansaitin.cameraanim.animation.slice.ITimeSlice;
import org.joml.Vector3f;

public class SliceVec3fPrevNextGetter implements IParameterGetter<ITimeSlice<Vector3f>, Vector3f[]> {
    private int sliceVersion = -1;
    private final Vector3f[] result = new Vector3f[2];

    @Override
    public Vector3f[] getParameters(int time, ITimeSlice<Vector3f> slice) {
        if (slice instanceof IVersion version) {
            if (version.version() == sliceVersion) {
                return result;
            } else {
                sliceVersion = version.version();
            }
        }

        var current = slice.getKeyframe(time);
        var next = slice.getNextKeyframe(time);
        var prev = slice.getPrevKeyframe(time);

        if (current == null) {
            if (prev != null) {
                result[0] = prev.getValue().getValue();
            }

            if (next != null) {
                result[1] = next.getValue().getValue();
            }
        } else {
            result[0] = current.getValue();

            if (next != null) {
                result[1] = next.getValue().getValue();
            }
        }

        return result;
    }
}
