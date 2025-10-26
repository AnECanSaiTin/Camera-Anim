package cn.anecansaitin.cameraanim.client.listener;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.ClientPaths;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CameraAnim.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    @SubscribeEvent
    public static void Setup(FMLClientSetupEvent event) {
        ClientPaths.loadAllFromFile();
    }
}
