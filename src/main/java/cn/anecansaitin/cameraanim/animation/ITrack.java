package cn.anecansaitin.cameraanim.animation;

import cn.anecansaitin.cameraanim.util.IntIntObjectMutTriple;
import org.jetbrains.annotations.Nullable;

public interface ITrack<T> {
    T interpolated(int time, float t, T dest);

    @Nullable IntIntObjectMutTriple<ITimeSlice<T>> getTimeSlice(int time);

    boolean putTimeSlice(int start, int end, ITimeSlice<T> timeSlice);

    boolean removeTimeSlice(int start);

    boolean moveTimeSlice(int start, int newStart);
}
