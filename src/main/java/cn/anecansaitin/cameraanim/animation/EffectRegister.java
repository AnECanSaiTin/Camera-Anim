package cn.anecansaitin.cameraanim.animation;

import java.util.HashMap;

public final class EffectRegister {
    private final HashMap<String, EffectType<?>> effects = new HashMap<>();
    record InternalEffectType<T> (String id, Class<T> clazz) implements EffectType<T> {
        @Override
        public String getId() {
            return id;
        }

        @Override
        public Class<T> getType() {
            return clazz;
        }
    }

    public <T> EffectType<T> register(String id, Class<T> clazz) {
        if (effects.containsKey(id)) {
            throw new IllegalArgumentException("Effect with id " + id + " already registered");
        }

        EffectType<T> type = new InternalEffectType<>(id, clazz);
        effects.put(id, type);
        return type;
    }

    public EffectType<?> get(String id) {
        return effects.get(id);
    }
}
