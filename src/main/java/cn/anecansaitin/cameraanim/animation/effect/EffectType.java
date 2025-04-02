package cn.anecansaitin.cameraanim.animation.effect;

public sealed interface EffectType<T> permits EffectRegister.InternalEffectType {
    String getId();
    Class<T> getType();
}
