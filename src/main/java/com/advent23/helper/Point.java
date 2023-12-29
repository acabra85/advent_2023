package com.advent23.helper;

/**
 *
 */
public record Point(int x, int y) {
    public static Point of(int x, int y) {
        return new Point(x, y);
    }

    public long manhattan(Point g) {
        return Math.abs(g.x - x) + Math.abs(g.y - y);
    }
}
