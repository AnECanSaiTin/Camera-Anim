package cn.anecansaitin.cameraanim.animation;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

/// 时间片段
/// 其中保存的关键帧时间为相对于时间片段的时间
/// 如时间片段时间为100-200，关键帧时间为50，则关键帧的实际时间为150
public interface ITimeSlice<T> {
    boolean interpolated(int time, float t, T dest);

    @Nullable IKeyframe<T> getKeyframe(int time);

    @Nullable Map.Entry<Integer, IKeyframe<T>> getPrevKeyframe(int time);

    @Nullable Map.Entry<Integer, IKeyframe<T>> getNextKeyframe(int time);

    void putKeyframe(int time, IKeyframe<T> keyframe);

    boolean removeKeyframe(int time);

    boolean moveKeyframe(int time, int newTime);

    void clear();
}
