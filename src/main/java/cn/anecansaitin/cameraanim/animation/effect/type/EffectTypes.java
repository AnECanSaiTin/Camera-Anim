package cn.anecansaitin.cameraanim.animation.effect.type;

import cn.anecansaitin.cameraanim.animation.effect.IPropertyEffect;
import cn.anecansaitin.cameraanim.animation.effect.PathEffect;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class EffectTypes {
    public static final IEffectType<Vector3f> PATH = register("path", PathEffect::new);

    private static <T> IEffectType<T> register(String id, Supplier<IPropertyEffect<T>> supplier) {
        return EffectRegister.register(id, supplier);
    }
}
