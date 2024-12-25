package cn.anecansaitin.cameraanim.client.listener;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.TrackCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.joml.Vector3f;

@EventBusSubscriber(modid = CameraAnim.MODID)
public class MouseClick {

    @SubscribeEvent
    public static void onClick(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        Options options = mc.options;

        if (!TrackCache.EDIT) {
            return;
        }
        // 编辑模式下，阻断鼠标点击事件
        event.setCanceled(true);
        LocalPlayer player = mc.player;
        float deltaTicks = mc.getDeltaTracker().getGameTimeDeltaTicks();

        if (event.getKeyMapping() == options.keyAttack) {
            // 左键
            Vector3f view = player.getViewVector(deltaTicks).toVector3f();
            Vector3f eye = player.getEyePosition(deltaTicks).toVector3f();
            TrackCache.pick(eye, view, (float) player.entityInteractionRange());
        } else if (event.getKeyMapping() == options.keyUse) {
            // 右键
        }
    }
}
