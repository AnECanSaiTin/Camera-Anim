package cn.anecansaitin.cameraanim.animation;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;

public class TimeSlice<T> implements ITimeSlice<T> {
    /// 保存相对于片段的时间与关键帧
    private final TreeMap<Integer, IKeyframe<T>> keyframes;

    public TimeSlice() {
        keyframes = new TreeMap<>();
    }

    /// 获取指定时间插值后的值
    /// @param time 相对帧时间
    /// @param t 插值时间
    /// @param dest 存储结果的对象
    /// @return 插值后的值，若为null则表示没有关键帧，无法插值
    @Override
    public boolean interpolated(int time, float t, T dest) {
        var keyframe = getKeyframe(time);

        if (keyframe == null) {
            var prevKeyframe = getPrevKeyframe(time);

            if (prevKeyframe == null) {
                var next = getNextKeyframe(time);

                if (next == null) {
                    return false;
                }

                keyframe = next.getValue();
            } else {
                keyframe = prevKeyframe.getValue();
            }
        }

        return keyframe.getInterpolatedValue(time, t, this, dest);
    }

    @Override
    public @Nullable IKeyframe<T> getKeyframe(int time) {
        return keyframes.get(time);
    }

    @Override
    public @Nullable Map.Entry<Integer, IKeyframe<T>> getPrevKeyframe(int time) {
        return keyframes.floorEntry(time);
    }

    @Override
    public @Nullable Map.Entry<Integer, IKeyframe<T>> getNextKeyframe(int time) {
        return keyframes.ceilingEntry(time);
    }

    @Override
    public void putKeyframe(int time, IKeyframe<T> keyframe) {
        keyframes.put(time, keyframe);
    }

    @Override
    public boolean removeKeyframe(int time) {
        return keyframes.remove(time) != null;
    }

    @Override
    public boolean moveKeyframe(int time, int newTime) {
        if (keyframes.containsKey(newTime)) {
            return false;
        }

        if (keyframes.containsKey(time)) {
            var keyframe = keyframes.remove(time);
            keyframes.put(newTime, keyframe);
            return true;
        }

        return false;
    }

    @Override
    public void clear() {
        keyframes.clear();
    }
}
