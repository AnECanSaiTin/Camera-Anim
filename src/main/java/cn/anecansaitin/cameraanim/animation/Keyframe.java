package cn.anecansaitin.cameraanim.animation;

import cn.anecansaitin.cameraanim.animation.interpolaty.IInterpolator;

public class Keyframe<T> implements IKeyframe<T> {
    private T value;
    private IInterpolator<Track<T>, T[], T> interpolator;

    public Keyframe(T value, IInterpolator<Track<T>, T[], T> interpolator) {
        this.value = value;
        this.interpolator = interpolator;
    }

    @Override
    public T getInterpolatedValue(int time, float t, Track<T> track, T dest) {
        return interpolator.interpolated(t, dest, interpolator.getParameterGetter().getParameters(time, track));
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public IInterpolator<Track<T>, T[], T> getInterpolator() {
        return interpolator;
    }

    @Override
    public void setInterpolator(IInterpolator<Track<T>, T[], T> interpolator) {
        this.interpolator = interpolator;
    }
}
