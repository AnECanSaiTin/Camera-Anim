package cn.anecansaitin.cameraanim.client.listener;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.Animator;
import cn.anecansaitin.cameraanim.client.CameraAnimIdeCache;
import cn.anecansaitin.cameraanim.client.PreviewAnimator;
import cn.anecansaitin.cameraanim.client.ClientUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static cn.anecansaitin.cameraanim.client.CameraAnimIdeCache.getPath;

@Mod.EventBusSubscriber(modid = CameraAnim.MODID, value = Dist.CLIENT)
public class AnimatorTick {
    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ClientUtil.gamePaused()) {
            return;
        }

        if (Animator.INSTANCE.isPlaying()) {
            Animator.INSTANCE.tick();
        } else if ((CameraAnimIdeCache.VIEW || CameraAnimIdeCache.EDIT) && !getPath().getPoints().isEmpty() && !ClientUtil.gamePaused()) {
            PreviewAnimator.INSTANCE.tick();
        }
    }
}
