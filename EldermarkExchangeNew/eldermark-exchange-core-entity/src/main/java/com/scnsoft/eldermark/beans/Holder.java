package com.scnsoft.eldermark.beans;

import java.util.function.Supplier;

public class Holder<T> implements Supplier<T> {
    private T value;

    public void set(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }
}
