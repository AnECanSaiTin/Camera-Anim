package cn.anecansaitin.cameraanim.animation.effect;

import cn.anecansaitin.cameraanim.animation.Animation;
import cn.anecansaitin.cameraanim.animation.slice.ITimeSlice;

public class TimeSliceEffect<T> extends AbstractEffect<T> {
    private final ITimeSlice<T> timeSlice;
    private final IEffectApplier<ITimeSlice<T>> onApply;

    public TimeSliceEffect(Class<T> type, ITimeSlice<T> timeSlice, IEffectApplier<ITimeSlice<T>> onApply) {
        super(type);
        this.timeSlice = timeSlice;
        this.onApply = onApply;
    }

    @Override
    public void apply(int time, float t, Animation animation) {
        onApply.apply(time, t, animation, timeSlice);
    }
}
