package cn.anecansaitin.cameraanim.client;

import cn.anecansaitin.cameraanim.CameraAnim;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

@EventBusSubscriber(modid = CameraAnim.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ModKeyMapping {
    private static ArrayList<Lazy<KeyMapping>> list = new ArrayList<>();

    public static final Lazy<KeyMapping> ADD_GLOBAL_CAMERA_POINT = register(
            new KeyMapping(
                    "key." + CameraAnim.MODID + ".add_global_camera_point",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + CameraAnim.MODID
            ));

    public static final Lazy<KeyMapping> DELETE_GLOBAL_CAMERA_POINT = register(
            new KeyMapping(
                    "key." + CameraAnim.MODID + ".delete_global_camera_point",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + CameraAnim.MODID
            ));

    public static final Lazy<KeyMapping> EDIT_MODE = register(
            new KeyMapping(
                    "key." + CameraAnim.MODID + ".edit_mode",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + CameraAnim.MODID
            ));

    public static final Lazy<KeyMapping> VIEW_MODE = register(
            new KeyMapping(
                    "key." + CameraAnim.MODID + ".view_mode",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + CameraAnim.MODID
            ));

    public static final Lazy<KeyMapping> POINT_SETTING = register(
            new KeyMapping(
                    "key." + CameraAnim.MODID + ".point_setting",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + CameraAnim.MODID
            ));

    public static final Lazy<KeyMapping> PREVIEW_MODE = register(
            new KeyMapping(
                    "key." + CameraAnim.MODID + ".preview_mode",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + CameraAnim.MODID
            ));

    public static final Lazy<KeyMapping> PLAY = register(
            new KeyMapping(
                    "key." + CameraAnim.MODID + ".play",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + CameraAnim.MODID
            ));

    public static final Lazy<KeyMapping> RESET = register(
            new KeyMapping(
                    "key." + CameraAnim.MODID + ".reset",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + CameraAnim.MODID
            ));

    public static final Lazy<KeyMapping> SET_CAMERA_TIME = register(
            new KeyMapping(
                    "key." + CameraAnim.MODID + ".set_camera_time",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + CameraAnim.MODID
            ));

    public static final Lazy<KeyMapping> BACK = register(
            new KeyMapping(
                    "key." + CameraAnim.MODID + ".back",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + CameraAnim.MODID
            ));

    public static final Lazy<KeyMapping> FORWARD = register(
            new KeyMapping(
                    "key." + CameraAnim.MODID + ".forward",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + CameraAnim.MODID
            ));

    public static final Lazy<KeyMapping> MANAGER = register(
            new KeyMapping(
                    "key." + CameraAnim.MODID + ".manager",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + CameraAnim.MODID
            ));

    public static final Lazy<KeyMapping> CLEAN = register(
            new KeyMapping(
                    "key." + CameraAnim.MODID + ".clean",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + CameraAnim.MODID
            ));

    private static Lazy<KeyMapping> register(KeyMapping key) {
        Lazy<KeyMapping> lazy = Lazy.of(() -> key);
        list.add(lazy);
        return lazy;
    }

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        for (Lazy<KeyMapping> key : list) {
            event.register(key.get());
        }

        list = null;
    }
}
