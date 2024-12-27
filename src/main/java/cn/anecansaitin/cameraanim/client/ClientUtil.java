package cn.anecansaitin.cameraanim.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public final class ClientUtil {
    private static final Vector3f EYE_POS = new Vector3f();
    private static final Vector3f VIEW = new Vector3f();

    public static LocalPlayer player() {
        return Minecraft.getInstance().player;
    }

    public static float partialTicks() {
        return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
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
}
