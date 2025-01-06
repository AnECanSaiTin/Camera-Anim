package cn.anecansaitin.cameraanim.common;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.common.network.C2SPayloadManager;
import cn.anecansaitin.cameraanim.common.network.S2CPayloadReply;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CameraAnim.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    static {
        int i = 0;
        INSTANCE.registerMessage(i++, C2SPayloadManager.class, C2SPayloadManager::encode, C2SPayloadManager::decode, C2SPayloadManager::handle);
        INSTANCE.registerMessage(i++ , S2CPayloadReply.class, S2CPayloadReply::encode, S2CPayloadReply::decode, S2CPayloadReply::handle);
    }

    public static void init() {

    }
}
