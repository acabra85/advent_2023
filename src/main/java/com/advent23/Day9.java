package com.advent23;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Day9 extends AdventDayBase {
    public Day9(String fileName) {
        super(fileName);
    }

    @Override
    public AdventResult solve() throws Throwable {
        List<Long> numbers = new ArrayList<>();
        for (String line : this.lines) {
            final List<Long> longs = AdventDayBase.toLongList(line);
            numbers.add(findNextNumber(longs));
        }
        return AdventResult.ofLong(numbers.stream().reduce(0L, Long::sum));
    }

    private Long findNextNumber(List<Long> base) {
        List<List<Long>> previous = buildLists(base);
        long sum = 0;
        for (int i = previous.size()-1; i>=0; --i) {
            sum += previous.get(i).getLast();
        }
        return sum;
    }

    private List<List<Long>> buildLists(List<Long> base) {
        List<List<Long>> previous = new ArrayList<>();
        previous.add(base);
        boolean allZeroes = false;
        while(!allZeroes) {
            allZeroes =  true;
            List<Long> list = new ArrayList<>();
            List<Long> prev = previous.getLast();
            for (int i = 0; i < prev.size() - 1; ++i) {
                list.add(prev.get(i + 1) - prev.get(i));
                allZeroes = allZeroes && list.getLast() == 0;
            }
            previous.add(list);
        }
        return previous;
    }

    @Override
    public AdventResult solvePart2() throws Throwable {
        List<Long> numbers = new ArrayList<>();
        for (String line : this.lines) {
            final List<Long> longs = AdventDayBase.toLongList(line);
            numbers.add(findPreviousNumber(longs));
        }
        return AdventResult.ofLong(numbers.stream().reduce(0L, Long::sum));
    }

    private Long findPreviousNumber(List<Long> base) {
        List<List<Long>> previous = buildLists(base);
        List<Long> nums = new ArrayList<>();
        long sum = 0;
        for (int i = previous.size()-2; i>=0; --i) {
            sum = previous.get(i).getFirst() - sum;
            nums.add(sum);
        }
        return nums.getLast();
    }
}
