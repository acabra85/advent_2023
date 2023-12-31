package com.advent23.helper;

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
