package com.advent23.helper;

import com.advent23.Day3;

public record Pair<L, R>(L left, R right) {
    public static Pair<String, String> fromNode(String node) {
        return new Pair<>(node.substring(1, 4), node.substring(6, node.length()-1));
    }

    public String getStr(char next) {
        return next == 'L' ? left.toString() : right.toString();
    }
}