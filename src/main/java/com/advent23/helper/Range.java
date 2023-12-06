package com.advent23.helper;

import java.util.ArrayList;
import java.util.List;

public record Range(long start, long end, long size){
    public static List<Range> transform(List<Range> ranges, int bulkSize) {
        return ranges.stream().map(r -> {
            if (r.size() > bulkSize) {
                return r.split(bulkSize);
            }
            return List.of(r);
        }).flatMap(List::stream).toList();
    }

    public boolean belongs(long q) {
        return start <= q && q <= end;
    }

    public boolean touches(Range other) {
        return belongs(other.start) || belongs(other.end);
    }

    public void merge(Range other) {

    }

    public List<Range> split(int bulkSize) {
        long mod = size % bulkSize;
        int pieces = Double.valueOf((size * 1.0) / (bulkSize * 1.0)).intValue();
        long curr = this.start;
        long newEnd = 0;
        List<Range> ranges = new ArrayList<>(pieces + (mod > 0 ? 1 : 0));
        for (long i = 0; i < pieces; ++i) {
            newEnd = curr + bulkSize -1;
            ranges.add(new Range(curr, newEnd, bulkSize));
            curr = newEnd + 1;
        }
        if (mod > 0) {
            ranges.add(new Range(curr, curr + mod - 1, mod));
        }
        return ranges;
    }

    public List<Long> iterable() {
        List<Long> explode = new ArrayList<>((int)this.size());
        for (long seed = this.start(); seed <= this.end(); ++seed) {
            explode.add(seed);
        }
        return explode;
    }
}
