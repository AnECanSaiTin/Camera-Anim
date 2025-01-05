package cn.anecansaitin.cameraanim.common.network;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.function.BiFunction;

public record C2SPayloadManager(CompoundTag tag) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<C2SPayloadManager> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CameraAnim.MODID, "c_2_s_payload_manager"));
    public static final StreamCodec<ByteBuf, C2SPayloadManager> CODEC = StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, C2SPayloadManager::tag, C2SPayloadManager::new);
    private static final HashMap<String, BiFunction<CompoundTag, IPayloadContext, CompoundTag>> HANDLERS = new HashMap<>();

    static {
        HANDLERS.put("checkGlobalPath", (tag, context) -> ServerPayloadManager.INSTANCE.checkGlobalPath(tag.getInt("page"), tag.getInt("size"), context));
        HANDLERS.put("putGlobalPath", (tag, context) -> ServerPayloadManager.INSTANCE.putGlobalPath(GlobalCameraPath.fromNBT(tag), context));
        HANDLERS.put("removeGlobalPath", (tag, context) -> ServerPayloadManager.INSTANCE.removeGlobalPath(tag.getString("id"), context));
        HANDLERS.put("getGlobalPath", (tag, context) -> ServerPayloadManager.INSTANCE.getGlobalPath(tag.getString("id"), tag.getInt("receiver"), context));
    }

    @Override
    public Type<C2SPayloadManager> type() {
        return TYPE;
    }

    public static void handle(C2SPayloadManager payload, IPayloadContext context) {
        String key = payload.tag.getString("key");
        BiFunction<CompoundTag, IPayloadContext, CompoundTag> handler = HANDLERS.get(key);

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

        context.reply(new S2CPayloadReply(root));
    }
}
