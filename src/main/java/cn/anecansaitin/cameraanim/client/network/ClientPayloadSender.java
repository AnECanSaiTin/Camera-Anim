package cn.anecansaitin.cameraanim.client.network;

import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import cn.anecansaitin.cameraanim.common.network.C2SPayloadManager;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.network.PacketDistributor;

public class ClientPayloadSender {
    public static void putGlobalPath(GlobalCameraPath path) {
        send("putGlobalPath", GlobalCameraPath.toNBT(path));
    }

    public static void removeGlobalPath(String id) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        send("removeGlobalPath", tag);
    }

    public static void getGlobalPath(String id) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        send("getGlobalPath", tag);
    }

    public static void checkGlobalPath(int page, int size) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("page", page);
        tag.putInt("size", size);
        send("checkGlobalPath", tag);
    }

    public static void send(String key, CompoundTag value) {
        CompoundTag root = new CompoundTag();
        root.putString("key", key);
        root.put("value", value);
        PacketDistributor.sendToServer(new C2SPayloadManager(root));
    }
}
