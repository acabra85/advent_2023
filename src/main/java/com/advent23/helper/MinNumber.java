package com.advent23.helper;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class MinNumber<T> {
    private final BiFunction<T, T, T> min;
    private T value;
    public MinNumber(T maxValue, BiFunction<T, T, T> min) {
        value = maxValue;
        this.min = min;
    }

    public void update(T check) {
        value = this.min.apply(value, check);
    }

    public T getValue() {
        return value;
    }
}
