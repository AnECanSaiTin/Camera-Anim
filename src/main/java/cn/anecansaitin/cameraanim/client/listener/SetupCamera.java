package cn.anecansaitin.cameraanim.client.listener;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.Animator;
import cn.anecansaitin.freecameraapi.CameraModifierManager;
import cn.anecansaitin.freecameraapi.ICameraModifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;

@EventBusSubscriber(modid = CameraAnim.MODID, value = Dist.CLIENT)
public class SetupCamera {
    private static final ICameraModifier modifier = CameraModifierManager
            .createModifier(CameraAnim.MODID, true)
            .enableFov()
            .enablePos()
            .enableRotation()
            .enableGlobalMode();

    @SubscribeEvent
    public static void setupCamera(ComputeFovModifierEvent event) {
        Animator animator = Animator.INSTANCE;

        if (!animator.isPlaying()) {
            modifier.disable();
            return;
        }

        modifier.enable()
                .setPos(animator.getPosition().x, animator.getPosition().y, animator.getPosition().z)
                .setRotationYXZ(animator.getRotation())
                .setFov(animator.getFov());
    }
}
