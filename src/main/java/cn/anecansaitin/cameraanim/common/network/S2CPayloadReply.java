package cn.anecansaitin.cameraanim.common.network;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.network.ClientPayloadManager;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import cn.anecansaitin.cameraanim.common.data_entity.GlobalCameraPathInfo;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiFunction;

public record S2CPayloadReply(CompoundTag tag) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<S2CPayloadReply> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CameraAnim.MODID, "s_2_c_payload_reply"));
    public static final StreamCodec<ByteBuf, S2CPayloadReply> CODEC = StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, S2CPayloadReply::tag, S2CPayloadReply::new);
    private static final HashMap<String, BiFunction<CompoundTag, IPayloadContext, CompoundTag>> HANDLERS = new HashMap<>();

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
            ClientPayloadManager.INSTANCE.getGlobalPath(GlobalCameraPath.fromNBT(tag), context);
            return null;
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(S2CPayloadReply payload, IPayloadContext context) {
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

        context.reply(new C2SPayloadManager(root));
    }
}
