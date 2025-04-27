package cn.anecansaitin.cameraanim.animation.effect;

import cn.anecansaitin.freecameraapi.ICameraModifier;

@FunctionalInterface
public interface IEffectApplier<T> {
    void apply(int time, float t, ICameraModifier camera, T data);
}
