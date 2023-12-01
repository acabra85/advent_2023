package com.advent23;

import com.advent23.helper.Helper;

import java.io.*;
import java.util.ArrayDeque;
import java.util.InputMismatchException;
import java.util.IntSummaryStatistics;

public class Problem1 {
    private final Helper help;

    public Problem1(InputStream in, OutputStreamWriter out) {
        this.help = new Helper(new BufferedReader(new InputStreamReader(in)), new PrintWriter(out));
    }

    public static void main(String... args) throws IOException {
        System.out.println("solution:" + Problem1.ofTestFile("input_p1.txt").solve());
    }

    private long solve() throws IOException {
        IntSummaryStatistics is = new IntSummaryStatistics();
        String next;
        int first = -1;
        int last = -1;
        char[] charArray;
        while ((next = this.help.next()) != null) {
            charArray = next.toCharArray();
            for (char c : charArray) {
                if (Character.isDigit(c)) {
                    first = (c - 48) * 10;
                    break;
                }
            }
            for (int i = charArray.length - 1; i >= 0; --i) {
                char c = charArray[i];
                if (Character.isDigit(c)) {
                    last = c - 48;
                    break;
                }
            }
            if (first >= 0 && last >= 0) {
                is.accept(first  + last);
            } else {
                throw new InputMismatchException("expected positive digits");
            }
            first = -1;
            last = -1;
        }
        return is.getSum();
    }

    public static Problem1 ofTestFile(String fileName) {
        InputStream resourceAsStream = Problem1.class.getClassLoader().getResourceAsStream(fileName);
        return new Problem1(resourceAsStream, new OutputStreamWriter(System.out));
    }
}
