package cn.anecansaitin.cameraanim.client.network;

import cn.anecansaitin.cameraanim.client.ClientUtil;
import cn.anecansaitin.cameraanim.client.PathCache;
import cn.anecansaitin.cameraanim.client.gui.screen.RemotePathSearchScreen;
import cn.anecansaitin.cameraanim.client.gui.screen.InfoScreen;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import cn.anecansaitin.cameraanim.common.data_entity.GlobalCameraPathInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClientPayloadManager {
    public static final ClientPayloadManager INSTANCE = new ClientPayloadManager();

    public void checkGlobalPath(int page, int size, boolean succeed, @Nullable List<GlobalCameraPathInfo> paths, IPayloadContext context) {
        if (succeed && paths != null) {
            Screen screen = Minecraft.getInstance().screen;

            if (!(screen instanceof RemotePathSearchScreen search)) {
                return;
            }

            search.setInfo(paths);
        }
    }

    public void putGlobalPath(boolean succeed, IPayloadContext context) {
        if (succeed) {
            ClientUtil.pushGuiLayer(new InfoScreen("添加路径成功"));
        } else {
            ClientUtil.pushGuiLayer(new InfoScreen("添加路径失败"));
        }
    }

    public void removeGlobalPath(boolean succeed, IPayloadContext context) {
        if (succeed) {
            ClientUtil.pushGuiLayer(new InfoScreen("删除路径成功"));
        } else {
            ClientUtil.pushGuiLayer(new InfoScreen("删除路径失败"));
        }
    }

    public void getGlobalPath(GlobalCameraPath path, IPayloadContext context) {
        PathCache.setTrack(path);
        ClientUtil.pushGuiLayer(new InfoScreen("获取路径成功"));
    }
}
