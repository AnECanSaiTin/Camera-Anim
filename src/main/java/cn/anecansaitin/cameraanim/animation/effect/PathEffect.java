package cn.anecansaitin.cameraanim.animation.effect;

import cn.anecansaitin.cameraanim.animation.effect.type.EffectTypes;
import cn.anecansaitin.cameraanim.animation.track.ITrackManager;
import cn.anecansaitin.cameraanim.animation.track.TrackManager;
import cn.anecansaitin.freecameraapi.ICameraModifier;
import org.joml.Vector3f;

public class PathEffect extends TrackPropertyEffect<Vector3f> {
    public PathEffect() {
        super(EffectTypes.PATH, new TrackManager<>(), new IEffectApplier<>() {
            private final Vector3f dest = new Vector3f();

            @Override
            public void apply(int time, float t, ICameraModifier camera, ITrackManager<Vector3f> data) {
                boolean hasResult = data.interpolated(time, t, dest);

                if (hasResult) {
                    camera.setPos(dest.x, dest.y, dest.z);
                }
            }
        });
    }
}
