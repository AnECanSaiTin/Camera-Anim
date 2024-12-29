package cn.anecansaitin.cameraanim.common.animation;

import net.minecraft.network.chat.Component;

public enum PointInterpolationType {
    LINEAR,
    SMOOTH,
    BEZIER,
    STEP;

    public Component getDisplayName() {
        return Component.translatable(getDisplayNameKey());
    }

    public String getDisplayNameKey() {
        return "camera_anim.point_interpolation_type." + name().toLowerCase();
    }
}
