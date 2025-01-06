package cn.anecansaitin.cameraanim.common.network;

import cn.anecansaitin.cameraanim.common.ModNetwork;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public record C2SPayloadManager(CompoundTag tag) {
    private static final HashMap<String, BiFunction<CompoundTag, NetworkEvent.Context, CompoundTag>> HANDLERS = new HashMap<>();

    static {
        HANDLERS.put("checkGlobalPath", (tag, context) -> ServerPayloadManager.INSTANCE.checkGlobalPath(tag.getInt("page"), tag.getInt("size"), context));
        HANDLERS.put("putGlobalPath", (tag, context) -> ServerPayloadManager.INSTANCE.putGlobalPath(GlobalCameraPath.fromNBT(tag), context));
        HANDLERS.put("removeGlobalPath", (tag, context) -> ServerPayloadManager.INSTANCE.removeGlobalPath(tag.getString("id"), context));
        HANDLERS.put("getGlobalPath", (tag, context) -> ServerPayloadManager.INSTANCE.getGlobalPath(tag.getString("id"), tag.getInt("receiver"), context));
    }

    public static void encode(C2SPayloadManager pag, FriendlyByteBuf buf) {
        buf.writeNbt(pag.tag);
    }

    public static C2SPayloadManager decode(FriendlyByteBuf bug) {
        return new C2SPayloadManager(bug.readNbt());
    }

    public static void handle(C2SPayloadManager payload, Supplier<NetworkEvent.Context> contextSupplier) {
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

            ModNetwork.INSTANCE.reply(new S2CPayloadReply(root), context);
        });

        context.setPacketHandled(true);
    }
}
