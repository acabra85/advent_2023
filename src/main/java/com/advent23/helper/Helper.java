package com.advent23.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class Helper {final BufferedReader bf;
    final PrintWriter out;
    StringTokenizer tokenizer;

    public Helper(BufferedReader bf, PrintWriter out) {
        this.bf = bf;
        this.out = out;
    }

    public int nInt() throws IOException {
        return Integer.parseInt(next());
    }

    public long nLong() throws IOException {
        return Long.parseLong(next());
    }

    public double nDouble() throws IOException {
        return Double.parseDouble(next());
    }

    public String next() throws IOException {
        String s = bf.readLine();
        return s;
    }

    public void close() throws IOException {
        bf.close();
        out.flush();
    }
}
