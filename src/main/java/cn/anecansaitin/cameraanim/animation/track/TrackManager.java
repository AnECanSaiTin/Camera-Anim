package cn.anecansaitin.cameraanim.animation.track;

import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class TrackManager<T> implements ITrackManager<T> {
    private final ArrayList<Pair<String, ITrack<T>>> tracks;

    public TrackManager() {
        tracks = new ArrayList<>();
    }

    @Override
    public boolean interpolated(int time, float t, T dest) {
        if (tracks.isEmpty()) {
            return false;
        }

        for (int i = tracks.size() - 1; i >= 0; i--) {
            ITrack<T> track = tracks.get(i).second();

            if (track.interpolated(time, t, dest)) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public ITrack<T> getTrack(int index) {
        Pair<String, ITrack<T>> pair = getTrackWithName(index);

        if (pair == null) {
            return null;
        }

        return pair.second();
    }

    @Nullable
    @Override
    public ITrack<T> getTrack(String name) {
        Pair<String, ITrack<T>> pair = getTrackWithName(name);

        if (pair == null) {
            return null;
        }

        return pair.second();
    }

    @Override
    public Pair<String, ITrack<T>> getTrackWithName(int index) {
        return tracks.get(index);
    }

    @Nullable
    public Pair<String, ITrack<T>> getTrackWithName(String name) {
        for (int i = tracks.size() - 1; i >= 0; i--) {
            if (tracks.get(i).first().equals(name)) {
                return tracks.get(i);
            }
        }

        return null;
    }

    @Override
    public void putTrack(String name, ITrack<T> track) {
        tracks.add(Pair.of(name, track));
    }

    @Override
    public void putTrack(int index, String name, ITrack<T> track) {
        tracks.add(index, Pair.of(name, track));
    }

    @Override
    public void removeTrack(int index) {
        tracks.remove(index);
    }

    @Override
    public void removeTrack(String name) {
        for (int i = tracks.size() - 1; i >= 0; i--) {
            if (tracks.get(i).first().equals(name)) {
                tracks.remove(i);
                break;
            }
        }
    }

    @Override
    public void clearTracks() {
        tracks.clear();
    }

    @Override
    public boolean swapTrack(int index, int newIndex) {
        if (index == newIndex || index < 0 || newIndex < 0 || index >= tracks.size() || newIndex >= tracks.size()) {
            return false;
        }

        tracks.set(index, tracks.set(newIndex, tracks.get(index)));
        return true;
    }

    /// 将index位置的Track移动到afterIndex所对应元素位置后一位
    @Override
    public boolean moveTrack(int index, int afterIndex) {
        if (index == afterIndex || (index > afterIndex && index - 1 == afterIndex) || index < 0 || afterIndex < 0 || index >= tracks.size() || afterIndex >= tracks.size()) {
            return false;
        }

        if (index > afterIndex) {
            Pair<String, ITrack<T>> track = tracks.get(index);
            tracks.remove(index);
            tracks.add(afterIndex, track);
        } else {
            Pair<String, ITrack<T>> track = tracks.get(index);
            tracks.remove(index);
            tracks.add(afterIndex + 1, track);
        }

        return true;
    }

    @Override
    public boolean renameTrack(int index, String name) {
        if (index < 0 || index >= tracks.size()) {
            return false;
        }

        tracks.set(index, Pair.of(name, getTrack(index)));
        return true;
    }
}
