package cn.anecansaitin.cameraanim.client.network;

import cn.anecansaitin.cameraanim.client.Animator;
import cn.anecansaitin.cameraanim.client.CameraAnimIdeCache;
import cn.anecansaitin.cameraanim.client.ClientUtil;
import cn.anecansaitin.cameraanim.client.gui.screen.RemotePathSearchScreen;
import cn.anecansaitin.cameraanim.client.gui.screen.InfoScreen;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import cn.anecansaitin.cameraanim.common.data_entity.GlobalCameraPathInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class ClientPayloadManager {
    public static final ClientPayloadManager INSTANCE = new ClientPayloadManager();
    private static final Component PUT_GLOBAL_PATH_SUCCESS = Component.translatable("gui.camera_anim.client_payload_manager.put_global_path_success");
    private static final Component PUT_GLOBAL_PATH_FAILURE = Component.translatable("gui.camera_anim.client_payload_manager.put_global_path_failure");
    private static final Component DELETE_GLOBAL_PATH_SUCCESS = Component.translatable("gui.camera_anim.client_payload_manager.delete_global_path_success");
    private static final Component DELETE_GLOBAL_PATH_FAILURE = Component.translatable("gui.camera_anim.client_payload_manager.delete_global_path_failure");
    private static final Component GET_GLOBAL_PATH_SUCCESS = Component.translatable("gui.camera_anim.client_payload_manager.get_global_path_success");
    private static final Component GET_GLOBAL_PATH_FAILURE = Component.translatable("gui.camera_anim.client_payload_manager.get_global_path_failure");

    public void checkGlobalPath(int page, int size, boolean succeed, @Nullable List<GlobalCameraPathInfo> paths, NetworkEvent.Context context) {
        if (succeed && paths != null) {
            Screen screen = Minecraft.getInstance().screen;

            if (!(screen instanceof RemotePathSearchScreen search)) {
                return;
            }

            search.setInfo(paths);
        }
    }

    public void putGlobalPath(boolean succeed, NetworkEvent.Context context) {
        if (succeed) {
            ClientUtil.pushGuiLayer(new InfoScreen(PUT_GLOBAL_PATH_SUCCESS));
        } else {
            ClientUtil.pushGuiLayer(new InfoScreen(PUT_GLOBAL_PATH_FAILURE));
        }
    }

    public void removeGlobalPath(boolean succeed, NetworkEvent.Context context) {
        if (succeed) {
            ClientUtil.pushGuiLayer(new InfoScreen(DELETE_GLOBAL_PATH_SUCCESS));
        } else {
            ClientUtil.pushGuiLayer(new InfoScreen(DELETE_GLOBAL_PATH_FAILURE));
        }
    }

    public void getGlobalPath(@Nullable GlobalCameraPath path, boolean succeed, int receiver, NetworkEvent.Context context) {
        switch (receiver) {
            case 0 -> {
                if (succeed && path != null) {
                    if (path.isNativeMode()) {
                        Vector3f pos = ClientUtil.player().position().toVector3f();
                        float v = ClientUtil.playerYHeadRot();
                        path = path.fromNative(pos, v);
                        CameraAnimIdeCache.setNative(pos, new Vector3f(0, v, 0));
                    }

                    CameraAnimIdeCache.setPath(path);
                    ClientUtil.pushGuiLayer(new InfoScreen(GET_GLOBAL_PATH_SUCCESS));
                } else {
                    ClientUtil.pushGuiLayer(new InfoScreen(GET_GLOBAL_PATH_FAILURE));
                }
            }
            case 1 -> {
                if (succeed && path != null) {
                    Animator.INSTANCE.setPathAndPlay(path);
                    ClientUtil.toThirdView();
                }
            }
        }
    }

    public void getNativePath(@Nullable GlobalCameraPath path, @Nullable Entity entity, boolean succeed, NetworkEvent.Context context) {
        if (succeed && path != null && entity != null) {
            Animator.INSTANCE.setPathAndPlay(path, entity.position().toVector3f(), new Vector3f(0, entity.getYRot(), 0));
            ClientUtil.toThirdView();
        }
    }
}
