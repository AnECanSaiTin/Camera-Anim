package cn.anecansaitin.cameraanim.common.network;

import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class ServerPayloadSender {
    public static void sendGlobalPath(GlobalCameraPath path, ServerPlayer player, int receiver) {
        CompoundTag root = new CompoundTag();
        root.putInt("receiver", receiver);

        if (path == null) {
            root.putBoolean("succeed", false);
        } else {
            root.put("path", GlobalCameraPath.toNBT(path));
            root.putBoolean("succeed", true);
        }

        send("getGlobalPath", root, player);
    }

    public static void send(String key, CompoundTag value, ServerPlayer player) {
        CompoundTag root = new CompoundTag();
        root.putString("key", key);
        root.put("value", value);
        PacketDistributor.sendToPlayer(player, new S2CPayloadReply(root));
    }
}
