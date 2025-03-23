package cn.anecansaitin.cameraanim.client.listener;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.animation.Animator;
import cn.anecansaitin.cameraanim.client.ide.CameraAnimIdeCache;
import cn.anecansaitin.cameraanim.client.animation.PreviewAnimator;
import cn.anecansaitin.cameraanim.client.util.ClientUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import static cn.anecansaitin.cameraanim.client.ide.CameraAnimIdeCache.getPath;

@EventBusSubscriber(modid = CameraAnim.MODID, value = Dist.CLIENT)
public class AnimatorTick {
    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if (ClientUtil.gamePaused()) {
            return;
        }

        if (Animator.INSTANCE.isPlaying()) {
            Animator.INSTANCE.tick();
        } else if ((CameraAnimIdeCache.VIEW || CameraAnimIdeCache.EDIT) && !getPath().getPoints().isEmpty() && !ClientUtil.gamePaused()) {
            PreviewAnimator.INSTANCE.tick();
        }
    }
}
