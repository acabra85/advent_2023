package com.advent23.helper;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class MultReducer {
    public MultReducer() {
    }

    public Long getLong(ArrayList<CompletableFuture<Long>> cfs) throws Exception {
        final CompletableFuture<Void> failFast = CompletableFuture.allOf(cfs.toArray(new CompletableFuture[cfs.size()]));
        final CompletableFuture<?> failure = new CompletableFuture<>();
        cfs.forEach(f -> f.exceptionally(ex -> {
            failure.completeExceptionally(ex);
            return null;
        }));
        failure.exceptionally(ex -> {
            cfs.forEach(f -> f.cancel(true));
            return null;
        });
        return CompletableFuture.anyOf(failure, failFast)
                .thenApply(
                        v -> {
                            return cfs.stream()
                                    .map(CompletableFuture::join)
                                    .reduce(1L, (a, b) -> a * b);
                        }
                ).exceptionally(err -> {
                    System.out.println("error: " + err);
                    return 0L;
                })
                .get();
    }
}