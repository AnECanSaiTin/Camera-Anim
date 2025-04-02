package cn.anecansaitin.cameraanim.animation.keyframe;

import cn.anecansaitin.cameraanim.animation.interpolaty.IInterpolator;

public class Keyframe<V, P> implements IKeyframe<V, P> {
    private V value;
    private IInterpolator<P, V[], V> interpolator;

    public Keyframe(V value, IInterpolator<P, V[], V> interpolator) {
        this.value = value;
        this.interpolator = interpolator;
    }

    @Override
    public boolean getInterpolatedValue(int time, float t, P timeSlice, V dest) {
        return interpolator.interpolated(time, t, timeSlice, dest);
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public IInterpolator<P, V[], V> getInterpolator() {
        return interpolator;
    }

    @Override
    public void setInterpolator(IInterpolator<P, V[], V> interpolator) {
        this.interpolator = interpolator;
    }
}
