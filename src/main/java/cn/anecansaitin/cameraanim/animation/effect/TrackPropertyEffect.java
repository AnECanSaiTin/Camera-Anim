package cn.anecansaitin.cameraanim.animation.effect;

import cn.anecansaitin.cameraanim.animation.effect.type.IEffectType;
import cn.anecansaitin.cameraanim.animation.track.ITrackManager;
import cn.anecansaitin.freecameraapi.ICameraModifier;

public class TrackPropertyEffect<T> extends AbstractPropertyEffect<T> {
    private final ITrackManager<T> trackManager;
    private final IEffectApplier<ITrackManager<T>> onApply;

    public TrackPropertyEffect(IEffectType<T> type, ITrackManager<T> trackManager, IEffectApplier<ITrackManager<T>> onApply) {
        super(type);
        this.trackManager = trackManager;
        this.onApply = onApply;
    }

    @Override
    public void apply(int time, float t, ICameraModifier camera) {
        onApply.apply(time, t, camera, trackManager);
    }

    public ITrackManager<T> getTrackManager() {
        return trackManager;
    }
}
