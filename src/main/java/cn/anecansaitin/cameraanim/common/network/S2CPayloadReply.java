package cn.anecansaitin.cameraanim.common.network;

import cn.anecansaitin.cameraanim.client.network.ClientPayloadManager;
import cn.anecansaitin.cameraanim.common.ModNetwork;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import cn.anecansaitin.cameraanim.common.data_entity.GlobalCameraPathInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public record S2CPayloadReply(CompoundTag tag) {
    private static final HashMap<String, BiFunction<CompoundTag, NetworkEvent.Context, CompoundTag>> HANDLERS = new HashMap<>();

    static {
        HANDLERS.put("checkGlobalPath", (tag, context) -> {
            boolean succeed = tag.getBoolean("succeed");
            ArrayList<GlobalCameraPathInfo> paths;

            if (succeed) {
                paths = new ArrayList<>();
                ListTag list = tag.getList("paths", CompoundTag.TAG_COMPOUND);

                for (int i = 0; i < list.size(); i++) {
                    paths.add(GlobalCameraPathInfo.fromNBT(list.getCompound(i)));
                }
            } else {
                paths = null;
            }

            ClientPayloadManager.INSTANCE.checkGlobalPath(tag.getInt("page"), tag.getInt("size"), succeed, paths, context);
            return null;
        });
        HANDLERS.put("putGlobalPath", (tag, context) -> {
            ClientPayloadManager.INSTANCE.putGlobalPath(tag.getBoolean("succeed"), context);
            return null;
        });
        HANDLERS.put("removeGlobalPath", (tag, context) -> {
            ClientPayloadManager.INSTANCE.removeGlobalPath(tag.getBoolean("succeed"), context);
            return null;
        });
        HANDLERS.put("getGlobalPath", (tag, context) -> {
            boolean succeed = tag.getBoolean("succeed");
            GlobalCameraPath path;

            if (succeed) {
                path = GlobalCameraPath.fromNBT(tag.getCompound("path"));
            } else {
                path = null;
            }

            ClientPayloadManager.INSTANCE.getGlobalPath(path, succeed, tag.getInt("receiver"), context);
            return null;
        });
        HANDLERS.put("getNativePath", (tag, context) -> {
            boolean succeed = tag.getBoolean("succeed");
            GlobalCameraPath path;
            Entity entity;

            if (succeed) {
                path = GlobalCameraPath.fromNBT(tag.getCompound("path"));
                entity = Minecraft.getInstance().level.getEntity(tag.getInt("center"));
            } else {
                path = null;
                entity = null;
            }

            ClientPayloadManager.INSTANCE.getNativePath(path, entity, succeed, context);
            return null;
        });
    }

    public static void encode(S2CPayloadReply pag, FriendlyByteBuf buf) {
        buf.writeNbt(pag.tag);
    }

    public static S2CPayloadReply decode(FriendlyByteBuf bug) {
        return new S2CPayloadReply(bug.readNbt());
    }

    public static void handle(S2CPayloadReply payload, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            String key = payload.tag.getString("key");
            BiFunction<CompoundTag, NetworkEvent.Context, CompoundTag> handler = HANDLERS.get(key);

            if (handler == null) {
                return;
            }

            CompoundTag result = handler.apply(payload.tag.getCompound("value"), context);

            if (result == null) {
                return;
            }

            CompoundTag root = new CompoundTag();
            root.putString("key", key);
            root.put("value", result);

            ModNetwork.INSTANCE.reply(new C2SPayloadManager(root), context);
        });
        context.setPacketHandled(true);
    }
}
