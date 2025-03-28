package cn.anecansaitin.cameraanim.animation.interpolaty.parameter;

public interface IParameterGetter<Data, T> {
    T getParameters(int time, Data data);
}
