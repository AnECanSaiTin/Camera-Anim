package cn.anecansaitin.cameraanim.client.register;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.gui.overlay.ModifyModeOverlay;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = CameraAnim.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ModOverlays {
    @SubscribeEvent
    public static void register(RegisterGuiLayersEvent event) {
        event.registerBelow(VanillaGuiLayers.HOTBAR, ResourceLocation.fromNamespaceAndPath(CameraAnim.MODID, "modify_mode_overlay"), new ModifyModeOverlay());
    }
}
