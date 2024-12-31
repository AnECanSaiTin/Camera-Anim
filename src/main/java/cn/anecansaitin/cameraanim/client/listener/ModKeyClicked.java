package cn.anecansaitin.cameraanim.client.listener;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.Animator;
import cn.anecansaitin.cameraanim.client.ClientUtil;
import cn.anecansaitin.cameraanim.client.ModKeyMapping;
import cn.anecansaitin.cameraanim.client.TrackCache;
import cn.anecansaitin.cameraanim.client.gui.screen.PointSettingScreen;
import cn.anecansaitin.cameraanim.common.animation.CameraPoint;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraTrack;
import cn.anecansaitin.cameraanim.common.animation.PointInterpolationType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;

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

        while (ModKeyMapping.LOAD.get().consumeClick()) {
            Path path = FMLPaths.GAMEDIR.get().resolve("camera_anim.json");
            File file = path.toFile();

            if (file.exists()) {
                try {
                    String s = Files.readString(path);
                    Gson gson = new Gson();
                    TypeToken<TreeMap<Integer, CameraPoint>> type = new TypeToken<>(){};
                    TreeMap<Integer, CameraPoint> map = gson.fromJson(s, type.getType());
                    TrackCache.setTrack(new GlobalCameraTrack(map, "test"));
                    ClientUtil.player().displayClientMessage(Component.literal("动画文件加载成功"), true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                ClientUtil.player().displayClientMessage(Component.literal("文件不存在，请把动画文件放到.minecraft目录下。"), true);
            }
        }

        while (ModKeyMapping.SAVE.get().consumeClick()) {
            Path path = FMLPaths.GAMEDIR.get().resolve("camera_anim.json");
            String json = new Gson().toJson(TrackCache.getTrack().getKeyframes());
            try {
                Files.writeString(path, json);
                ClientUtil.player().displayClientMessage(Component.literal("动画文件保存成功"), true);
            } catch (IOException e) {
                ClientUtil.player().displayClientMessage(Component.literal("无法在.minecraft目录保存动画文件"), true);
            }
        }
    }
}