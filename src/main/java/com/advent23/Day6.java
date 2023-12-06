package com.advent23;

import com.advent23.helper.AdventGrinderBase;
import com.advent23.helper.MultReducer;
import com.advent23.helper.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class Day6 extends AdventDayBase {

    private static final Function<List<Long>, Long> MULTIPLY_ELEMENTS = (s) -> s.stream().reduce(1L, (a, b) -> a*b);

    public Day6(String fileName) {
        super(fileName);
    }

    @Override
    public AdventResult solve() throws Exception {
        long[] timeHolding = AdventDayBase.toLongArray(lines.removeFirst(), Optional.of(1));
        long[] recordDistance = AdventDayBase.toLongArray(lines.removeFirst(), Optional.of(1));
        try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            ArrayList<CompletableFuture<Long>> cfs = new ArrayList<>(timeHolding.length);
            for (int i = 0; i < timeHolding.length; ++i) {
                AdventGrinderBase<Pair<Long, Long>, Long> brg = buildGrinder(timeHolding[i], recordDistance[i]);
                cfs.add(brg.getCF());
                CompletableFuture.runAsync(brg, executor);
            }
            return AdventResult.ofLong(new MultReducer().getLong(cfs));
        }
    }

    private Long howManyWays(Pair<Long, Long> pair) {
        long raceDuration = pair.left();
        long toBeat = pair.right();
        long ways = 0L;
        for (int i = 1; i < raceDuration; ++i) {
            long record = getDistance(raceDuration - i, raceDuration);
            if (record > toBeat) {
                ++ways;
            }
        }
        return ways;
    }

    private long getDistance(long holding, long raceDuration) {
        long remainingDistance = raceDuration - holding;
        return holding * remainingDistance;
    }

    @Override
    public AdventResult solvePart2() throws Throwable {
        Long timeHolding = toLong(asJoinedString(lines.removeFirst(), Optional.empty(), Optional.of(1)));
        Long recordDistance = toLong(asJoinedString(lines.removeFirst(), Optional.empty(), Optional.of(1)));
        try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            ArrayList<CompletableFuture<Long>> cfs = new ArrayList<>(1);
            final AdventGrinderBase<Pair<Long, Long>, Long> brg = buildGrinder(timeHolding, recordDistance);
            cfs.add(brg.getCF());
            CompletableFuture.runAsync(brg, executor);
            return AdventResult.ofLong(new MultReducer().getLong(cfs));
        }
    }

    private AdventGrinderBase<Pair<Long, Long>, Long> buildGrinder(Long timeHolding, Long recordDistance) {
        return new AdventGrinderBase<>(
                this::howManyWays,
                List.of(new Pair<>(timeHolding, recordDistance)),
                MULTIPLY_ELEMENTS) {};
    }
}
