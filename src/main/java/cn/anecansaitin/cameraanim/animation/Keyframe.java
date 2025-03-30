package cn.anecansaitin.cameraanim.animation;

import cn.anecansaitin.cameraanim.animation.interpolaty.IInterpolator;

public class Keyframe<V> implements IKeyframe<V> {
    private V value;
    private IInterpolator<ITimeSlice<V>, V[], V> interpolator;

    public Keyframe(V value, IInterpolator<ITimeSlice<V>, V[], V> interpolator) {
        this.value = value;
        this.interpolator = interpolator;
    }

    @Override
    public boolean getInterpolatedValue(int time, float t, ITimeSlice<V> timeSlice, V dest) {
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
    public IInterpolator<ITimeSlice<V>, V[], V> getInterpolator() {
        return interpolator;
    }

    @Override
    public void setInterpolator(IInterpolator<ITimeSlice<V>, V[], V> interpolator) {
        this.interpolator = interpolator;
    }
}
