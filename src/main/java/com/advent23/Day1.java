package com.advent23;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day1 extends AdventDayBase {

    public Day1(String fileName) {
        super(fileName);
    }

    @Override
    public AdventResult solve() {
        IntSummaryStatistics is = new IntSummaryStatistics();
        int first = -1;
        int last = -1;
        char[] charArray;
        for (String line: this.lines) {
            charArray = line.toCharArray();
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
                throw new InputMismatchException("expected positive digits: %s, %s %s".formatted(first, last, line));
            }
            first = -1;
            last = -1;
        }
        return AdventResult.ofStr(is.getSum());
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

    @Override
    public AdventResult solvePart2() {
        IntSummaryStatistics is = new IntSummaryStatistics();
        char[] charArray;
        ArrayDeque<Integer> q;
        for (String next : this.lines) {
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
        return AdventResult.ofLong(is.getSum());
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
