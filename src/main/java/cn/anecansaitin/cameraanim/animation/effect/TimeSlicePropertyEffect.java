package cn.anecansaitin.cameraanim.animation.effect;

import cn.anecansaitin.cameraanim.animation.effect.type.IEffectType;
import cn.anecansaitin.cameraanim.animation.slice.ITimeSlice;
import cn.anecansaitin.freecameraapi.ICameraModifier;

public class TimeSlicePropertyEffect<T> extends AbstractPropertyEffect<T> {
    private final ITimeSlice<T> timeSlice;
    private final IEffectApplier<ITimeSlice<T>> onApply;

    public TimeSlicePropertyEffect(IEffectType<T> type, ITimeSlice<T> timeSlice, IEffectApplier<ITimeSlice<T>> onApply) {
        super(type);
        this.timeSlice = timeSlice;
        this.onApply = onApply;
    }

    @Override
    public void apply(int time, float t, ICameraModifier camera) {
        onApply.apply(time, t, camera, timeSlice);
    }
}
