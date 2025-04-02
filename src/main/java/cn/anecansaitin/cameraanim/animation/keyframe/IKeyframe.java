package cn.anecansaitin.cameraanim.animation.keyframe;

import cn.anecansaitin.cameraanim.animation.interpolaty.IInterpolator;

public interface IKeyframe<V, P> {
    boolean getInterpolatedValue(int time, float t, P parent, V dest);

    V getValue();

    void setValue(V value);

    IInterpolator<P, V[], V> getInterpolator();

    void setInterpolator(IInterpolator<P, V[], V> interpolator);
}
