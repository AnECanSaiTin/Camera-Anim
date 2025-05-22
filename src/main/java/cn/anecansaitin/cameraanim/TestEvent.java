package cn.anecansaitin.cameraanim;

import cn.anecansaitin.cameraanim.animation.Animation;
import cn.anecansaitin.cameraanim.client.ClientUtil;
import cn.anecansaitin.freecameraapi.CameraModifierManager;
import cn.anecansaitin.freecameraapi.ICameraModifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import static cn.anecansaitin.freecameraapi.ModifierStates.*;

@EventBusSubscriber(value = Dist.CLIENT)
public class TestEvent {
    public static int tick = -20;
    private static int max = 20 * 10;
    private static boolean ok = false;
    private static ICameraModifier modifier = CameraModifierManager.createModifier("test", false);
    private static Animation animation = new Animation();

    @SubscribeEvent
    public static void tick(ClientTickEvent.Pre event) {
        if (ok) {
            tick++;
        } else {
            modifier.disable();
        }
    }

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        if (!ok || event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            return;
        }

        if (tick < 0) {
            return;
        }

        animation.animationTick(tick, ClientUtil.partialTicks());
        modifier.setState(ENABLE | POS_ENABLED | GLOBAL_MODE_ENABLED);

        if (tick >= max) {
            ok = false;
            tick = -20;
        }
    }
}
