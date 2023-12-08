package com.advent23.helper;

import com.advent23.Day8;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CompletableHelper {

    public static List<Day8.GhostGrinderState> getGhostGrinders(
            ArrayList<CompletableFuture<Day8.GhostGrinderState>> cfs
    ) throws Exception {
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
                                    .collect(Collectors.toList());
                        }
                ).exceptionally(err -> {
                    System.out.println("error: " + err);
                    return new ArrayList<>();
                })
                .get();
    }
}
