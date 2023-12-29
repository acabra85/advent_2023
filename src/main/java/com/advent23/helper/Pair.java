package com.advent23.helper;

import com.advent23.Day10;
import com.advent23.Day3;

import java.util.Objects;

public record Pair<L, R>(L left, R right) {
    public static Pair<String, String> fromNode(String node) {
        return new Pair<>(node.substring(1, 4), node.substring(6, node.length()-1));
    }

    public static Pair<Integer, Integer> ofInt(int a, int b) {
        return new Pair<>(a, b);
    }

    public String getStr(char next) {
        return next == 'L' ? left.toString() : right.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return left.toString() + ":" + right.toString();
    }
}