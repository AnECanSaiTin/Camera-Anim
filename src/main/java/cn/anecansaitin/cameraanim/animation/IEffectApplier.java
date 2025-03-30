package cn.anecansaitin.cameraanim.animation;

@FunctionalInterface
public interface IEffectApplier {
    void apply(int time, float t, Animation animation);
}
