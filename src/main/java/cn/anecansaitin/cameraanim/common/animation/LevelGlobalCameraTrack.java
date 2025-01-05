package cn.anecansaitin.cameraanim.common.animation;

import java.util.HashMap;

public class LevelGlobalCameraTrack {
    private final HashMap<String, GlobalCameraPath> trackMap = new HashMap<>();

    public void addTrack(String name, GlobalCameraPath track) {
        trackMap.put(name, track);
    }

    public GlobalCameraPath getTrack(String name) {
        return trackMap.get(name);
    }
}
