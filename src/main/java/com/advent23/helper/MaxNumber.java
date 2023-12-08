package com.advent23.helper;

public class MaxNumber {
    private long value;

    public MaxNumber() {
        this.value = Long.MIN_VALUE;
    }

    public void update(long check) {
        value = Math.max(value, check);
    }

    public long getValue() {
        return value;
    }
}
