package cn.anecansaitin.cameraanim.animation.effect;

import cn.anecansaitin.cameraanim.animation.effect.type.IEffectType;

public abstract class AbstractPropertyEffect<T> implements IPropertyEffect<T> {
    private boolean enabled;
    private final IEffectType<T> type;

    public AbstractPropertyEffect(IEffectType<T> type) {
        enabled = true;
        this.type = type;
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public IEffectType<T> getType() {
        return type;
    }
}
