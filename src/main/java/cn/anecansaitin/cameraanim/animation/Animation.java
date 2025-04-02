package cn.anecansaitin.cameraanim.animation;

import cn.anecansaitin.cameraanim.animation.effect.EffectType;
import cn.anecansaitin.cameraanim.animation.effect.IEffect;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Animation {
    private final ArrayList<Pair<String, IEffect<?>>> effects;
    private final HashMap<String, IEffect<?>> effectMap;

    public Animation() {
        effects = new ArrayList<>();
        effectMap = new HashMap<>();
    }

    public void animationTick(int time, float t) {
        for (Pair<String, IEffect<?>> effect : effects) {
            effect.right().apply(time, t, this);
        }
    }

    @Nullable
    public <T> IEffect<T> getEffect(int index, EffectType<T> type) {
        var effect = effects.get(index);

        if (effect == null || !type.getType().isAssignableFrom(effect.right().getType())) {
            return null;
        }

        return (IEffect<T>) effect.right();
    }

    public <T> IEffect<T> getEffect(String id, EffectType<T> type) {
        var effect = effectMap.get(id);

        if (effect == null || !type.getType().isAssignableFrom(effect.getType())) {
            return null;
        }

        return (IEffect<T>) effect;
    }

    public <T> boolean addEffect(int index, String id, IEffect<T> effect) {
        if (effectMap.containsKey(id)) {
            return false;
        }

        if (index < 0) {
            return false;
        }

        if (index >= effects.size()) {
            effects.add(Pair.of(id, effect));
            effectMap.put(id, effect);
            return true;
        }

        effects.add(index, Pair.of(id, effect));
        effectMap.put(id, effect);
        return true;
    }

    public <T> boolean addEffect(String id, IEffect<T> effect) {
        return addEffect(effects.size(), id, effect);
    }

    public <T> boolean removeEffect(int index) {
        if (index < 0 || index >= effects.size()) {
            return false;
        }

        var effect = effects.get(index);
        effectMap.remove(effect.left());
        effects.remove(index);
        return true;
    }

    public <T> boolean removeEffect(String id) {
        var effect = effectMap.get(id);

        if (effect == null) {
            return false;
        }

        var index = effects.indexOf(Pair.of(id, effect));

        if (index < 0) {
            return false;
        }

        effects.remove(index);
        effectMap.remove(id);
        return true;
    }

    public <T> boolean renameEffect(int index, String id) {
        if (index < 0 || index >= effects.size()) {
            return false;
        }

        var effect = effects.get(index);
        effectMap.remove(effect.left());
        effectMap.put(id, effect.right());
        effects.set(index, Pair.of(id, effect.right()));
        return true;
    }

    public <T> boolean renameEffect(String oldId, String newId) {
        var effect = effectMap.get(oldId);

        if (effect == null) {
            return false;
        }

        var index = effects.indexOf(Pair.of(oldId, effect));

        if (index < 0) {
            return false;
        }

        effectMap.remove(oldId);
        effectMap.put(newId, effect);
        effects.set(index, Pair.of(newId, effect));
        return true;
    }

    public <T> boolean swapEffect(int index, int newIndex) {
        if (index < 0 || index >= effects.size() || newIndex < 0 || newIndex >= effects.size() || index == newIndex) {
            return false;
        }

        effects.set(newIndex, effects.set(index, effects.get(newIndex)));
        return true;
    }
}
