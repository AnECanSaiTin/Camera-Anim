package cn.anecansaitin.cameraanim.client.listener;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.ModKeyMapping;
import cn.anecansaitin.cameraanim.client.TrackCache;
import cn.anecansaitin.cameraanim.common.animation.CameraPoint;
import cn.anecansaitin.cameraanim.common.animation.PointInterpolationType;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = CameraAnim.MODID, value = Dist.CLIENT)
public class ModKeyClicked {
    @SubscribeEvent
    public static void keyClick(ClientTickEvent.Post event) {
        while (ModKeyMapping.ADD_GLOBAL_CAMERA_POINT.get().consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            Camera camera = mc.gameRenderer.getMainCamera();
            TrackCache.getTrack().add(new CameraPoint(camera.getPosition().toVector3f(), camera.rotation(), mc.options.fov().get(), PointInterpolationType.LINEAR));
        }

        while (ModKeyMapping.EDIT_MODE.get().consumeClick()) {
            TrackCache.EDIT = !TrackCache.EDIT;
        }

        while (ModKeyMapping.VIEW_MODE.get().consumeClick()) {
            TrackCache.VIEW = !TrackCache.VIEW;
        }
    }
}