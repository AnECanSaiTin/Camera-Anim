package cn.anecansaitin.cameraanim.common.animation.interpolation.types;

import net.minecraft.network.chat.Component;

public enum PathInterpolator {
    LINEAR(0),
    SMOOTH(1),
    BEZIER(2),
    STEP(3);

    public final int index;

    PathInterpolator(int index) {
        this.index = index;
    }

    public Component getDisplayName() {
        return Component.translatable(getDisplayNameKey());
    }

    public String getDisplayNameKey() {
        return "camera_anim.motion_interpolator." + name().toLowerCase();
    }

    public static PathInterpolator fromIndex(int index) {
        return switch (index) {
            case 0 -> LINEAR;
            case 1 -> SMOOTH;
            case 2 -> BEZIER;
            case 3 -> STEP;
            default -> LINEAR;
        };
    }
}
