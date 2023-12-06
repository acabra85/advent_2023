package com.advent23.helper;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;

public class MultReducer {

    public static final BinaryOperator<Long> LONG_BINARY_OPERATOR = (a, b) -> a * b;

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
                                    .reduce(1L, LONG_BINARY_OPERATOR);
                        }
                ).exceptionally(err -> {
                    System.out.println("error: " + err);
                    return 0L;
                })
                .get();
    }
}