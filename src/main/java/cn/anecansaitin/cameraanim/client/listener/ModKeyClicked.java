package cn.anecansaitin.cameraanim.client.listener;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.Animator;
import cn.anecansaitin.cameraanim.client.ClientUtil;
import cn.anecansaitin.cameraanim.client.ModKeyMapping;
import cn.anecansaitin.cameraanim.client.PathCache;
import cn.anecansaitin.cameraanim.client.gui.screen.RemotePathSearchScreen;
import cn.anecansaitin.cameraanim.client.gui.screen.PointSettingScreen;
import cn.anecansaitin.cameraanim.common.animation.CameraKeyframe;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import cn.anecansaitin.cameraanim.common.animation.PathInterpolator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Vector3f;

import java.util.Map;

@EventBusSubscriber(modid = CameraAnim.MODID, value = Dist.CLIENT)
public class ModKeyClicked {
    @SubscribeEvent
    public static void keyClick(ClientTickEvent.Post event) {
        while (ModKeyMapping.ADD_GLOBAL_CAMERA_POINT.get().consumeClick()) {
            if (!PathCache.EDIT) {
                continue;
            }

            Minecraft mc = Minecraft.getInstance();
            Camera camera = mc.gameRenderer.getMainCamera();
            float yRot = Mth.wrapDegrees(camera.getYRot());
            float xRot = Mth.wrapDegrees(camera.getXRot());
            PathCache.getTrack().add(new CameraKeyframe(camera.getPosition().toVector3f(), new Vector3f(xRot, yRot, 0), mc.options.fov().get(), PathInterpolator.LINEAR));
        }

        while (ModKeyMapping.DELETE_GLOBAL_CAMERA_POINT.get().consumeClick()) {
            if (!PathCache.EDIT) {
                continue;
            }

            PathCache.SelectedPoint selectedPoint = PathCache.getSelectedPoint();
            int time = selectedPoint.getPointTime();
            GlobalCameraPath track = PathCache.getTrack();
            track.remove(time);
            Map.Entry<Integer, CameraKeyframe> pre = track.getPreEntry(time);

            if (pre == null) {
                selectedPoint.reset();
            } else {
                selectedPoint.setSelected(pre.getKey());
            }
        }

        while (ModKeyMapping.EDIT_MODE.get().consumeClick()) {
            if (ClientUtil.player().isCreative()) {
                PathCache.EDIT = !PathCache.EDIT;
            }
        }

        while (ModKeyMapping.VIEW_MODE.get().consumeClick()) {
            if (ClientUtil.player().isCreative()) {
                PathCache.VIEW = !PathCache.VIEW;
            }
        }

        while (ModKeyMapping.POINT_SETTING.get().consumeClick()) {
            if (!PathCache.EDIT || PathCache.getSelectedPoint().getPointTime() < 0) {
                continue;
            }

            Minecraft.getInstance().setScreen(new PointSettingScreen());
        }

        while (ModKeyMapping.PREVIEW_MODE.get().consumeClick()) {
            if (!PathCache.EDIT) {
                continue;
            }

            Animator.INSTANCE.setPreview(!Animator.INSTANCE.isPreview());
        }

        while (ModKeyMapping.PLAY.get().consumeClick()) {
            if (!PathCache.EDIT) {
                continue;
            }

            if (Animator.INSTANCE.isPlaying()) {
                Animator.INSTANCE.stop();
            } else {
                Animator.INSTANCE.play();
            }
        }

        while (ModKeyMapping.RESET.get().consumeClick()) {
            if (!PathCache.EDIT) {
                continue;
            }

            Animator.INSTANCE.reset();
        }

        while (ModKeyMapping.SET_CAMERA_TIME.get().consumeClick()) {
            if (!PathCache.EDIT || PathCache.getSelectedPoint().getPointTime() < 0) {
                continue;
            }

            Animator.INSTANCE.setTime(PathCache.getSelectedPoint().getPointTime());
        }

        if (ModKeyMapping.BACK.get().isDown() && PathCache.EDIT) {
            Animator.INSTANCE.back();
        }

        if (ModKeyMapping.FORWARD.get().isDown() && PathCache.EDIT) {
            Animator.INSTANCE.forward();
        }

        while (ModKeyMapping.MANAGER.get().consumeClick()) {
            Minecraft.getInstance().setScreen(new RemotePathSearchScreen());
        }
    }
}