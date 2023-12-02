package com.advent23;

import java.io.IOException;

public class Day3 extends ProblemBase {
    public Day3(String fileName) {
        super(fileName);
    }

    @Override
    public AdventResult solve() throws IOException {
        return AdventResult.ofStr(this.help.next());
    }
}
