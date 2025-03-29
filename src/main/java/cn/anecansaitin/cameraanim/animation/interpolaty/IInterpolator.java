package cn.anecansaitin.cameraanim.animation.interpolaty;

import cn.anecansaitin.cameraanim.animation.interpolaty.parameter.IParameterGetter;

public interface IInterpolator<PARENT, PARAMETER, RESULT> {
    default RESULT interpolated(int time, float t, PARENT parent, RESULT dest) {
        return interpolated(t, dest, getParameterGetter().getParameters(time, parent));
    }

    RESULT interpolated(float t, RESULT dest, PARAMETER parameter);

    IParameterGetter<PARENT, PARAMETER> getParameterGetter();
}
