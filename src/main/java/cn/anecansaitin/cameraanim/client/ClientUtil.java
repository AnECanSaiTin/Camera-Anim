package cn.anecansaitin.cameraanim.client;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public final class ClientUtil {
    private static final Minecraft MC = Minecraft.getInstance();
    private static final Vector3f EYE_POS = new Vector3f();
    private static final Vector3f VIEW = new Vector3f();
    private static CameraType cameraType = CameraType.FIRST_PERSON;
    private static boolean bobView = false;

    public static LocalPlayer player() {
        return MC.player;
    }

    public static float partialTicks() {
        return MC.getDeltaTracker().getGameTimeDeltaPartialTick(true);
    }

    public static Vector3f playerEyePos() {
        float partialTicks = partialTicks();
        LocalPlayer player = player();
        double x = Mth.lerp(partialTicks, player.xo, player.getX());
        double y = Mth.lerp(partialTicks, player.yo, player.getY()) + (double) player.getEyeHeight();
        double z = Mth.lerp(partialTicks, player.zo, player.getZ());
        return EYE_POS.set(x, y, z);
    }

    public static Vector3f playerView() {
        LocalPlayer player = player();
        float f = player.getXRot() * Mth.DEG_TO_RAD;
        float f1 = -player.getYRot() * Mth.DEG_TO_RAD;
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return VIEW.set(f3 * f4, -f5, f2 * f4);
    }

    public static float playerYHeadRot() {
        return Mth.wrapDegrees(player().getYHeadRot());
    }

    public static float playerXRot() {
        return Mth.wrapDegrees(player().getXRot());
    }

    public static boolean hideGui() {
        return MC.options.hideGui;
    }

    public static boolean gamePaused() {
        return MC.isPaused();
    }

    public static void pushGuiLayer(Screen screen) {
        MC.pushGuiLayer(screen);
    }

    public static void popGuiLayer() {
        MC.popGuiLayer();
    }

    public static void toThirdView() {
        Options options = MC.options;
        cameraType = options.getCameraType();
        options.setCameraType(CameraType.THIRD_PERSON_BACK);
    }

    public static void resetCameraType() {
        MC.options.setCameraType(cameraType);
    }

    public static Font font() {
        return MC.font;
    }

    /**
     * Disable view bobbing to avoid annoying camera movements.
     * Stores the current value to allow restoration later.
     */
    public static void disableBobView() {
        Options options = MC.options;
        bobView = options.bobView().get();
        options.bobView().set(false);
    }

    /**
     * Resets the view bobbing setting to the previously stored value.
     * Typically used to revert changes made by {@link #disableBobView()}.
     */
    public static void resetBobView() {
        MC.options.bobView().set(bobView);
    }
}