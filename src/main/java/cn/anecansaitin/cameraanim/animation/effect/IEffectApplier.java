package cn.anecansaitin.cameraanim.animation.effect;

import cn.anecansaitin.cameraanim.animation.Animation;

@FunctionalInterface
public interface IEffectApplier<T> {
    void apply(int time, float t, Animation animation, T data);
}
