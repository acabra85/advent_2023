package com.advent23.helper;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Stream;

public class MinReducer<T> {
    private final Function<Stream<T>, Optional<T>> command;
    private final T vDefault;

    public MinReducer(Function<Stream<T>, Optional<T>> command, T vDefault) {
        this.command = command;
        this.vDefault = vDefault;
    }

    public T getMin(ArrayList<CompletableFuture<T>> cfs) throws InterruptedException, ExecutionException {
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
                            final Stream<T> tStream = cfs.stream()
                                    .map(CompletableFuture::join);
                            return command.apply(tStream).get();
                        }
                ).exceptionally(err -> {
                    System.out.println("error: " + err);
                    return vDefault;
                })
                .get();
    }
}
