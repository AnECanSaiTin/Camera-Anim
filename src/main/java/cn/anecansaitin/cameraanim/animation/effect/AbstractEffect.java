package cn.anecansaitin.cameraanim.animation.effect;

public abstract class AbstractEffect<T> implements IEffect<T>{
    private boolean enabled;
    private final Class<T> type;

    public AbstractEffect(Class<T> type) {
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
    public Class<T> getType() {
        return type;
    }
}
