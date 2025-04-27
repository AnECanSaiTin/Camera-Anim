package cn.anecansaitin.cameraanim.animation.effect;

import cn.anecansaitin.cameraanim.animation.effect.type.IEffectType;
import cn.anecansaitin.freecameraapi.ICameraModifier;

public interface IPropertyEffect<T> {
    void apply(int time, float t, ICameraModifier camera);

    boolean enabled();

    void setEnabled(boolean enabled);

    IEffectType<T> getType();
}
