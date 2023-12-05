package com.advent23;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface Solvable {

    AdventResult solve() throws IOException;
    AdventResult solvePart2() throws IOException, ExecutionException, InterruptedException;
}
