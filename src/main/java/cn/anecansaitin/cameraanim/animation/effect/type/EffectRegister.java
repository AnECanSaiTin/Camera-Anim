package cn.anecansaitin.cameraanim.animation.effect.type;

import cn.anecansaitin.cameraanim.animation.effect.IPropertyEffect;

import java.util.HashMap;
import java.util.function.Supplier;

public final class EffectRegister {
    private static final HashMap<String, IEffectType<?>> effects = new HashMap<>();

    record InternalEffectType<T> (String id, Supplier<IPropertyEffect<T>> supplier) implements IEffectType<T> {
        @Override
        public String id() {
            return id;
        }

        @Override
        public IPropertyEffect<T> create() {
            return supplier().get();
        }
    }

    public static <T> IEffectType<T> register(String id, Supplier<IPropertyEffect<T>> supplier) {
        if (effects.containsKey(id)) {
            throw new IllegalArgumentException("Effect with id " + id + " already registered");
        }

        IEffectType<T> effectType = new InternalEffectType<>(id, supplier);
        effects.put(id, effectType);
        return effectType;
    }

    public static IEffectType<?> get(String id) {
        return effects.get(id);
    }
}
