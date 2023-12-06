package com.advent23.helper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AdventRangeGrinder<T> implements Runnable{
    private static boolean REPORT_PROGRESS = false;
    private final CompletableFuture<T> cf;
    private final Iterable<T> seeds;
    private final long id;
    private final int totalSize;
    private final Function<T, T> ag;
    private final MinNumber<T> min;

    public AdventRangeGrinder(Function<T, T> command, Iterable<T> seeds, MinNumber<T> min, long idx, int size) {
        this.cf = new CompletableFuture<>();
        this.seeds = seeds;
        this.ag = command;
        this.id = idx;
        this.totalSize = size;
        this.min = min;
    }

    public static void seeResults() {
        REPORT_PROGRESS = true;
    }

    @Override
    public void run() {
        try {
            for (T seed : seeds) {
                final T apply = this.ag.apply(seed);
                min.update(apply);
            }
            if (REPORT_PROGRESS) {
                System.out.printf("\n %.2f %s",((this.id*1.0) / (this.totalSize * 1.0)) * 100, "%");
            }
            this.cf.complete(min.getValue());
        } catch (Throwable e) {
            this.cf.completeExceptionally(e);
        }
    }

    public CompletableFuture<T> getCF() {
        return this.cf;
    }
}