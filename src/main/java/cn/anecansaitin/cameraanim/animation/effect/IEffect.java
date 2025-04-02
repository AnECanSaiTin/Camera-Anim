package cn.anecansaitin.cameraanim.animation.effect;

import cn.anecansaitin.cameraanim.animation.Animation;

public interface IEffect<T> {
    void apply(int time, float t, Animation animation);

    boolean enabled();

    void setEnabled(boolean enabled);

    Class<T> getType();
}
