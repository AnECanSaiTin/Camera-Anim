package cn.anecansaitin.cameraanim.util;

public class IntIntObjectMutTriple<T> {
    private int first;
    private int second;
    private T third;

    public IntIntObjectMutTriple(int first, int second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public int first() {
        return first;
    }

    public int second() {
        return second;
    }

    public T third() {
        return third;
    }

    public void first(int value) {
        first = value;
    }

    public void second(int value) {
        second = value;
    }

    public void third(T value) {
        third = value;
    }
}
