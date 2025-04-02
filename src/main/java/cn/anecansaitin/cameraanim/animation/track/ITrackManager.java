package cn.anecansaitin.cameraanim.animation.track;

import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.Nullable;

public interface ITrackManager<T> {
    boolean interpolated(int time, float t, T dest);

    @Nullable
    ITrack<T> getTrack(int index);

    @Nullable
    ITrack<T> getTrack(String name);

    @Nullable
    Pair<String, ITrack<T>> getTrackWithName(int index);

    void addTrack(String name, ITrack<T> track);

    void addTrack(int index, String name, ITrack<T> track);

    void removeTrack(int index);

    void removeTrack(String name);

    void clearTracks();

    boolean swapTrack(int index, int newIndex);

    boolean moveTrack(int index, int afterIndex);

    boolean renameTrack(int index, String name);
}
