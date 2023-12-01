package com.advent23;

import java.io.IOException;

/**
 * @author Agustin Cabra on 12/1/23.
 * @since
 */
public class Solver {

    public static void main(String[] args) throws IOException {
        System.out.println("P1:" + Problem1.ofTestFile("input_p1.txt").solve());
        System.out.println("P2:" + Problem1.ofTestFile("input_p2.txt").solveText());
    }
}
