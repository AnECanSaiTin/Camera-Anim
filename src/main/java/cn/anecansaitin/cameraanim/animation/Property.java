package cn.anecansaitin.cameraanim.animation;

import it.unimi.dsi.fastutil.Pair;

import java.util.ArrayList;

public class Property<T> {
    private final ArrayList<Pair<String, ITrack<T>>> tracks;

    public Property() {
        this.tracks = new ArrayList<>();
    }


}
