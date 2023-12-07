package com.advent23.helper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AdventRangeGrinder implements Runnable {
    public static final boolean REPORT_PROGRESS = false;
    private final CompletableFuture<Long> cf;
    private final Range seeds;
    private final long id;
    private final int totalSize;
    private final Function<Long, Long> ag;
    private final MinNumber min;

    public AdventRangeGrinder(Function<Long, Long> command, Range range, long idx, int size) {
        this.cf = new CompletableFuture<>();
        this.seeds = range;
        this.ag = command;
        this.id = idx;
        this.totalSize = size;
        this.min = new MinNumber();
    }

    @Override
    public void run() {
        try {
            for (Long seed : seeds.iterable()) {
                min.update(this.ag.apply(seed));
            }
            if (REPORT_PROGRESS) {
                System.out.printf(", %.2f %s",((this.id*1.0) / (this.totalSize * 1.0)) * 100, "%");
            }
            this.cf.complete(min.getValue());
        } catch (Throwable e) {
            this.cf.completeExceptionally(e);
        }
    }

    public CompletableFuture<Long> getCF() {
        return this.cf;
    }
}