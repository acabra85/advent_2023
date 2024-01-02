package com.advent23;

import java.util.List;
import java.util.LongSummaryStatistics;

/**
 *
 */
public class Day12 extends AdventDayBase {
    public Day12(String fileName) {
        super(fileName);
    }

    @Override
    public AdventResult solve() throws Throwable {
        LongSummaryStatistics lss = new LongSummaryStatistics();
        for (String line : lines) {
            lss.accept(processLine(line.split("\\s++")));
        }
        return ofLong(lss.getSum());
    }

    private long processLine(String[] line) {
        final String springs = line[0];
        final List<Integer> groups = toIntList(line[1], ",");
        System.out.println(springs + ": " + groups);
        return 0;
    }

    @Override
    public AdventResult solvePart2() throws Throwable {
        return ofLong(-1L);
    }
}
