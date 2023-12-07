package com.advent23.helper;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class MinNumber {
    private long value;

    public MinNumber() {
        this.value = Long.MAX_VALUE;
    }

    public void update(long check) {
        value = Math.min(value, check);
    }

    public long getValue() {
        return value;
    }
}
