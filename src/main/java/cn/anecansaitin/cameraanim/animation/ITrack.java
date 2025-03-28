package cn.anecansaitin.cameraanim.animation;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ITrack<T> {
    @Nullable T getInterpolatedValue(int time, float t, T dest);

    @Nullable IKeyframe<T> getKeyframe(int time);

    @Nullable Map.Entry<Integer, IKeyframe<T>> getPrevKeyframe(int time);

    @Nullable Map.Entry<Integer, IKeyframe<T>> getNextKeyframe(int time);

    void putKeyframe(int time, IKeyframe<T> keyframe);

    boolean removeKeyframe(int time);

    boolean moveKeyframe(int time, int newTime);

    void clear();
}
