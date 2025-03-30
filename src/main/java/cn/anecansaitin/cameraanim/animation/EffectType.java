package cn.anecansaitin.cameraanim.animation;

public sealed interface EffectType<T> permits EffectRegister.InternalEffectType {
    String getId();
    Class<T> getType();
}
