package cn.anecansaitin.cameraanim.common.animation;

import it.unimi.dsi.fastutil.ints.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/// 全局相机轨迹
public class GlobalCameraTrack {
    public static final GlobalCameraTrack NULL = new NullTrack();
    private final TreeMap<Integer, CameraPoint> keyframes;
    private final Int2ObjectOpenHashMap<CameraPoint> keyframeMapCache;
    private final ArrayList<CameraPoint> keyframeListCache;
    private boolean dirty;
    private final String id;

    public GlobalCameraTrack(String id) {
        keyframes = new TreeMap<>();
        keyframeMapCache = new Int2ObjectOpenHashMap<>();
        keyframeListCache = new ArrayList<>();
        this.id = id;
    }

    private GlobalCameraTrack(TreeMap<Integer, CameraPoint> keyframes, String id) {
        this.keyframes = keyframes;
        this.id = id;
        keyframeMapCache = new Int2ObjectOpenHashMap<>(keyframes);
        keyframeListCache = new ArrayList<>(keyframes.values());
    }

    /// 把点加入到指定时间
    ///
    /// 相同时间点进行覆盖
    public void add(int time, CameraPoint point) {
        Integer i = keyframes.lastKey();

        if (i != null && i < time) {
            keyframeListCache.add(point);
        } else {
            dirty = true;
        }

        keyframes.put(time, point);
        keyframeMapCache.put(time, point);
        updateBezier(time);
    }

    public void add(CameraPoint point) {
        if (keyframes.isEmpty()) {
            keyframes.put(0, point);
            keyframeMapCache.put(0, point);
        } else {
            int time = keyframes.lastKey() + 20;
            keyframes.put(time, point);
            keyframeMapCache.put(time, point);
            updateBezier(time);
        }

        keyframeListCache.add(point);
    }

    public Int2ObjectMap.FastEntrySet<CameraPoint> getEntries() {
        return keyframeMapCache.int2ObjectEntrySet();
    }

    public ArrayList<CameraPoint> getPoints() {
        updateList();
        return keyframeListCache;
    }

    private void updateList() {
        if (!dirty) {
            return;
        }

        keyframeListCache.clear();
        keyframeListCache.addAll(keyframes.values());
        dirty = false;
    }

    /// 更新控制点
    public void updateBezier(int time) {
        if (!keyframeMapCache.containsKey(time)) {
            return;
        }

        CameraPoint point = keyframeMapCache.get(time);

        if (point.getType() == PointInterpolationType.BEZIER) {
            Map.Entry<Integer, CameraPoint> pre = keyframes.lowerEntry(time);

            if (pre != null) {
                CameraPoint prePoint = pre.getValue();
                Vector3f c = new Vector3f(point.getPosition()).add(prePoint.getPosition()).mul(0.5f);
                prePoint.setRightBezierControl(c.x, c.y, c.z);
                point.setLeftBezierControl(c.x, c.y, c.z);
            }
        }

        Map.Entry<Integer, CameraPoint> next = keyframes.higherEntry(time);

        if (next != null) {
            CameraPoint nextPoint = next.getValue();

            if (nextPoint.getType() != PointInterpolationType.BEZIER) {
                return;
            }

            Vector3f c = new Vector3f(point.getPosition()).add(nextPoint.getPosition()).mul(0.5f);
            nextPoint.setLeftBezierControl(c.x, c.y, c.z);
            point.setRightBezierControl(c.x, c.y, c.z);
        }
    }

    public void remove(int time) {
        if (!keyframeMapCache.containsKey(time)) {
            return;
        }

        Map.Entry<Integer, CameraPoint> pre = keyframes.lowerEntry(time);
        Map.Entry<Integer, CameraPoint> next = keyframes.higherEntry(time);
        keyframes.remove(time);
        keyframeMapCache.remove(time);

        if (next == null || pre == null) {
            return;
        }

        CameraPoint nextPoint = next.getValue();

        if (nextPoint.getType() != PointInterpolationType.BEZIER) {
            return;
        }

        CameraPoint prePoint = pre.getValue();
        Vector3f c = new Vector3f(prePoint.getPosition()).add(nextPoint.getPosition()).mul(0.5f);
        prePoint.setRightBezierControl(c.x, c.y, c.z);
        nextPoint.setLeftBezierControl(c.x, c.y, c.z);
    }

    @Nullable
    public CameraPoint getPoint(int time) {
        return keyframeMapCache.get(time);
    }

    @Nullable
    public CameraPoint getPrePoint(int time) {
        Map.Entry<Integer, CameraPoint> entry = getPreEntry(time);
        if (entry == null) return null;
        return entry.getValue();
    }

    @Nullable
    public Map.Entry<Integer, CameraPoint> getPreEntry(int time) {
        return keyframes.lowerEntry(time);
    }

    @Nullable
    public CameraPoint getNextPoint(int time) {
        Map.Entry<Integer, CameraPoint> next = getNextEntry(time);
        if (next == null) return null;
        return keyframes.higherEntry(time).getValue();
    }

    @Nullable
    public Map.Entry<Integer, CameraPoint> getNextEntry(int time) {
        return keyframes.higherEntry(time);
    }

    public int getLength() {
        return keyframes.lastKey();
    }

    public void setTime(int oldTime, int newTime) {
        if (!keyframeMapCache.containsKey(oldTime)) {
            return;
        }

        CameraPoint point = keyframeMapCache.remove(oldTime);
        keyframeMapCache.put(newTime, point);
        Integer pre = keyframes.lowerKey(oldTime);
        Integer next = keyframes.higherKey(oldTime);
        keyframes.remove(oldTime);
        keyframes.put(newTime, point);

        if (pre != null && newTime < pre || next != null && newTime > next) {
            updateBezier(newTime);
        }

        dirty = true;
    }

    public String getId() {
        return id;
    }

    public GlobalCameraTrack resetID(String id) {
        return new GlobalCameraTrack(keyframes, id);
    }

    private static class NullTrack extends GlobalCameraTrack {
        public NullTrack() {
            super("null");
        }

        @Override
        public void add(int time, CameraPoint point) {
        }

        @Override
        public void add(CameraPoint point) {
        }

        @Override
        public void remove(int index) {
        }

        @Override
        public CameraPoint getPoint(int index) {
            return CameraPoint.NULL;
        }
    }
}
