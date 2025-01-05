package cn.anecansaitin.cameraanim.common.network;

import cn.anecansaitin.cameraanim.common.GlobalCameraSavedData;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import cn.anecansaitin.cameraanim.common.data_entity.GlobalCameraPathInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Collection;

public class ServerPayloadManager {
    public static final ServerPayloadManager INSTANCE = new ServerPayloadManager();

    public CompoundTag checkGlobalPath(int page, int size, IPayloadContext context) {
        ServerLevel level = (ServerLevel) context.player().level();
        Collection<GlobalCameraPath> paths = GlobalCameraSavedData.getData(level).getPaths();
        ArrayList<GlobalCameraPath> pathList = new ArrayList<>(paths);
        int begin = (page - 1) * size;
        int end = Math.min(begin + size, pathList.size());
        CompoundTag root = new CompoundTag();
        root.putInt("size", size);
        root.putInt("page", page);

        if (begin >= end) {
            root.putBoolean("succeed", false);
        } else {
            root.putBoolean("succeed", true);
            ListTag result = new ListTag();
            root.put("paths", result);

            for (int i = begin; i < end; i++) {
                GlobalCameraPath path = pathList.get(i);
                result.add(GlobalCameraPathInfo.toNBT(path));
            }
        }

        return root;
    }

    public CompoundTag putGlobalPath(GlobalCameraPath path, IPayloadContext context) {
        ServerLevel level = (ServerLevel) context.player().level();
        path.setVersion(System.currentTimeMillis());
        path.setLastModifier(context.player().getUUID());
        GlobalCameraSavedData.getData(level).addPath(path);
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("succeed", true);
        return tag;
    }

    public CompoundTag removeGlobalPath(String id, IPayloadContext context) {
        ServerLevel level = (ServerLevel) context.player().level();
        GlobalCameraSavedData.getData(level).removePath(id);
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("succeed", true);
        return tag;
    }

    public CompoundTag getGlobalPath(String id, int receiver, IPayloadContext context) {
        ServerLevel level = (ServerLevel) context.player().level();
        GlobalCameraPath path = GlobalCameraSavedData.getData(level).getPath(id);
        CompoundTag root = new CompoundTag();
        root.putInt("receiver", receiver);

        if (path == null) {
            root.putBoolean("succeed", false);
        } else {
            root.put("path", GlobalCameraPath.toNBT(path));
            root.putBoolean("succeed", true);
        }

        return root;
    }
}
