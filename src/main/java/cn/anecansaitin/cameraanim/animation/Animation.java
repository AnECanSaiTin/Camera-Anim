package cn.anecansaitin.cameraanim.animation;

import cn.anecansaitin.cameraanim.animation.effect.EffectType;
import cn.anecansaitin.cameraanim.animation.effect.IEffect;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class Animation {
    private final HashMap<String, IEffect<?>> effects;

    public Animation () {
        effects = new HashMap<>();
    }

    public void animationTick(int time, float t) {
        for (IEffect<?> effect : effects.values()) {
            effect.apply(time, t, this);
        }
    }

    @Nullable
    public <T> IEffect<T> getEffect(EffectType<T> type) {
        IEffect<?> effect = effects.get(type.getId());

        if (effect == null || !type.getType().isAssignableFrom(effect.getType())) {
            return null;
        }

        return (IEffect<T>) effect;
    }

    public <T> boolean addEffect(IEffect<T> effect, EffectType<T> type) {
        if (effects.containsKey(type.getId())) {
            return false;
        }

        effects.put(type.getId(), effect);
        return true;
    }
}
