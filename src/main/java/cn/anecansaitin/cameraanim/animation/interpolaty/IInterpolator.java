package cn.anecansaitin.cameraanim.animation.interpolaty;

import cn.anecansaitin.cameraanim.animation.interpolaty.parameter.IParameterGetter;

public interface IInterpolator<Data, P, R> {
    R interpolated(float t, R dest, P parameter);
    IParameterGetter<Data, P> getParameterGetter();
}
