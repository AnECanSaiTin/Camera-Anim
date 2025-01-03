package cn.anecansaitin.cameraanim.common.animation;

import net.minecraft.network.chat.Component;

public enum TimeInterpolator {
    LINEAR(0),
    BEZIER(1);

    public final int index;

    TimeInterpolator(int index) {
        this.index = index;
    }

    public Component getDisplayName() {
        return Component.translatable(getDisplayNameKey());
    }

    public String getDisplayNameKey() {
        return "camera_anim.time_interpolator." + name().toLowerCase();
    }

    public static TimeInterpolator fromIndex(int index) {
        return switch (index) {
            case 0 -> LINEAR;
            case 1 -> BEZIER;
            default -> LINEAR;
        };
    }
}
