package cn.anecansaitin.cameraanim.animation;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;

public class Track<T> implements ITrack<T> {
    private final TreeMap<Integer, IKeyframe<T>> keyframes;

    public Track() {
        this.keyframes = new TreeMap<>();
    }

    @Override
    @Nullable
    public T getInterpolatedValue(int time, float t, T dest) {
        IKeyframe<T> keyframe = getKeyframe(time);

        if (keyframe == null) {
            Map.Entry<Integer, IKeyframe<T>> prevKeyframe = getPrevKeyframe(time);

            if (prevKeyframe == null) {
                Map.Entry<Integer, IKeyframe<T>> next = getNextKeyframe(time);

                if (next == null) {
                    return null;
                }

                return next.getValue().getValue();
            } else {
                keyframe = prevKeyframe.getValue();
            }
        }

        if (t == 0) {
            return keyframe.getValue();
        }

        return keyframe.getInterpolatedValue(time, t, this, dest);
    }

    @Override
    @Nullable
    public IKeyframe<T> getKeyframe(int time) {
        return keyframes.get(time);
    }

    @Override
    @Nullable
    public Map.Entry<Integer, IKeyframe<T>> getPrevKeyframe(int time) {
        return keyframes.lowerEntry(time);
    }

    @Override
    @Nullable
    public Map.Entry<Integer, IKeyframe<T>> getNextKeyframe(int time) {
        return keyframes.higherEntry(time);
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
            IKeyframe<T> keyframe = keyframes.remove(time);
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
