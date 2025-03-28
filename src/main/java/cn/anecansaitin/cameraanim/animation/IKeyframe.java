package cn.anecansaitin.cameraanim.animation;

import cn.anecansaitin.cameraanim.animation.interpolaty.IInterpolator;

public interface IKeyframe<T> {
    T getInterpolatedValue(int time, float t, Track<T> track, T dest);

    T getValue();

    void setValue(T value);

    IInterpolator<Track<T>, T[], T> getInterpolator();

    void setInterpolator(IInterpolator<Track<T>, T[], T> interpolator);
}
