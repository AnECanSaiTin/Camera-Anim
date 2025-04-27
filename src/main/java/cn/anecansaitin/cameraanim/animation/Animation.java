package cn.anecansaitin.cameraanim.animation;

import cn.anecansaitin.cameraanim.animation.effect.PathEffect;
import cn.anecansaitin.cameraanim.animation.effect.type.IEffectType;
import cn.anecansaitin.cameraanim.animation.effect.IPropertyEffect;
import cn.anecansaitin.cameraanim.animation.interpolaty.SliceVec3fLineInterpolator;
import cn.anecansaitin.cameraanim.animation.keyframe.Keyframe;
import cn.anecansaitin.cameraanim.animation.slice.TimeSlice;
import cn.anecansaitin.cameraanim.animation.track.ITrackManager;
import cn.anecansaitin.cameraanim.animation.track.Track;
import cn.anecansaitin.freecameraapi.CameraModifierManager;
import cn.anecansaitin.freecameraapi.ICameraModifier;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;

public class Animation {
    private final ArrayList<Pair<String, IPropertyEffect<?>>> effects;
    private final HashMap<String, IPropertyEffect<?>> effectMap;
    private final ICameraModifier camera;
    private final PathEffect path;

    public Animation() {
        effects = new ArrayList<>();
        effectMap = new HashMap<>();
        camera = CameraModifierManager.createModifier("test", false);
        path = new PathEffect();

        // test
        ITrackManager<Vector3f> trackManager = path.getTrackManager();
        Track<Vector3f> track = new Track<>();
        trackManager.putTrack("test track", track);
        TimeSlice<Vector3f> slice = new TimeSlice<>();
        track.putTimeSlice(0, 20 * 10, slice);
        slice.putKeyframe(20 * 0, new Keyframe<>(new Vector3f(0, 0, 0), new SliceVec3fLineInterpolator()));
        slice.putKeyframe(20 * 1, new Keyframe<>(new Vector3f(0, 0, 0), new SliceVec3fLineInterpolator()));
        slice.putKeyframe(20 * 2, new Keyframe<>(new Vector3f(0, 0, 0), new SliceVec3fLineInterpolator()));
        slice.putKeyframe(20 * 3, new Keyframe<>(new Vector3f(0, 0, 0), new SliceVec3fLineInterpolator()));
        slice.putKeyframe(20 * 4, new Keyframe<>(new Vector3f(0, 0, 0), new SliceVec3fLineInterpolator()));
        slice.putKeyframe(20 * 5, new Keyframe<>(new Vector3f(0, 0, 0), new SliceVec3fLineInterpolator()));
        slice.putKeyframe(20 * 6, new Keyframe<>(new Vector3f(0, 0, 0), new SliceVec3fLineInterpolator()));
        slice.putKeyframe(20 * 7, new Keyframe<>(new Vector3f(0, 0, 0), new SliceVec3fLineInterpolator()));
        slice.putKeyframe(20 * 8, new Keyframe<>(new Vector3f(0, 0, 0), new SliceVec3fLineInterpolator()));
        slice.putKeyframe(20 * 9, new Keyframe<>(new Vector3f(0, 0, 0), new SliceVec3fLineInterpolator()));
        slice.putKeyframe(20 * 10, new Keyframe<>(new Vector3f(0, 0, 0), new SliceVec3fLineInterpolator()));
    }

    public void animationTick(int time, float t) {
        path.apply(time, t, camera);

        for (Pair<String, IPropertyEffect<?>> effect : effects) {
            effect.right().apply(time, t, camera);
        }
    }

    @Nullable
    public <T> IPropertyEffect<T> getEffect(int index, IEffectType<T> type) {
        var effect = effects.get(index);

        if (effect.right().getType() != type) {
            return null;
        }

        return (IPropertyEffect<T>) effect.right();
    }

    public <T> IPropertyEffect<T> getEffect(String id, IEffectType<T> type) {
        var effect = effectMap.get(id);

        if (effect.getType() != type) {
            return null;
        }

        return (IPropertyEffect<T>) effect;
    }

    public <T> boolean addEffect(int index, String id, IPropertyEffect<T> effect) {
        if (effectMap.containsKey(id)) {
            return false;
        }

        if (index < 0) {
            return false;
        }

        if (index >= effects.size()) {
            effects.add(Pair.of(id, effect));
            effectMap.put(id, effect);
            return true;
        }

        effects.add(index, Pair.of(id, effect));
        effectMap.put(id, effect);
        return true;
    }

    public <T> boolean addEffect(String id, IPropertyEffect<T> effect) {
        return addEffect(effects.size(), id, effect);
    }

    public <T> boolean removeEffect(int index) {
        if (index < 0 || index >= effects.size()) {
            return false;
        }

        var effect = effects.get(index);
        effectMap.remove(effect.left());
        effects.remove(index);
        return true;
    }

    public <T> boolean removeEffect(String id) {
        var effect = effectMap.get(id);

        if (effect == null) {
            return false;
        }

        int index = effects.indexOf(Pair.of(id, effect));

        if (index < 0) {
            return false;
        }

        effects.remove(index);
        effectMap.remove(id);
        return true;
    }

    public <T> boolean renameEffect(int index, String id) {
        if (index < 0 || index >= effects.size()) {
            return false;
        }

        var effect = effects.get(index);
        effectMap.remove(effect.left());
        effectMap.put(id, effect.right());
        effects.set(index, Pair.of(id, effect.right()));
        return true;
    }

    public <T> boolean renameEffect(String oldId, String newId) {
        var effect = effectMap.get(oldId);

        if (effect == null) {
            return false;
        }

        int index = effects.indexOf(Pair.of(oldId, effect));

        if (index < 0) {
            return false;
        }

        effectMap.remove(oldId);
        effectMap.put(newId, effect);
        effects.set(index, Pair.of(newId, effect));
        return true;
    }

    public <T> boolean swapEffect(int index, int newIndex) {
        if (index < 0 || index >= effects.size() || newIndex < 0 || newIndex >= effects.size() || index == newIndex) {
            return false;
        }

        effects.set(newIndex, effects.set(index, effects.get(newIndex)));
        return true;
    }
}
