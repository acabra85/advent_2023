package com.advent23;

import com.advent23.helper.MultReducer;
import com.advent23.helper.Pair;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

public class Day6 extends AdventDayBase {

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
                final BoatRecordGrinder brg =
                        new BoatRecordGrinder(this::howManyWays, new Pair<>(timeHolding[i], recordDistance[i]));
                cfs.add(brg.getCF());
                CompletableFuture.runAsync(brg, executor);
            }
            return AdventResult.ofLong(new MultReducer().getLong(cfs));
        }
    }

    private Long howManyWays(long raceDuration, long toBeat) {
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
            final BoatRecordGrinder brg =
                    new BoatRecordGrinder(this::howManyWays, new Pair<>(timeHolding, recordDistance));
            cfs.add(brg.getCF());
            CompletableFuture.runAsync(brg, executor);
            return AdventResult.ofLong(new MultReducer().getLong(cfs));
        }
    }

    private class BoatRecordGrinder implements Runnable {
        private final CompletableFuture<Long> cf;
        private final BiFunction<Long, Long, Long> command;
        private final Pair<Long> params;

        public BoatRecordGrinder(BiFunction<Long, Long, Long> howManyWays, Pair<Long> params) {
            this.command = howManyWays;
            this.cf = new CompletableFuture<>();
            this.params = params;
        }

        public CompletableFuture<Long> getCF() {
            return cf;
        }

        @Override
        public void run() {
            try {
                this.cf.complete(this.command.apply(params.left(), params.right()));
            } catch (Throwable t) {
                this.cf.completeExceptionally(t);
            }
        }
    }
}
