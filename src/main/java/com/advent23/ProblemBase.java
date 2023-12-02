package com.advent23;

import com.advent23.helper.Helper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public abstract class ProblemBase implements Solvable {
    protected final Helper help;
    public ProblemBase(String fileName) {
        InputStream in = ProblemBase.class.getClassLoader().getResourceAsStream(fileName);
        OutputStreamWriter out = new OutputStreamWriter(System.out);
        this.help = new Helper(new BufferedReader(new InputStreamReader(in)), new PrintWriter(out));
    }
}
