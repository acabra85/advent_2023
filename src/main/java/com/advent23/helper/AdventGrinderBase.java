package com.advent23.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 *
 */
public abstract class AdventGrinderBase<A, C> implements Runnable {
    private final CompletableFuture<C> cf;
    protected final List<A> params;
    protected final Function<A, C> command;
    private final Function<List<C>, C> merger;

    protected AdventGrinderBase(Function<A, C> command, List<A> params, Function<List<C>, C> merger) {
        this.cf = new CompletableFuture<>();
        this.params = params;
        this.command = command;
        this.merger = merger;
    }

    public CompletableFuture<C> getCF() {
        return cf;
    }

    @Override
    public void run() {
        try {
            ArrayList<C> partialResults = new ArrayList<>();
            if (params.size() == 1) {
                this.cf.complete(this.command.apply(params.getFirst()));
                return;
            }
            for (A param : params) {
                partialResults.add(this.command.apply(param));
            }
            this.cf.complete(this.merger.apply(partialResults));
        } catch (Throwable t) {
            this.cf.completeExceptionally(t);
        }
    }

}
