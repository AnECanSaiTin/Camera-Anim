package cn.anecansaitin.cameraanim.animation.effect.type;

import cn.anecansaitin.cameraanim.animation.effect.IPropertyEffect;

public sealed interface IEffectType<T> permits EffectRegister.InternalEffectType {
    String id();
    IPropertyEffect<T> create();
}
