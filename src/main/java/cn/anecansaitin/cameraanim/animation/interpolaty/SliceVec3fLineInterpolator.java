package cn.anecansaitin.cameraanim.animation.interpolaty;

import cn.anecansaitin.cameraanim.animation.interpolaty.parameter.IParameterGetter;
import cn.anecansaitin.cameraanim.animation.interpolaty.parameter.SliceVec3fPrevNextGetter;
import cn.anecansaitin.cameraanim.animation.slice.ITimeSlice;
import cn.anecansaitin.cameraanim.util.InterpolationMath;
import org.joml.Vector3f;

public class SliceVec3fLineInterpolator implements IInterpolator<ITimeSlice<Vector3f>, Vector3f[], Vector3f> {
    @Override
    public boolean interpolated(float t, Vector3f dest, Vector3f[] vector3fs) {
        if (vector3fs.length < 2) {
            return false;
        }

        Vector3f pre = vector3fs[0];
        Vector3f next = vector3fs[1];

        if (pre == null) {
            if (next == null) {
                return false;
            } else {
                dest.set(next);
            }
        } else {
            if (next == null) {
                dest.set(pre);
            } else {
                InterpolationMath.line(t, pre, next, dest);
            }
        }

        return true;
    }

    @Override
    public IParameterGetter<ITimeSlice<Vector3f>, Vector3f[]> getParameterGetter() {
        return new SliceVec3fPrevNextGetter();
    }
}
