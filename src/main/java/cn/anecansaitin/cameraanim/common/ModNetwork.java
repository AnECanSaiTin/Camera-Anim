package cn.anecansaitin.cameraanim.common;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.common.network.C2SPayloadManager;
import cn.anecansaitin.cameraanim.common.network.S2CPayloadReply;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CameraAnim.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetwork {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.optional()
                .playToServer(
                        C2SPayloadManager.TYPE,
                        C2SPayloadManager.CODEC,
                        C2SPayloadManager::handle
                ).playToClient(
                        S2CPayloadReply.TYPE,
                        S2CPayloadReply.CODEC,
                        S2CPayloadReply::handle
                );
    }
}
