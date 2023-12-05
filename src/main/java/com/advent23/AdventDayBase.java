package com.advent23;

import com.advent23.helper.Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AdventDayBase implements Solvable {
    protected final ArrayDeque<String> lines;

    public AdventDayBase(String fileName) {
        InputStream in = AdventDayBase.class.getClassLoader().getResourceAsStream(fileName);
        OutputStreamWriter out = new OutputStreamWriter(System.out);
        Helper help = new Helper(new BufferedReader(new InputStreamReader(in)), new PrintWriter(out));
        ArrayDeque<String> result = null;
        try {
            result = AdventDayBase.parse(help);
        } catch (IOException e) {
            result =  new ArrayDeque<>();
        }
        this.lines = result;
    }

    public static ArrayDeque<String> parse(final Helper help) throws IOException {
        String line;
        ArrayDeque<String> lines = new ArrayDeque<>();
        while((line = help.next())!= null) {
            lines.add(line);
        }
        return lines;
    }

    public static List<Integer> asInt(String line) {
        final String[] split = line.split("\\s+");
        return Arrays.stream(split).map(Integer::parseInt).collect(Collectors.toList());
    }

    public static List<Long> asLong(String line) {
        final String[] split = line.split("\\s+");
        return Arrays.stream(split).map(Long::parseLong).collect(Collectors.toList());
    }
}
