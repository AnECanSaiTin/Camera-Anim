package cn.anecansaitin.cameraanim.client.listener;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.CameraAnimIdeCache;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import static cn.anecansaitin.cameraanim.client.ClientUtil.*;

@Mod.EventBusSubscriber(modid = CameraAnim.MODID, value = Dist.CLIENT)
public class MouseInput {
    @SubscribeEvent
    public static void onMouseReleased(InputEvent.MouseButton.Pre event) {
        if (Minecraft.getInstance().screen != null) {
            return;
        }

        switch (event.getButton()) {
            case GLFW.GLFW_MOUSE_BUTTON_LEFT -> {
                switch (event.getAction()) {
                    case GLFW.GLFW_PRESS -> leftPress(event);
                    case GLFW.GLFW_RELEASE -> release();
                }
            }
            case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> {
                switch (event.getAction()) {
                    case GLFW.GLFW_PRESS -> rightPress(event);
                    case GLFW.GLFW_RELEASE -> release();
                }
            }
        }
    }

    private static void leftPress(InputEvent.MouseButton.Pre event) {
        if (!CameraAnimIdeCache.EDIT) {
            return;
        }
        // 编辑模式下，阻断鼠标点击事件
        event.setCanceled(true);
        CameraAnimIdeCache.leftPick(playerEyePos(), playerView(), 6);
    }

    private static void rightPress(InputEvent.MouseButton.Pre event) {
        if (!CameraAnimIdeCache.EDIT) {
            return;
        }

        event.setCanceled(true);
        CameraAnimIdeCache.rightPick(playerEyePos(), playerView(), 6);
    }

    private static void release() {
        if (!CameraAnimIdeCache.EDIT) {
            return;
        }

        switch (CameraAnimIdeCache.getMode()) {
            case NONE -> {
            }
            case MOVE -> {
                CameraAnimIdeCache.MoveModeData moveData = CameraAnimIdeCache.getMoveMode();

                if (moveData.getMoveType() != CameraAnimIdeCache.MoveType.NONE) {
                    moveData.reset();
                }
            }
        }
    }
}
