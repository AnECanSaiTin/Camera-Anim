package cn.anecansaitin.cameraanim.animation.effect;

import cn.anecansaitin.cameraanim.animation.Animation;
import cn.anecansaitin.cameraanim.animation.track.ITrackManager;
import cn.anecansaitin.cameraanim.animation.track.TrackManager;

public class TrackEffect<T> extends AbstractEffect<T> {
    private final ITrackManager<T> trackManager;
    private final IEffectApplier<ITrackManager<T>> onApply;

    public TrackEffect(Class<T> type, ITrackManager<T> trackManager, IEffectApplier<ITrackManager<T>> onApply) {
        super(type);
        this.trackManager = trackManager;
        this.onApply = onApply;
    }

    @Override
    public void apply(int time, float t, Animation animation) {
        onApply.apply(time, t, animation, trackManager);
    }
}
