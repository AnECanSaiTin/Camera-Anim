package cn.anecansaitin.cameraanim.animation;

import cn.anecansaitin.cameraanim.animation.interpolaty.IInterpolator;

public interface IKeyframe<V> {
    V getInterpolatedValue(int time, float t, ITimeSlice<V> parent, V dest);

    V getValue();

    void setValue(V value);

    IInterpolator<ITimeSlice<V>, V[], V> getInterpolator();

    void setInterpolator(IInterpolator<ITimeSlice<V>, V[], V> interpolator);
}
