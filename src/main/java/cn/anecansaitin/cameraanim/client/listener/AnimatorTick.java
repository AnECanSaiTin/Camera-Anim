package cn.anecansaitin.cameraanim.client.listener;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.Animator;
import cn.anecansaitin.cameraanim.client.ClientUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = CameraAnim.MODID, value = Dist.CLIENT)
public class AnimatorTick {
    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if(ClientUtil.gamePaused()) {
            return;
        }

        Animator.INSTANCE.tick();
    }
}
