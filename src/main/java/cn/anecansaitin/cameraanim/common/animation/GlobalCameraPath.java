package cn.anecansaitin.cameraanim.common.animation;

import cn.anecansaitin.cameraanim.common.animation.interpolation.types.PathInterpolator;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/// 全局相机轨迹
public class GlobalCameraPath {
    private final TreeMap<Integer, CameraKeyframe> keyframes;
    private final Int2ObjectOpenHashMap<CameraKeyframe> keyframeMapCache;
    private final ArrayList<CameraKeyframe> keyframeListCache;
    private boolean dirty;
    private final String id;
    private long version;
    private UUID lastModifier;

    private boolean nativeMode;

    public GlobalCameraPath(String id, @Nullable Player lastModifier) {
        keyframes = new TreeMap<>();
        keyframeMapCache = new Int2ObjectOpenHashMap<>();
        keyframeListCache = new ArrayList<>();
        this.id = id;
        version = System.currentTimeMillis();
        this.lastModifier = lastModifier == null ? UUID.fromString("00000000-0000-0000-0000-000000000000") : lastModifier.getUUID();
    }

    public GlobalCameraPath(String id) {
        this(id, null);
    }

    public GlobalCameraPath(TreeMap<Integer, CameraKeyframe> keyframes, String id) {
        this(keyframes, id, 0, UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    private GlobalCameraPath(TreeMap<Integer, CameraKeyframe> keyframes, String id, long version, UUID lastModifier) {
        this.keyframes = keyframes;
        this.id = id;
        keyframeMapCache = new Int2ObjectOpenHashMap<>(keyframes);
        keyframeListCache = new ArrayList<>(keyframes.values());
        this.version = version;
        this.lastModifier = lastModifier;
    }

    private GlobalCameraPath(TreeMap<Integer, CameraKeyframe> keyframes, String id, long version, UUID lastModifier, boolean nativeMode) {
        this.keyframes = keyframes;
        this.id = id;
        keyframeMapCache = new Int2ObjectOpenHashMap<>(keyframes);
        keyframeListCache = new ArrayList<>(keyframes.values());
        this.version = version;
        this.lastModifier = lastModifier;
        this.nativeMode = nativeMode;
    }

    /// 把点加入到指定时间
    ///
    /// 相同时间点进行覆盖
    public void add(int time, CameraKeyframe point) {
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

    public void add(CameraKeyframe point) {
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

    public Int2ObjectMap.FastEntrySet<CameraKeyframe> getEntries() {
        return keyframeMapCache.int2ObjectEntrySet();
    }

    public ArrayList<CameraKeyframe> getPoints() {
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

        CameraKeyframe point = keyframeMapCache.get(time);

        if (point.getPathInterpolator() == PathInterpolator.BEZIER) {
            Map.Entry<Integer, CameraKeyframe> pre = keyframes.lowerEntry(time);

            if (pre != null) {
                CameraKeyframe prePoint = pre.getValue();
                point.getPathBezier().reset(prePoint.getPos(), point.getPos());
            }
        }

        Map.Entry<Integer, CameraKeyframe> next = keyframes.higherEntry(time);

        if (next != null) {
            CameraKeyframe nextPoint = next.getValue();

            if (nextPoint.getPathInterpolator() != PathInterpolator.BEZIER) {
                return;
            }

            nextPoint.getPathBezier().reset(nextPoint.getPos(), point.getPos());
        }
    }

    public void remove(int time) {
        if (!keyframeMapCache.containsKey(time)) {
            return;
        }

        Map.Entry<Integer, CameraKeyframe> pre = keyframes.lowerEntry(time);
        Map.Entry<Integer, CameraKeyframe> next = keyframes.higherEntry(time);
        keyframes.remove(time);
        keyframeMapCache.remove(time);
        dirty = true;

        if (next == null || pre == null) {
            return;
        }

        CameraKeyframe nextPoint = next.getValue();

        if (nextPoint.getPathInterpolator() != PathInterpolator.BEZIER) {
            return;
        }

        CameraKeyframe prePoint = pre.getValue();
        nextPoint.getPathBezier().reset(prePoint.getPos(), nextPoint.getPos());
    }

    @Nullable
    public CameraKeyframe getPoint(int time) {
        return keyframeMapCache.get(time);
    }

    @Nullable
    public CameraKeyframe getPrePoint(int time) {
        Map.Entry<Integer, CameraKeyframe> entry = getPreEntry(time);
        if (entry == null) return null;
        return entry.getValue();
    }

    @Nullable
    public Map.Entry<Integer, CameraKeyframe> getPreEntry(int time) {
        return keyframes.lowerEntry(time);
    }

    @Nullable
    public CameraKeyframe getNextPoint(int time) {
        Map.Entry<Integer, CameraKeyframe> next = getNextEntry(time);
        if (next == null) return null;
        return keyframes.higherEntry(time).getValue();
    }

    @Nullable
    public Map.Entry<Integer, CameraKeyframe> getNextEntry(int time) {
        return keyframes.higherEntry(time);
    }

    public int getLength() {
        return keyframes.lastKey();
    }

    public void setTime(int oldTime, int newTime) {
        if (!keyframeMapCache.containsKey(oldTime)) {
            return;
        }

        CameraKeyframe point = keyframeMapCache.remove(oldTime);
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

    public GlobalCameraPath resetID(String id) {
        return new GlobalCameraPath(keyframes, id, version, lastModifier, nativeMode);
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public UUID getLastModifier() {
        return lastModifier;
    }

    public void setLastModifier(UUID lastModifier) {
        this.lastModifier = lastModifier;
    }

    public boolean isNativeMode() {
        return nativeMode;
    }

    public void setNativeMode(boolean nativeMode) {
        this.nativeMode = nativeMode;
    }

    public GlobalCameraPath toNative(Vector3f pos, float yRot) {
        TreeMap<Integer, CameraKeyframe> map = new TreeMap<>();

        for (Map.Entry<Integer, CameraKeyframe> entry : keyframes.entrySet()) {
            map.put(entry.getKey(), entry.getValue().copy());
        }

        Matrix3f matrix3f = new Matrix3f();
        matrix3f.rotateY(yRot * Mth.DEG_TO_RAD);

        for (CameraKeyframe keyframe : map.values()) {
            Vector3f pos1 = keyframe.getPos().sub(pos);
            matrix3f.transform(pos1);
            keyframe.getRot().sub(0, yRot, 0);
        }

        return new GlobalCameraPath(map, id, version, lastModifier, true);
    }

    public GlobalCameraPath fromNative(Vector3f pos, float yRot) {
        TreeMap<Integer, CameraKeyframe> map = new TreeMap<>(keyframes);
        Matrix3f matrix3f = new Matrix3f();
        matrix3f.rotateY((360 - yRot) * Mth.DEG_TO_RAD);

        for (CameraKeyframe keyframe : map.values()) {
            Vector3f pos1 = matrix3f.transform(keyframe.getPos());
            pos1.add(pos);
            keyframe.getRot().add(0, yRot, 0);
        }

       return new GlobalCameraPath(map, id, version, lastModifier, true);
    }

    public String toJsonString(Gson gson) {
        return gson.toJson(keyframes);
    }

    public static CompoundTag toNBT(GlobalCameraPath path) {
        CompoundTag root = new CompoundTag();
        root.putString("id", path.id);
        root.putLong("version", path.version);
        root.putUUID("lastModifier", path.lastModifier);
        root.putBoolean("native", path.nativeMode);
        ListTag keyframes = new ListTag();

        for (Map.Entry<Integer, CameraKeyframe> entry : path.keyframes.entrySet()) {
            CompoundTag point = new CompoundTag();
            point.putInt("time", entry.getKey());
            point.put("keyframe", CameraKeyframe.toNBT(entry.getValue()));
            keyframes.add(point);
        }

        root.put("keyframes", keyframes);
        return root;
    }

    public static GlobalCameraPath fromNBT(CompoundTag root) {
        ListTag list = root.getList("keyframes", 10);
        TreeMap<Integer, CameraKeyframe> map = new TreeMap<>();

        for (int i = 0; i < list.size(); i++) {
            CompoundTag keyframe = list.getCompound(i);
            map.put(keyframe.getInt("time"),
                    CameraKeyframe.fromNBT(keyframe.getCompound("keyframe"))
            );
        }

        String id = root.getString("id");
        long version = root.getLong("version");
        UUID lastModifier = root.getUUID("lastModifier");
        boolean nativeMode = root.getBoolean("native");
        return new GlobalCameraPath(map, id, version, lastModifier, nativeMode);
    }
}
