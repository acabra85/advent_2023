package com.advent23;

import com.advent23.helper.Helper;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Problem1 {
    private final Helper help;

    public Problem1(InputStream in, OutputStreamWriter out) {
        this.help = new Helper(new BufferedReader(new InputStreamReader(in)), new PrintWriter(out));
    }

    public long solve() throws IOException {
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

    static Map<String, Integer> StrToNum = new HashMap<>(){{
       put("zero", 0);

       put("one", 1);

       put("two", 2);
       put("three", 3);

       put("four", 4);
       put("five", 5);

       put("six", 6);
       put("seven", 7);

       put("eight", 8);
       put("nine", 9);
    }};

    static Set<NumberKey> NUMS = StrToNum.entrySet().stream()
            .map((k) -> new NumberKey(k.getKey(), k.getKey().toCharArray(), k.getKey().length(), StrToNum.get(k.getKey())))
            .collect(Collectors.toSet());

    static Set<Character> START_LETTERS = NUMS.stream().map((k) -> k.arr[0]).collect(Collectors.toSet());

    record NumberKey(String name, char[] arr, int size, int number) {}
    public long solveText() throws IOException {
        IntSummaryStatistics is = new IntSummaryStatistics();
        String next;
        char[] charArray;
        ArrayDeque<Integer> q;
        while ((next = this.help.next()) != null) {
            q = new ArrayDeque<>();
            charArray = next.toLowerCase().toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                char c = charArray[i];
                if (Character.isDigit(c)) {
                    q.add(c - 48);
                }
                else if (START_LETTERS.contains(c)) {
                    NumberKey numberKey = readNumber(charArray, charArray.length, i);
                    if (numberKey != null) {
                        q.add(numberKey.number());
                    }
                }
            }
            int value = (q.getFirst() * 10) + q.getLast();
            is.accept(value);
        }
        return is.getSum();
    }

    private static NumberKey readNumber(char[] charArray, int n, int pos) {
        int capacity = n - pos;
        for (NumberKey num : NUMS) {
            if (capacity >= num.size() && isEqualNumber(charArray, pos+1, num.size(), num.arr())) {
                return num;
            }
        }
        return null;
    }

    private static boolean isEqualNumber(char[] charArray, int pos, int size, char[] numberToCheck) {
        for (int i = pos,j=1; j < size; ++i, ++j) {
            if(charArray[i] != numberToCheck[j]) {
                return false;
            }
        }
        return true;
    }
}
