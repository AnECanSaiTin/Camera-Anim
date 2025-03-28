package cn.anecansaitin.cameraanim.generator;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.generator.assest.ModLangProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = CameraAnim.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        event.addProvider(event.includeServer(), new ModLangProvider(event.getGenerator().getPackOutput(), "zh_cn", true));
        event.addProvider(event.includeServer(), new ModLangProvider(event.getGenerator().getPackOutput(), "en_us", false));
    }
}
