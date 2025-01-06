package cn.anecansaitin.cameraanim.common;

import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GlobalCameraSavedData extends SavedData {
    private final HashMap<String, GlobalCameraPath> paths = new HashMap<>();

    private static GlobalCameraSavedData create() {
        return new GlobalCameraSavedData();
    }

    private static GlobalCameraSavedData load(CompoundTag tag) {
        GlobalCameraSavedData data = new GlobalCameraSavedData();
        ListTag paths = tag.getList("paths", 10);

        for (int i = 0; i < paths.size(); i++) {
            CompoundTag path = paths.getCompound(i);
            data.paths.put(path.getString("id"), GlobalCameraPath.fromNBT(path.getCompound("path")));
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag paths = new ListTag();

        for (Map.Entry<String, GlobalCameraPath> entry : this.paths.entrySet()) {
            CompoundTag path = new CompoundTag();
            path.putString("id", entry.getKey());
            path.put("path", GlobalCameraPath.toNBT(entry.getValue()));
            paths.add(path);
        }

        tag.put("paths", paths);
        return tag;
    }

    public void addPath(GlobalCameraPath path) {
        paths.put(path.getId(), path);
        setDirty();
    }

    public void removePath(String id) {
        paths.remove(id);
        setDirty();
    }

    @Nullable
    public GlobalCameraPath getPath(String id) {
        return paths.get(id);
    }

    public Collection<GlobalCameraPath> getPaths() {
        return paths.values();
    }

    public static GlobalCameraSavedData getData(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(GlobalCameraSavedData::load, GlobalCameraSavedData::create, "camera_anim");
    }
}
