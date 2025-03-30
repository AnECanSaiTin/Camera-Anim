package cn.anecansaitin.cameraanim.animation;

import cn.anecansaitin.cameraanim.util.IntIntObjectMutTriple;
import org.jetbrains.annotations.Nullable;

import java.util.TreeMap;

public class Track<T> implements ITrack<T> {
    /// first 起始时间，second 结束时间，third 时间片
    private final TreeMap<Integer, IntIntObjectMutTriple<ITimeSlice<T>>> timeSlices;

    public Track() {
        timeSlices = new TreeMap<>();
    }

    @Override
    public boolean interpolated(int time, float t, T dest) {
        var timeSlice = getTimeSlice(time);

        if (timeSlice == null) {
            return false;
        }

        return timeSlice.third().interpolated(time - timeSlice.first(), t, dest);
    }

    /// 获取指定时间所对应的时间片
    @Override
    public @Nullable IntIntObjectMutTriple<ITimeSlice<T>> getTimeSlice(int time) {
        var current = timeSlices.get(time);

        if (current != null) {
            return current;
        }

        var pre = timeSlices.lowerEntry(time);

        if (pre != null) {
            var prePair = pre.getValue();
            int end = prePair.second();

            if (time > end) {
                return null;
            }

            return prePair;
        }

        return null;
    }

    @Override
    public boolean putTimeSlice(int start, int end, ITimeSlice<T> timeSlice) {
        if (start > end) {
            return false;
        }

        var startSlice = getTimeSlice(start);

        if (startSlice != null && startSlice.second() > start) {
            return false;
        }

        var endSlice = getTimeSlice(end);

        if (endSlice != null && endSlice.first() < end) {
            return false;
        }

        timeSlices.put(start, new IntIntObjectMutTriple<>(start, end, timeSlice));
        return true;
    }

    @Override
    public boolean removeTimeSlice(int start) {
        return timeSlices.remove(start) != null;
    }

    @Override
    public boolean moveTimeSlice(int start, int newStart) {
        var timeSliceTriple = timeSlices.get(start);

        if (timeSliceTriple == null) {
            return false;
        }

        var startSlice = getTimeSlice(newStart);

        if (startSlice != null && startSlice.second() > newStart) {
            return false;
        }

        var endSlice = getTimeSlice(timeSliceTriple.second() + newStart);

        if (endSlice != null && endSlice.first() < newStart) {
            return false;
        }

        timeSliceTriple.first(newStart);
        timeSliceTriple.second(timeSliceTriple.second() + newStart);
        timeSlices.remove(start);
        timeSlices.put(newStart, timeSliceTriple);
        return true;
    }
}
