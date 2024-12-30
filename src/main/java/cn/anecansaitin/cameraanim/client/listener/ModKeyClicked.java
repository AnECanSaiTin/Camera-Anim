package cn.anecansaitin.cameraanim.client.listener;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.Animator;
import cn.anecansaitin.cameraanim.client.ClientUtil;
import cn.anecansaitin.cameraanim.client.ModKeyMapping;
import cn.anecansaitin.cameraanim.client.TrackCache;
import cn.anecansaitin.cameraanim.client.gui.screen.PointSettingScreen;
import cn.anecansaitin.cameraanim.common.animation.CameraPoint;
import cn.anecansaitin.cameraanim.common.animation.PointInterpolationType;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Vector3f;

@EventBusSubscriber(modid = CameraAnim.MODID, value = Dist.CLIENT)
public class ModKeyClicked {
    @SubscribeEvent
    public static void keyClick(ClientTickEvent.Post event) {
        while (ModKeyMapping.ADD_GLOBAL_CAMERA_POINT.get().consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            Camera camera = mc.gameRenderer.getMainCamera();
            TrackCache.getTrack().add(new CameraPoint(camera.getPosition().toVector3f(), camera.rotation().getEulerAnglesYXZ(new Vector3f()), mc.options.fov().get(), PointInterpolationType.LINEAR));
        }

        while (ModKeyMapping.EDIT_MODE.get().consumeClick()) {
            if (ClientUtil.player().isCreative()) {
                TrackCache.EDIT = !TrackCache.EDIT;
            }
        }

        while (ModKeyMapping.VIEW_MODE.get().consumeClick()) {
            if (ClientUtil.player().isCreative()) {
                TrackCache.VIEW = !TrackCache.VIEW;
            }
        }

        while (ModKeyMapping.POINT_SETTING.get().consumeClick()) {
            if (!TrackCache.EDIT || TrackCache.getSelectedPoint().getPointTime() < 0) {
                continue;
            }

            Minecraft.getInstance().setScreen(new PointSettingScreen());
        }

        while (ModKeyMapping.PREVIEW_MODE.get().consumeClick()) {
            if (!TrackCache.EDIT) {
                continue;
            }

            Animator.INSTANCE.setPreview(!Animator.INSTANCE.isPreview());
        }

        while (ModKeyMapping.PLAY.get().consumeClick()) {
            if (!TrackCache.EDIT) {
                continue;
            }

            if (Animator.INSTANCE.isPlaying()) {
                Animator.INSTANCE.stop();
            } else {
                Animator.INSTANCE.play();
            }
        }

        while (ModKeyMapping.RESET.get().consumeClick()) {
            if (!TrackCache.EDIT) {
                continue;
            }

            Animator.INSTANCE.reset();
        }

        while (ModKeyMapping.SET_CAMERA_TIME.get().consumeClick()) {
            if (!TrackCache.EDIT || TrackCache.getSelectedPoint().getPointTime() < 0) {
                continue;
            }

            Animator.INSTANCE.setTime(TrackCache.getSelectedPoint().getPointTime());
        }

        if (ModKeyMapping.BACK.get().isDown() && TrackCache.EDIT) {
            Animator.INSTANCE.back();
        }

        if (ModKeyMapping.FORWARD.get().isDown() && TrackCache.EDIT) {
            Animator.INSTANCE.forward();
        }
    }
}